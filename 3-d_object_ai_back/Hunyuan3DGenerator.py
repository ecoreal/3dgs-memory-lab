import json
import configparser
from typing import Dict, Any, Optional
from tencentcloud.common import credential
from tencentcloud.common.exception.tencent_cloud_sdk_exception import TencentCloudSDKException
from tencentcloud.ai3d.v20250513 import ai3d_client, models
from tools import Tools


class Hunyuan3DGenerator:
    def __init__(self, config_path: str = "config.ini", region: str = "ap-guangzhou"):
        config = configparser.ConfigParser()
        config.read(config_path)
        self.secret_key = config['SECRET']['hunyuan_key']
        self.secret_id = config['SECRET']['hunyuan_id']
        cred = credential.Credential(self.secret_id, self.secret_key)
        self.client = ai3d_client.Ai3dClient(cred, region)

    def submit_job(
            self,
            image_base64: Optional[str] = None,
            model: str = "3.1",
            enable_pbr: bool = False,
            generate_type: str = "Normal"
    ) -> Dict[str, Any]:
        """
        提交混元生3D专业版任务

        Args:
            image_base64: 输入图 Base64（与 prompt/image_url 三选一）
            model: 模型版本，"3.0" 或 "3.1"
            enable_pbr: 是否开启 PBR 材质生成
            generate_type: 任务类型：Normal / LowPoly / Geometry / Sketch

        Returns:
            统一的响应结构字典
        """
        if image_base64 is None or image_base64 == "":
            return Tools._create_unified_response(
                status="FAIL",
                error_code="INVALID_PARAMETER",
                error_message="image_base64 不能为空"
            )

        params = {
            "Model": model,
            "EnablePBR": enable_pbr
        }

        try:
            params["ImageBase64"] = Tools.valid_image_base64_hunyuan(image_base64)
            params["GenerateType"] = generate_type
            req = models.SubmitHunyuanTo3DProJobRequest()
            req.from_json_string(json.dumps(params))
            resp = self.client.SubmitHunyuanTo3DProJob(req)

            print(f"[submit_job] 任务提交成功，JobId: {resp.JobId}")

            return Tools._create_unified_response(
                status="WAIT",  # 刚提交的任务状态为等待中
                job_id=resp.JobId
            )

        except ValueError as e:
            error_msg = str(e)
            print(f"[submit_job] 提交失败: {error_msg}")
            return Tools._create_unified_response(
                status="FAIL",
                error_code="VALIDATION_ERROR",
                error_message=error_msg
            )
        except TencentCloudSDKException as e:
            print(f"[submit_job] 提交失败: {e}")
            return Tools._create_unified_response(
                status="FAIL",
                error_code="API_ERROR",
                error_message=str(e)
            )
        except Exception as e:
            print(f"[submit_job] 未知错误: {e}")
            return Tools._create_unified_response(
                status="FAIL",
                error_code="UNKNOWN_ERROR",
                error_message=str(e)
            )

    def submit_job_quick(
            self,
            image_base64: Optional[str] = None,
            enable_pbr: bool = False,
            enable_geometry: bool = False
    ) -> Dict[str, Any]:
        """
        提交混元生3D极速版任务

        Args:
            image_base64: 输入图 Base64，大小 ≤6M，格式 jpg/png/jpeg/webp
                          与 image_url、prompt 三选一，且不可与 prompt 同时传入
            enable_pbr:   是否开启 PBR 材质生成，默认 False
            enable_geometry: 是否生成白模（无纹理），开启后不支持 OBJ 格式，默认输出 GLB

        Returns:
            统一的响应结构字典
        """
        if image_base64 is None or image_base64 == "":
            return Tools._create_unified_response(
                status="FAIL",
                error_code="INVALID_PARAMETER",
                error_message="image_base64 不能为空"
            )

        result_format = "GLB"  # 生成模型格式，可选 OBJ / GLB / STL / USDZ / FBX / MP4 默认返回 OBJ 格式

        params: Dict[str, Any] = {
            "EnablePBR": enable_pbr,
            "EnableGeometry": enable_geometry,
        }

        try:
            params["ImageBase64"] = Tools.valid_image_base64_hunyuan(image_base64)
            params["ResultFormat"] = result_format.upper()
            req = models.SubmitHunyuanTo3DRapidJobRequest()
            req.from_json_string(json.dumps(params))
            resp = self.client.SubmitHunyuanTo3DRapidJob(req)

            print(f"[submit_job_quick] 任务提交成功，JobId: {resp.JobId}")

            return Tools._create_unified_response(
                status="WAIT",  # 刚提交的任务状态为等待中
                job_id=resp.JobId
            )

        except ValueError as e:
            error_msg = str(e)
            print(f"[submit_job] 提交失败: {error_msg}")
            return Tools._create_unified_response(
                status="FAIL",
                error_code="VALIDATION_ERROR",
                error_message=error_msg
            )
        except TencentCloudSDKException as e:
            print(f"[submit_job_quick] 提交失败: {e}")
            return Tools._create_unified_response(
                status="FAIL",
                error_code="API_ERROR",
                error_message=str(e)
            )
        except Exception as e:
            print(f"[submit_job_quick] 未知错误: {e}")
            return Tools._create_unified_response(
                status="FAIL",
                error_code="UNKNOWN_ERROR",
                error_message=str(e)
            )

    def query_job(self, job_id: str, is_fast: bool) -> Dict[str, Any]:
        """
        查询混元生3D任务状态

        Args:
            job_id: 提交任务返回的 JobId
            is_fast: 是否为极速版任务

        Returns:
            统一的响应结构字典
        """
        if not job_id:
            return Tools._create_unified_response(
                status="FAIL",
                error_code="INVALID_PARAMETER",
                error_message="job_id 不能为空"
            )

        try:
            if is_fast:
                req = models.QueryHunyuanTo3DRapidJobRequest()
                req.JobId = job_id
                resp = self.client.QueryHunyuanTo3DRapidJob(req)
            else:
                req = models.QueryHunyuanTo3DProJobRequest()
                req.JobId = job_id
                resp = self.client.QueryHunyuanTo3DProJob(req)

            # 提取 ResultFile3Ds 中的信息
            result_files = []
            if resp.ResultFile3Ds:
                for file3d in resp.ResultFile3Ds:
                    # 处理 File3D 对象
                    file_info = {}
                    if hasattr(file3d, 'Type'):
                        file_info['Type'] = file3d.Type
                    if hasattr(file3d, 'Url'):
                        file_info['Url'] = file3d.Url
                    if hasattr(file3d, 'PreviewImageUrl'):
                        file_info['PreviewImageUrl'] = file3d.PreviewImageUrl
                    result_files.append(file_info)
            error_codet = getattr(resp, "ErrorCode", None)
            error_messaget = getattr(resp, "ErrorMessage", None)
            # 返回统一响应结构
            return Tools._create_unified_response(
                status=resp.Status,  # 使用API返回的实际状态
                job_id=job_id,
                result_file_3ds=result_files,
                error_code=error_codet if error_codet != "" else None,
                error_message=error_messaget if error_messaget != "" else None
            )

        except TencentCloudSDKException as e:
            print(f"[query_job] 查询失败: {e}")
            return Tools._create_unified_response(
                status="FAIL",
                job_id=job_id,
                error_code="QUERY_ERROR",
                error_message=str(e)
            )
        except Exception as e:
            print(f"[query_job] 未知错误: {e}")
            return Tools._create_unified_response(
                status="FAIL",
                job_id=job_id,
                error_code="UNKNOWN_ERROR",
                error_message=str(e)
            )