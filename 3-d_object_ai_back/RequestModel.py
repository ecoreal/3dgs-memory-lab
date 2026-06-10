from pydantic import BaseModel, Field, ConfigDict

class Submit3DGenRequest(BaseModel):
    """3D提交任务请求模型"""
    image_base64: str = Field(..., description="Base64编码的图片字符串")
    generate_type: str = Field("Normal", description="任务类型：Fast/Normal/LowPoly/Geometry/Sketch,若调用的是doubao则该字段无效")

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "image_base64": "data:image/png;base64,iV...",
                "generate_type": "Normal"
            }
        }
    )


class DouBaoImageGenerateRequest(BaseModel):
    """火山引擎图片生成请求模型"""
    image_base64: str = Field(..., description="Base64编码的图片字符串")
    size: str = Field("2K", description="生成图片的分辨率：2K/4K")

    model_config = ConfigDict(
        json_schema_extra={
            "image_base64": "data:image/png;base64,iVBORw...",
            "size": "2K"
        }
    )

class RTASRRequest(BaseModel):
    """语音识别请求模型"""
    audio_path: str = Field(..., description="音频文件路径")

    model_config = ConfigDict(
        json_schema_extra={
            "example": {
                "audio_path": "/path/to/audio.pcm"
            }
        }
    )