import os
import mimetypes
import base64
from PIL import Image
from io import BytesIO
import re
from typing import List, Dict, Any, Optional

class Tools:
    @staticmethod
    def get_image_mime_type(image_path: str) -> str:
        """获取图片的MIME类型"""
        mime_type, _ = mimetypes.guess_type(image_path)

        if mime_type and mime_type.startswith('image/'):
            return mime_type

        ext = os.path.splitext(image_path)[1].lower()
        if ext in ['.jpg', '.jpeg']:
            return 'image/jpeg'
        elif ext == '.png':
            return 'image/png'
        elif ext == '.gif':
            return 'image/gif'
        elif ext == '.webp':
            return 'image/webp'
        elif ext == '.bmp':
            return 'image/bmp'
        elif ext == '.tiff':
            return 'image/tiff'

        raise ValueError(f"暂不支持图片格式: {ext}")

    @staticmethod
    def image_to_base64(image_path: str) -> str:
        """将本地图片转换为标准的Base64数据URI字符串"""
        try:
            mime_type = Tools.get_image_mime_type(image_path)

            with open(image_path, "rb") as f:
                image_data = f.read()

            base64_encoded = base64.b64encode(image_data).decode("utf-8")
            standard_base64 = f"data:{mime_type};base64,{base64_encoded}"

            return standard_base64

        except FileNotFoundError:
            raise FileNotFoundError(f"图片文件未找到: {image_path}")
        except Exception as e:
            raise Exception(f"读取图片失败: {str(e)}")

    @staticmethod
    def valid_image_base64_hunyuan(image_base64: str) -> str:
        """
        校验base64格式的图片
        Args:
            image_base64: base64编码的图片字符串
        Raises:
            ValueError: 当图片不满足校验条件时
        """
        max_size_mb = 6
        supported_formats = ['jpeg', 'jpg', 'png', 'webp']
        supported_mime_types = [f'image/{fmt}' for fmt in supported_formats]

        try:
            if not image_base64 or not isinstance(image_base64, str):
                raise ValueError("图片base64字符串不能为空或非字符串")

            if not image_base64.startswith('data:'):
                raise ValueError("图片base64字符串必须以'data:'开头")

            pattern = r'^data:([^;]+);base64,(.+)$'
            match = re.match(pattern, image_base64)

            if not match:
                raise ValueError("无效的base64格式，必须是'data:[mime];base64,[data]'格式")

            mime_type = match.group(1)
            base64_data = match.group(2)

            if mime_type not in supported_mime_types:
                raise ValueError(f"不支持的图片格式: {mime_type}, 当前仅支持 {supported_formats} ")

            try:
                image_bytes = base64.b64decode(base64_data)
            except (base64.binascii.Error, Exception) as e:
                raise ValueError(f"无效的base64数据: {str(e)}")

            file_size_bytes = len(image_bytes)
            max_size_bytes = max_size_mb * 1024 * 1024

            if file_size_bytes > max_size_bytes:
                file_size_mb = file_size_bytes / (1024 * 1024)
                raise ValueError(f"图片文件过大: {file_size_mb:.2f}MB，最大允许: {max_size_mb}MB")

            try:
                img = Image.open(BytesIO(image_bytes))
                width, height = img.size
                img.close()
            except Exception as e:
                raise ValueError(f"无法解析图片文件: {str(e)}")

            if width > 5000 or height > 5000 or width < 128 or height < 128:
                raise ValueError(f"无效的图片尺寸: {width}x{height}, 单边长度应在[128, 5000]之间")

            return base64_data

        except ValueError as e:
            raise ValueError(str(e))
        except Exception as e:
            raise ValueError(f"图片校验过程中发生未知错误: {str(e)}")

    @staticmethod
    def valid_image_base64_doubao(image_base64: str):
        """
        校验base64格式的图片
        Args:
            image_base64: base64编码的图片字符串
        Raises:
            ValueError: 当图片不满足校验条件时
        """
        max_size_mb = 10
        supported_formats = ['jpeg', 'jpg', 'bmp', 'webp', 'tiff', 'gif']
        supported_mime_types = [f'image/{fmt}' for fmt in supported_formats]

        try:
            if not image_base64 or not isinstance(image_base64, str):
                raise ValueError("图片base64字符串不能为空或非字符串")

            if not image_base64.startswith('data:'):
                raise ValueError("图片base64字符串必须以'data:'开头")

            pattern = r'^data:([^;]+);base64,(.+)$'
            match = re.match(pattern, image_base64)

            if not match:
                raise ValueError("无效的base64格式，必须是'data:[mime];base64,[data]'格式")

            mime_type = match.group(1)
            base64_data = match.group(2)

            if mime_type not in supported_mime_types:
                raise ValueError(f"不支持的图片格式: {mime_type}, 当前仅支持 {supported_formats} ")

            try:
                image_bytes = base64.b64decode(base64_data)
            except (base64.binascii.Error, Exception) as e:
                raise ValueError(f"无效的base64数据: {str(e)}")

            file_size_bytes = len(image_bytes)
            max_size_bytes = max_size_mb * 1024 * 1024

            if file_size_bytes > max_size_bytes:
                file_size_mb = file_size_bytes / (1024 * 1024)
                raise ValueError(f"图片文件过大: {file_size_mb:.2f}MB，最大允许: {max_size_mb}MB")

            try:
                img = Image.open(BytesIO(image_bytes))
                width, height = img.size
                img.close()
            except Exception as e:
                raise ValueError(f"无法解析图片文件: {str(e)}")
            tmp = width / height
            if tmp < 1/16 or tmp > 16:
                raise ValueError(f"无效的图片尺寸: {width}x{height}, 宽高比应在[1/16, 16]之间, 当前宽高比为{tmp}")
            if width < 14 or height < 14:
                raise ValueError(f"无效的图片尺寸: {width}x{height}, 单边长度应大于14之间")
            tmp = width * height
            if tmp > 36000000:
                raise ValueError(f"无效的图片尺寸: {width}x{height}, 总像素{tmp}超过阈值36000000")

        except ValueError as e:
            raise ValueError(str(e))
        except Exception as e:
            raise ValueError(f"图片校验过程中发生未知错误: {str(e)}")

    @staticmethod
    def valid_image_base64_doubao3D(image_base64: str):
        """
        校验base64格式的图片
        Args:
            image_base64: base64编码的图片字符串
        Raises:
            ValueError: 当图片不满足校验条件时
        """
        max_size_mb = 10
        supported_formats = ['jpeg', 'jpg', 'png', 'webp', 'bmp']
        supported_mime_types = [f'image/{fmt}' for fmt in supported_formats]

        try:
            if not image_base64 or not isinstance(image_base64, str):
                raise ValueError("图片base64字符串不能为空或非字符串")
            if not image_base64.startswith('data:'):
                raise ValueError("图片base64字符串必须以'data:'开头")

            pattern = r'^data:([^;]+);base64,(.+)$'
            match = re.match(pattern, image_base64)

            if not match:
                raise ValueError("无效的base64格式，必须是'data:[mime];base64,[data]'格式")

            mime_type = match.group(1)
            base64_data = match.group(2)

            if mime_type not in supported_mime_types:
                raise ValueError(f"不支持的图片格式: {mime_type}, 当前仅支持 {supported_formats} ")

            try:
                image_bytes = base64.b64decode(base64_data)
            except (base64.binascii.Error, Exception) as e:
                raise ValueError(f"无效的base64数据: {str(e)}")

            file_size_bytes = len(image_bytes)
            max_size_bytes = max_size_mb * 1024 * 1024

            if file_size_bytes > max_size_bytes:
                file_size_mb = file_size_bytes / (1024 * 1024)
                raise ValueError(f"图片文件过大: {file_size_mb:.2f}MB，最大允许: {max_size_mb}MB")

            try:
                img = Image.open(BytesIO(image_bytes))
                width, height = img.size
                img.close()
            except Exception as e:
                raise ValueError(f"无法解析图片文件: {str(e)}")
            tmp = width * height
            if tmp > 4096 * 4096:
                raise ValueError(f"无效的图片尺寸: {width}x{height}, 总像素{tmp}超过阈值16777216")

        except ValueError as e:
            raise ValueError(str(e))
        except Exception as e:
            raise ValueError(f"图片校验过程中发生未知错误: {str(e)}")

    @staticmethod
    def _create_unified_response(
            status: str = "FAIL",
            job_id: Optional[str] = None,
            result_file_3ds: Optional[List] = None,
            error_code: Optional[str] = None,
            error_message: Optional[str] = None,
            image_url: Optional[str] = None
    ) -> Dict[str, Any]:
        """
        创建统一的响应结构

        Returns:
            统一的响应字典
        """
        return {
            "Status": status,  # "WAIT"等待中, "RUN"处理中, "DONE"已完成, "FAIL"失败
            "job_id": job_id,
            "ResultFile3Ds": result_file_3ds,
            "ErrorCode": error_code,
            "ErrorMessage": error_message,
            "image_url": image_url
        }