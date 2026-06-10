import time
from fastapi import FastAPI
import uvicorn
from typing import Generator
import json
from fastapi.responses import StreamingResponse
from Hunyuan3DGenerator import Hunyuan3DGenerator
from DouBaoGenerator import DouBaoGenerator
from RTASRClient import RTASRClient
from tools import Tools
from RequestModel import Submit3DGenRequest, DouBaoImageGenerateRequest, RTASRRequest
try:
    hunyuan_3d_generator = Hunyuan3DGenerator()
    doubao_generator = DouBaoGenerator()
    rtasr_client = RTASRClient()
    print("✓ 生成器初始化成功")
except Exception as e:
    raise Exception(f"生成器初始化失败: {e}")

app = FastAPI(
    title="3dgs-AI 服务",
    version="1.2.0"
)

hunyuan_tasks = {}

# ========== API路由 ==========
@app.post("/hunyuan3d/submit")
async def hunyuan_submit_task(request: Submit3DGenRequest):
    """
    提交腾讯混元3D生成任务
    """
    try:
        available_type = ["Fast", "Normal", "LowPoly", "Geometry", "Sketch"]
        if request.generate_type not in available_type:
            return Tools._create_unified_response(
                status="FAIL",
                error_code="ValueError",
                error_message=f"不支持格式{request.generate_type}, 当前仅支持{available_type}"
            )
        if request.generate_type == "Fast":
            result = hunyuan_3d_generator.submit_job_quick(
                image_base64=request.image_base64,
                enable_pbr=False,
                enable_geometry=False
            )
        else:
            result = hunyuan_3d_generator.submit_job(
                image_base64=request.image_base64,
                model="3.1",
                enable_pbr=False,
                generate_type=request.generate_type
            )

        hunyuan_tasks[result.get('job_id')] = {
            "job_id": result.get('job_id'),
            "status": "submitted",
            "submit_time": time.time(),
            "generate_type": request.generate_type
        }

        return result

    except Exception as e:
        print(f"任务调用失败: {str(e)}")
        return Tools._create_unified_response(
            status="FAIL",
            error_code="UNKNOWN_ERROR",
            error_message=str(e)
        )

@app.get("/hunyuan3d/query/{job_id}")
async def hunyuan_query_task(job_id: str):
    """
    查询腾讯混元3D任务状态
    直接返回query_job函数的结果
    """
    try:
        result = hunyuan_3d_generator.query_job(job_id, False)

        if result and (job_id in hunyuan_tasks):
            hunyuan_tasks[job_id]["status"] = result.get("status")
            hunyuan_tasks[job_id]["last_query_time"] = time.time()

        return result

    except Exception as e:
        print(f"查询任务失败: {str(e)}")
        return Tools._create_unified_response(
            status="FAIL",
            error_code="UNKNOWN_ERROR",
            error_message=str(e)
        )

@app.get("/hunyuan3d/query_fast/{job_id}")
async def hunyuan_query_fast_task(job_id: str):
    """
    查询腾讯混元3D任务状态
    """
    try:
        result = hunyuan_3d_generator.query_job(job_id, True)

        if result and (job_id in hunyuan_tasks):
            hunyuan_tasks[job_id]["status"] = result.get("status")
            hunyuan_tasks[job_id]["last_query_time"] = time.time()

        return result

    except Exception as e:
        print(f"查询任务失败: {str(e)}")
        return Tools._create_unified_response(
            status="FAIL",
            error_code="UNKNOWN_ERROR",
            error_message=str(e)
        )

@app.post("/seedream/enhance")
async def doubao_enhance_image(request: DouBaoImageGenerateRequest):
    """
    使用火山引擎生成图片
    """
    try:
        result = doubao_generator.image_generation_with_base64(
            image_base64=request.image_base64,
            prompt="高清画质修复，修复所有模糊、噪点和压缩痕迹，必须完全继承原图的色彩模式、宽高比和画面内容完全不变",
            size=request.size
        )

        return result

    except Exception as e:
        print(f"图片生成失败: {str(e)}")
        return Tools._create_unified_response(
            status="FAIL",
            error_code="UNKNOWN_ERROR",
            error_message=str(e)
        )

