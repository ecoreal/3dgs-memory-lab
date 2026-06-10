# RTASRClient.py
import hashlib
import hmac
import base64
import json
import time
import threading
import urllib.parse
import logging
import uuid
import configparser
from websocket import create_connection, WebSocketException
import datetime
from typing import Optional, Dict, Any, List, Callable, Generator
from queue import Queue

FIXED_PARAMS = {
    "audio_encode": "pcm_s16le",
    "lang": "autodialect",
    "samplerate": "16000"  # 固定16k采样率，对应每40ms发送1280字节
}
AUDIO_FRAME_SIZE = 1280  # 每帧音频字节数（16k采样率、16bit位深、40ms）
FRAME_INTERVAL_MS = 40  # 每帧发送间隔（毫秒）


class RTASRClient:
    def __init__(self, config_path: str = "config.ini"):
        config = configparser.ConfigParser()
        config.read(config_path)
        self.app_id = config['RTASR']['APP_ID']
        self.access_key_id = config['RTASR']['ACCESS_KEY_ID']
        self.access_key_secret = config['RTASR']['ACCESS_KEY_SECRET']

        self.base_ws_url = "wss://office-api-ast-dx.iflyaisol.com/ast/communicate/v1"
        self.ws = None
        self.is_connected = False
        self.recv_thread = None
        self.session_id = None
        self.is_sending_audio = False
        self.audio_file_size = 0

        self.message_queue = Queue()
        self.streaming_active = False

        self.full_transcript = ""

        logging.basicConfig(level=logging.WARNING)
        logging.getLogger("websocket").setLevel(logging.WARNING)

    def _get_audio_file_size(self, audio_path: str) -> int:
        """获取音频文件大小"""
        try:
            with open(audio_path, "rb") as f:
                f.seek(0, 2)
                return f.tell()  # 返回文件大小
        except Exception as e:
            print(f"【获取文件大小失败】{str(e)}")
            return 0

    def _generate_auth_params(self) -> Dict[str, str]:
        """生成鉴权参数（严格按字典序排序，匹配Java TreeMap）"""
        auth_params = {
            "accessKeyId": self.access_key_id,
            "appId": self.app_id,
            "uuid": uuid.uuid4().hex,
            "utc": self._get_utc_time(),
            **FIXED_PARAMS
        }

        sorted_params = dict(sorted([
            (k, v) for k, v in auth_params.items()
            if v is not None and str(v).strip() != ""
        ]))
        base_str = "&".join([
            f"{urllib.parse.quote(k, safe='')}={urllib.parse.quote(v, safe='')}"
            for k, v in sorted_params.items()
        ])

        signature = hmac.new(
            self.access_key_secret.encode("utf-8"),
            base_str.encode("utf-8"),
            hashlib.sha1
        ).digest()
        auth_params["signature"] = base64.b64encode(signature).decode("utf-8")
        return auth_params

    def _get_utc_time(self) -> str:
        """生成服务端要求的UTC时间格式：yyyy-MM-dd'T'HH:mm:ss+0800"""
        beijing_tz = datetime.timezone(datetime.timedelta(hours=8))
        now = datetime.datetime.now(beijing_tz)
        return now.strftime("%Y-%m-%dT%H:%M:%S%z")

    def connect(self) -> bool:
        try:
            auth_params = self._generate_auth_params()
            params_str = urllib.parse.urlencode(auth_params)
            full_ws_url = f"{self.base_ws_url}?{params_str}"
            print(f"【连接信息】完整URL：{full_ws_url}")

            self.ws = create_connection(
                full_ws_url,
                timeout=15,
                enable_multithread=True
            )
            self.is_connected = True
            print("【连接成功】WebSocket握手完成，等待服务端就绪...")
            time.sleep(0.5)
            return True
        except WebSocketException as e:
            print(f"【连接失败】WebSocket错误：{str(e)}")
            if hasattr(e, 'status_code'):
                print(f"【服务端状态码】{e.status_code}")
            return False
        except Exception as e:
            print(f"【连接异常】其他错误：{str(e)}")
            return False

    def _process_message(self, msg_json: Dict[str, Any]):
        """
        处理接收到的消息，提取识别文本
        """
        msg_type = msg_json.get("msg_type", "")
        data = msg_json.get("data", {})

        # 处理 action 消息，获取 sessionId
        if msg_type == "action":
            if "sessionId" in data:
                self.session_id = data["sessionId"]
                print(f"【会话ID】获取到 session_id: {self.session_id}")
            return

        # 只处理语音识别结果消息
        if msg_type != "result" or msg_json.get("res_type") != "asr":
            return

        cn_data = data.get("cn", {})
        if not cn_data:
            return
        st_data = cn_data.get("st", {})
        if not st_data:
            return

        result_type = st_data.get("type", "")
        is_final = (result_type == "0")  # type为0表示最终结果

        text = ""
        rt_list = st_data.get("rt", [])

        if rt_list and len(rt_list) > 0:
            for rt_item in rt_list:
                ws_list = rt_item.get("ws", [])
                for ws_item in ws_list:
                    cw_list = ws_item.get("cw", [])
                    for cw_item in cw_list:
                        word = cw_item.get("w", "")
                        if word:
                            text += word

        if not text:
            return

        if is_final:
            self.full_transcript += text

        self.message_queue.put({
            "type": "final" if is_final else "partial",
            "text": text,
            "session_id": self.session_id,  # 这里会使用更新后的 session_id
            "is_final": is_final
        })

    def _recv_msg(self):
        """接收服务端消息（流式接收）"""
        while self.is_connected and self.ws:
            try:
                msg = self.ws.recv()
                if not msg:
                    self.message_queue.put({
                        "type": "end",
                        "session_id": self.session_id
                    })
                    break

                if isinstance(msg, str):
                    try:
                        msg_json = json.loads(msg)
                        print(f"【接收消息】{msg_json}")
                        self._process_message(msg_json)
                    except json.JSONDecodeError:
                        # 忽略非JSON消息
                        pass
                else:
                    # 忽略二进制消息
                    pass

            except WebSocketException as e:
                print(f"【接收异常】连接中断：{str(e)}")
                self.message_queue.put({
                    "type": "error",
                    "error": f"连接中断: {str(e)}",
                    "session_id": self.session_id
                })
                break
            except Exception as e:
                print(f"【接收异常】未知错误：{str(e)}")
                self.message_queue.put({
                    "type": "error",
                    "error": f"未知错误: {str(e)}",
                    "session_id": self.session_id
                })
                break

        self.message_queue.put({"type": "end"})

    def send_audio(self, audio_path: str) -> bool:
        """
        精确控制音频发送节奏
        """
        if not self.is_connected or not self.ws:
            print("【发送失败】WebSocket未连接")
            return False

        if self.is_sending_audio:
            print("【发送失败】已有发送任务在执行")
            return False

        self.is_sending_audio = True
        frame_index = 0
        start_time = None

        try:
            self.audio_file_size = self._get_audio_file_size(audio_path)

            with open(audio_path, "rb") as f:
                # 计算总帧数和预估时长
                total_frames = self.audio_file_size // AUDIO_FRAME_SIZE
                remaining_bytes = self.audio_file_size % AUDIO_FRAME_SIZE
                if remaining_bytes > 0:
                    total_frames += 1
                estimated_duration = (total_frames * FRAME_INTERVAL_MS) / 1000

                print(
                    f"【发送配置】文件大小：{self.audio_file_size}字节 | 总帧数：{total_frames} | 预估时长：{estimated_duration:.1f}秒")

                f.seek(0)

                # 循环发送音频帧
                while True:
                    chunk = f.read(AUDIO_FRAME_SIZE)
                    if not chunk:
                        print(f"【发送完成】所有音频帧发送完毕（共{frame_index}帧）")
                        break

                    # 记录第一次发送的起始时间
                    if start_time is None:
                        start_time = time.time() * 1000

                    # 计算当前帧的理论发送时间
                    expected_send_time = start_time + (frame_index * FRAME_INTERVAL_MS)

                    # 动态调整休眠
                    current_time = time.time() * 1000
                    time_diff = expected_send_time - current_time

                    if time_diff > 0.1:
                        time.sleep(time_diff / 1000)

                    # 发送当前帧
                    self.ws.send_binary(chunk)
                    frame_index += 1

                # 发送结束标记
                end_msg = {"end": True}
                if self.session_id:
                    end_msg["sessionId"] = self.session_id
                end_msg_str = json.dumps(end_msg, ensure_ascii=False)
                self.ws.send(end_msg_str)
                print(f"【发送结束】已发送标准JSON结束标记")

            return True

        except FileNotFoundError:
            print(f"【发送失败】音频文件不存在：{audio_path}")
            return False
        except PermissionError:
            print(f"【发送失败】无权限读取音频文件：{audio_path}")
            return False
        except WebSocketException as e:
            print(f"【发送失败】WebSocket连接中断：{str(e)}")
            self.close()
            return False
        except Exception as e:
            print(f"【发送异常】未知错误：{str(e)}")
            self.close()
            return False
        finally:
            self.is_sending_audio = False

    def recognize_stream(self, audio_path: str, timeout: int = 30) -> Generator[Dict[str, Any], None, None]:
        """
        执行流式语音识别，返回生成器
        """
        # 重置状态
        self.full_transcript = ""
        self.session_id = None  # 重置session_id
        self.message_queue = Queue()
        self.streaming_active = True

        try:
            # 1. 建立连接
            if not self.connect():
                yield {
                    "success": False,
                    "error": "连接失败",
                    "is_final": True
                }
                return

            # 2. 启动接收线程
            self.recv_thread = threading.Thread(target=self._recv_msg, daemon=True)
            self.recv_thread.start()

            # 3. 发送音频
            send_thread = threading.Thread(target=self.send_audio, args=(audio_path,))
            send_thread.start()

            # 4. 流式返回结果
            start_time = time.time()
            has_final_result = False

            while self.streaming_active and (time.time() - start_time) < timeout:
                try:
                    # 尝试从队列中获取消息，设置超时
                    msg = self.message_queue.get(timeout=1.0)

                    if msg["type"] == "end":
                        # 流式输出结束
                        self.streaming_active = False
                        break

                    elif msg["type"] == "error":
                        # 错误消息
                        yield {
                            "success": False,
                            "error": msg.get("error", "未知错误"),
                            "is_final": True
                        }
                        self.streaming_active = False
                        break

                    elif msg["type"] in ["partial", "final"]:
                        # 识别结果
                        result = {
                            "success": True,
                            "text": msg["text"],
                            "session_id": msg.get("session_id"),
                            "is_final": msg["type"] == "final"
                        }

                        if msg["type"] == "final":
                            has_final_result = True

                        yield result

                        # 如果是最终结果，继续等待可能还有更多结果

                except Exception:
                    # 队列超时，继续循环
                    pass

            # 5. 如果没有收到最终结果，返回当前累积的文本
            if not has_final_result and self.full_transcript:
                yield {
                    "success": True,
                    "text": self.full_transcript,
                    "session_id": self.session_id,
                    "is_final": True
                }

        except Exception as e:
            yield {
                "success": False,
                "error": str(e),
                "is_final": True
            }
        finally:
            self.close()

    def get_full_transcript(self) -> str:
        """获取完整转录文本"""
        return self.full_transcript

    def close(self):
        """安全关闭WebSocket连接"""
        self.streaming_active = False

        if self.is_connected and self.ws:
            self.is_connected = False
            try:
                if self.ws.connected:
                    self.ws.close(status=1000, reason="客户端正常关闭")
                print("【连接关闭】WebSocket已安全关闭")
            except Exception as e:
                print(f"【关闭异常】关闭时出错：{str(e)}")
        else:
            print("【连接关闭】WebSocket已断开或未初始化")