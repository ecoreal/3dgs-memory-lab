import configparser
import requests
from volcenginesdkarkruntime import Ark
from typing import Optional, Dict, Any, List
from tools import Tools


class DouBaoGenerator:
    def __init__(self, config_path: str = "config.ini"):
        config = configparser.ConfigParser()
        config.read(config_path)

        self.base_url = "https://ark.cn-beijing.volces.com/api/v3"
        self.api_key = config['SECRET']['doubao_key']

        self.image_client = Ark(
            base_url=self.base_url,
            api_key=self.api_key
        )

    def image_generation_with_base64(self, image_base64: str, prompt: str, size: str) -> Dict[str, Any]:
        """
        使用火山引擎生成图片

        Args:
            image_base64: base64编码的图片字符串
            prompt: 生成图片的提示词
            size: 生成图片的分辨率2k(1M)/4k()

        Returns:
            统一的响应结构字典
        """
        if size != '2K' and size != '4K':
            return Tools._create_unified_response(
                    status="FAIL",
                    error_code="VALIDATION_ERROR",
                    error_message="无效的分辨率参数，仅可输入2K or 4K (K大写)"
                )
        try:
            Tools.valid_image_base64_doubao(image_base64)
            imagesResponse = self.image_client.images.generate(
                model="doubao-seedream-4-5-251128",
                prompt=prompt,
                image=image_base64,
                sequential_image_generation="disabled",  # 组图
                response_format="url",
                size=size,
                stream=False,
                watermark=False
            )

            image_url = imagesResponse.data[0].url

            if hasattr(imagesResponse, 'error') and imagesResponse.error:
                error_code = imagesResponse.error.code
                error_msg = imagesResponse.error.message
                print(f"请求失败，错误码：{error_code}，错误信息：{error_msg}")
                return Tools._create_unified_response(
                    status="FAIL",
                    error_code=error_code,
                    error_message=error_msg
                )

            return Tools._create_unified_response(
                status="DONE",
                image_url=image_url
            )
        except ValueError as e:
            return Tools._create_unified_response(
                status="FAIL",
                error_code="VALIDATION_ERROR",
                error_message=str(e)
            )
        except Exception as e:
            print(f"[image_generation_with_base64] 图片生成失败: {str(e)}")
            return Tools._create_unified_response(
                status="FAIL",
                error_code="UNKNOWN_ERROR",
                error_message=str(e)
            )

    def create_3d_generation_task(self, image_base64: str) -> Dict[str, Any]:
        """
        创建3D生成任务

        Args:
            image_base64: 输入图片URL或Base64编码（格式：data:image/[格式];base64,[编码内容]）

        Returns:
            统一的响应结构字典
        """
        try:
            Tools.valid_image_base64_doubao3D(image_base64)
            subdivisionlevel = "high"  # 3D模型面数等级 high：200000面 ;medium：100000面; low：30000面，3者开销一致
            fileformat = "glb"
            url = f"{self.base_url}/contents/generations/tasks"
            headers = {
                "Content-Type": "application/json",
                "Authorization": f"Bearer {self.api_key}"
            }

            payload: Dict[str, Any] = {
                "model": "doubao-seed3d-2-0-260328",
                "content": [
                    {
                        "type": "text",
                        "text": f"--subdivisionlevel {subdivisionlevel} --fileformat {fileformat}"
                    },
                    {
                        "type": "image_url",
                        "image_url": {
                            "url": image_base64   # 此处即可接受url也可接受base64
                        }
                    }
                ]
            }
            response = requests.post(url, json=payload, headers=headers)
            response.raise_for_status()
            task_data = response.json()
            task_id = task_data.get("id")

            print(f"3D生成任务创建成功，任务ID：{task_id}")

            return Tools._create_unified_response(
                status="WAIT",  # 刚提交的任务状态为等待中
                job_id=task_id
            )
        except requests.exceptions.RequestException as e:
            print(f"任务创建失败：{str(e)}")

            error_code = "REQUEST_ERROR"
            error_message = str(e)

            if hasattr(e, 'response') and e.response:
                try:
                    error_detail = e.response.json()
                    error_code = error_detail.get("code", error_code)
                    error_message = error_detail.get("message", error_message)
                except:
                    error_message = e.response.text
                print(f"错误详情：{error_message}")

            return Tools._create_unified_response(
                status="FAIL",
                error_code=error_code,
                error_message=error_message
            )
        except ValueError as e:
            return Tools._create_unified_response(
                status="FAIL",
                error_code="VALIDATION_ERROR",
                error_message=str(e)
            )
        except Exception as e:
            print(f"任务创建异常：{str(e)}")
            return Tools._create_unified_response(
                status="FAIL",
                error_code="UNKNOWN_ERROR",
                error_message=str(e)
            )

    def get_3d_task_result(self, task_id: str) -> Dict[str, Any]:
        """
        查询3D生成任务结果（单次查询）

        Args:
            task_id: 任务ID

        Returns:
            统一的响应结构字典
        """
        if not task_id:
            return Tools._create_unified_response(
                status="FAIL",
                error_code="INVALID_PARAMETER",
                error_message="task_id 不能为空"
            )

        url = f"{self.base_url}/contents/generations/tasks/{task_id}"
        headers = {
            "Content-Type": "application/json",
            "Authorization": f"Bearer {self.api_key}"
        }

        try:
            response = requests.get(url, headers=headers)
            response.raise_for_status()
            task_info = response.json()

            # 获取任务状态
            status = task_info.get("status", "failed")

            # 火山引擎状态映射到统一状态
            status_map = {
                "succeeded": "DONE",
                "failed": "FAIL",
                "queued": "WAIT",
                "running": "RUN",
                "cancelled": "FAIL"
            }

            unified_status = status_map.get(status, "FAIL")

            result_files = None
            error_code = None
            error_message = None

            if unified_status == "DONE":
                print(f"3D模型下载地址：{task_info['content'].get('file_url')}")

                file_info = {
                    "Type": task_info.get('fileformat', 'GLB').upper(),
                    "Url": task_info['content'].get('file_url'),
                    "PreviewImageUrl": None,  # 火山引擎API不提供预览图URL
                }
                result_files = [file_info]

            elif unified_status == "FAIL":
                error = task_info.get("error", {})
                error_code = error.get("code", "API_ERROR")
                error_message = error.get("message", "未知错误")
                print(f"查询失败{error}")
                print(f"任务失败，错误码：{error_code}，错误信息：{error_message}")

            elif unified_status in ["WAIT", "RUN"]:
                print(f"任务处理中，当前状态：{status}")

            else:
                print(f"未知状态：{status}")
                error_message = f"未知状态: {status}"
                error_code = "UNKNOWN_STATUS"

            return Tools._create_unified_response(
                status=unified_status,
                job_id=task_id,
                result_file_3ds=result_files,
                error_code=error_code,
                error_message=error_message
            )

        except requests.exceptions.RequestException as e:
            print(f"查询任务失败：{str(e)}")

            error_code = "REQUEST_ERROR"
            error_message = str(e)

            if hasattr(e, 'response') and e.response:
                try:
                    error_detail = e.response.json()
                    error_code = error_detail.get("code", error_code)
                    error_message = error_detail.get("message", error_message)
                except:
                    error_message = e.response.text
                print(f"错误详情：{error_message}")

            return Tools._create_unified_response(
                status="FAIL",
                job_id=task_id,
                error_code=error_code,
                error_message=error_message
            )
        except Exception as e:
            print(f"查询任务异常：{str(e)}")
            return Tools._create_unified_response(
                status="FAIL",
                job_id=task_id,
                error_code="UNKNOWN_ERROR",
                error_message=str(e)
            )