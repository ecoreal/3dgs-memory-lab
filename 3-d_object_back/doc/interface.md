AI端接口文段
3D模型生成API接口文档
基础URL: 公网地址暂时未定
Content-Type: application/json
响应格式: JSON
1. 提交腾讯混元3D生成任务
   接口描述: 提交一张图片（小于6M），生成对应的3D模型
   接口地址: POST /hunyuan3d/submit
   请求参数:
   参数名	类型	必填	说明	示例值
   image_base64	string	是	Base64编码的图片字符串，格式为data:image/<格式>;base64,<编码>	"data:image/png;base64,iVBORw0..."
   generate_type	string	否	任务类型，可选“Fast”(快速模式)/"Normal"(带纹理)/"LowPoly"/"Geometry"(白膜)/"Sketch"，应使用"Normal"	"Normal"

请求示例:
{
"image_base64": "data:image/png;base64,iVcdevefve...",
"generate_type": "Normal"
}
响应格式:
字段	类型	说明
success	boolean	是否成功
job_id	string	任务ID，成功时返回，失败时为none

成功响应示例:
{
"success": True,
"job_id": "job-1234567890"
}
失败响应示例:
{
"success": False,
"job_id": None
}
注意事项:
1.仅支持jpg，png，jpeg，webp
2.生成3D模型通常需要较长时间（几分钟到十几分钟）
3.图片须小于6M

2. 查询腾讯混元3D任务状态
   接口描述: 根据job_id查询3D生成任务的状态和结果
   接口地址: GET /hunyuan3d/query/{job_id}
   接口地址: GET /hunyuan3d/query_fast/{job_id}  //当使用快速模式生成时使用这个接口查询任务状态
   路径参数:
   参数名	类型	必填	说明	示例
   job_id	string	是	任务ID	"job-1234567890"

响应格式:直接返回腾讯云SDK的原始响应结果，主要字段如下：（ResultFile3Ds列表中存放的是对象）
{
"Status": "任务状态字符串",
"ResultFile3Ds": [
{
"Type": "文件格式（OBJ/GLB）",
"Url": "文件下载URL",
"PreviewImageUrl": "预览图URL"
},
],
"ErrorCode": "错误码（失败时）",
"ErrorMessage": "错误信息（失败时）"
}
状态说明:
状态值	说明
WAIT	等待中
RUN	处理中
DONE	已完成
FAIL	失败

成功示例:
{
"Status": "DONE",
"ResultFile3Ds": [
{
"Type": "glb",
"Url": "https://example.com/model.glb",
"PreviewImageUrl": "https://example.com/preview.jpg"
},
{
"Type": "obj",
"Url": "https://example.com/model.obj",
"PreviewImageUrl": "https://example.com/preview.jpg"
}
]
}
处理中示例:
{
"Status": "RUN"
}
失败示例:
{
"Status": "FAIL",
"ErrorCode": "InvalidImage",
"ErrorMessage": "图片格式不支持"
}
错误处理:
查询任务失败，返回空值None
注意事项:
1.生成的模型文件URL有效期为24小时

3. 火山引擎图片增强
   接口描述: 对输入图片进行高清画质修复
   接口地址: POST /seedream/enhance
   请求参数:
   参数名	类型	必填	说明	示例值
   image_base64	string	是	Base64编码的图片字符串	"data:image/png;base64,iVBORw0..."
   size	string	否	输出图片分辨率，可选"2K"或"4K"	"2K"

请求示例:
{
"image_base64": "data:image/png;base64,iVBOR...",
"size": "2K"
}
响应格式:
字段	类型	说明
success	boolean	是否成功
image_url	string	生成的图片URL

成功响应示例:
{
"success": True,
"image_url": "https://ark.cn-beijing.volces.com/images/xxx.jpg"
}
错误处理:
{
"success": False,
"image_url": None
}


注意事项:
1.输出格式为JPG
2.图片URL有效期为24小时
3.耗时约2-3min

4. 火山引擎图片上色(同3)
   接口描述: 对黑白图片进行自动上色
   接口地址: POST /seedream/colorize
   请求参数:
   参数名	类型	必填	说明	示例值
   image_base64	string	是	Base64编码的图片字符串	"data:image/png;base64,iVBORw0..."
   size	string	否	输出图片分辨率，可选"2K"或"4K"，默认"2K"	"2K"

请求示例:
{
"image_base64": "data:image/png;base64,iVBORw0KG...",
"size": "2K"
}
响应格式:
字段	类型	说明
success	boolean	是否成功
image_url	string	生成的图片URL

成功响应示例:
{
"success": true,
"image_url": "https://ark.cn-beijing.volces.com/images/xxx.jpg"
}
错误处理:
{
"success": False,
"image_url": None
}

注意事项:
1.输出格式为JPEG
2.图片URL有效期为24小时
3.耗时约2-3min