@app.post("/seedream/colorize")
async def doubao_colorize_image(request: DouBaoImageGenerateRequest):
    """
    使用火山引擎生成图片
    """
    try:
        result = doubao_generator.image_generation_with_base64(
            image_base64=request.image_base64,
            prompt="基于图片内容进行专业、自然的自动上色。必须严格保持原始图片的宽高比和画面内容完全不变，输出结果必须是彩色照片。",
            size=request.size
        )

        return result

    except Exception as e:
        print(f"图片生成失败: {str(e)}")
        return Tools._create_unified_response(
            status="FAIL",
            error_code="UNKNOWN_ERROR",
            error_message=str(e)
        )

@app.post("/seed3d/submit")
async def doubao_generate_3d(request: Submit3DGenRequest):
    """
    使用火山引擎生成3D模型
    """
    try:
        result = doubao_generator.create_3d_generation_task(
            image_base64=request.image_base64
        )
        return result

    except Exception as e:
        print(f"3D模型生成失败: {str(e)}")
        return Tools._create_unified_response(
            status="FAIL",
            error_code="UNKNOWN_ERROR",
            error_message=str(e)
        )

@app.get("/seed3d/query/{job_id}")
async def doubao3d_query_task(job_id: str):
    """
    查询doubao-seed3D任务状态
    """
    try:
        result = doubao_generator.get_3d_task_result(job_id)
        return result

    except Exception as e:
        print(f"查询任务失败: {str(e)}")
        return Tools._create_unified_response(
            status="FAIL",
            error_code="UNKNOWN_ERROR",
            error_message=str(e)
        )

@app.post("/rtasr/recognize_stream")
async def rtasr_recognize_stream(request: RTASRRequest):
    """
    流式语音识别接口
    返回流式JSON响应
    """
    def generate_stream() -> Generator[str, None, None]:
        """生成流式响应"""
        try:
            for chunk in rtasr_client.recognize_stream(request.audio_path, timeout=30):
                # 将每个chunk转换为JSON字符串
                chunk_json = json.dumps(chunk, ensure_ascii=False)
                yield f"data: {chunk_json}\n\n"

                # 如果是最终结果，结束流
                if chunk.get("is_final", False):
                    break

        except Exception as e:
            error_chunk = {
                "success": False,
                "error": str(e),
                "is_final": True
            }
            yield f"data: {json.dumps(error_chunk, ensure_ascii=False)}\n\n"

    return StreamingResponse(
        generate_stream(),
        media_type="text/event-stream",
        headers={
            "Cache-Control": "no-cache",
            "Connection": "keep-alive",
            "X-Accel-Buffering": "no"  # 禁用Nginx缓冲
        }
    )

# ========== 任务管理 API ==========
@app.get("/tasks/hunyuan")
async def get_hunyuan_tasks():
    """获取所有腾讯混元3D任务状态"""
    return {
        "success": True,
        "message": "获取任务列表成功",
        "data": {
            "count": len(hunyuan_tasks),
            "tasks": hunyuan_tasks
        }
    }

# ========== 健康检查 ==========
@app.get("/health")
async def health_check():
    """健康检查接口"""
    services = {
        "腾讯混元3D生成器": hunyuan_3d_generator is not None,
        "火山引擎生成器": doubao_generator is not None,
        "科大讯飞语音识别器": rtasr_client is not None
    }

    all_healthy = all(services.values())

    return {
        "status": "healthy" if all_healthy else "unhealthy",
        "services": services,
        "timestamp": time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    }

if __name__ == "__main__":
    uvicorn.run(
        "server_all:app",
        host="0.0.0.0",
        port=8000,
        reload=False,
        log_level="info"
    )