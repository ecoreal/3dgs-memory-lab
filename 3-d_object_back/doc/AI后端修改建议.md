# AI 后端修改建议

目标不是重构全部实现，而是解决当前会直接影响 Java 代理层稳定性和联调结果的一组问题。

## 优先级

1. 统一错误返回
2. 统一 `image_base64` 入参处理
3. 去掉不适合部署环境的启动参数
4. 清理依赖和 Pydantic 兼容问题

---

## 1. Hunyuan 入参和文档约定不一致

**文件**: [Hunyuan3DGenerator.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/Hunyuan3DGenerator.py)

**问题位置**:
- [Hunyuan3DGenerator.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/Hunyuan3DGenerator.py#L47)
- [Hunyuan3DGenerator.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/Hunyuan3DGenerator.py#L91)

**现象**:
- Java 和文档约定的输入是 `data:image/<格式>;base64,<编码>`
- Python 侧当前直接把整个字符串原样传给腾讯 SDK 的 `ImageBase64`
- 某些情况下腾讯接口会把它识别成“格式无效”

**建议**:
- 在进入腾讯 SDK 之前，明确解析 `data:image/...;base64,...`
- 只把逗号后的纯 Base64 内容传给 SDK
- 同时保留前缀中的图片格式信息用于校验和日志

---

## 2. query/query_fast 查询失败时返回 `None`

**文件**:
- [Hunyuan3DGenerator.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/Hunyuan3DGenerator.py)
- [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py)

**问题位置**:
- [Hunyuan3DGenerator.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/Hunyuan3DGenerator.py#L140)
- [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py#L123)
- [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py#L141)

**现象**:
- 腾讯查询异常时，生成器直接返回 `None`
- FastAPI 路由也没有统一错误对象，只是返回 `None`
- Java 侧只能再做一次兜底，丢失原始错误上下文

**建议**:
- Python 侧直接统一返回结构化失败对象，例如：
```json
{
  "Status": "FAIL",
  "ResultFile3Ds": [],
  "ErrorCode": "QUERY_FAILED",
  "ErrorMessage": "具体错误"
}
```
- `query` 和 `query_fast` 保持完全一致

---

## 3. Seedream 两个接口失败语义不一致

**文件**: [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py)

**问题位置**:
- [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py#L153)
- [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py#L177)

**现象**:
- `/seedream/enhance` 失败时返回：
```json
{"success": false, "image_url": null}
```
- `/seedream/colorize` 失败时直接抛 `HTTPException(500)`

**影响**:
- Java 代理层会遇到两种完全不同的失败模式
- 前端对这两个接口无法用一致逻辑处理

**建议**:
- 两个接口统一为同一种失败返回
- 更推荐统一返回 `200 + success=false + image_url=null + error_message`

---

## 4. DouBao 图片接口的 `image` 参数契约需要确认

**文件**: [DouBaoGenerator.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/DouBaoGenerator.py)

**问题位置**:
- [DouBaoGenerator.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/DouBaoGenerator.py#L36)

**现象**:
- 当前调用方式是：
  - `image=image_base64`
- 实测中曾出现上游报错：
  - `invalid url specified`

**说明**:
- 这说明 SDK 对 `image` 字段接收的是纯 Base64、Data URI、还是 URL，当前实现并不稳定

**建议**:
- 以 SDK 官方文档为准重新确认 `image` 参数格式
- 明确只接受一种输入格式，不要靠试错兼容

---

## 5. 启动脚本默认 `reload=True`，不适合服务环境

**文件**: [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py)

**问题位置**:
- [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py#L269)

**现象**:
- 直接运行 `python server_all.py` 时会启用 `reload=True`
- 某些环境下文件监听会失败，服务无法正常启动

**建议**:
- 开发态才开启 `reload`
- 默认启动参数改为 `reload=False`
- 或者通过环境变量控制

---

## 6. Pydantic V2 兼容写法不一致

**文件**: [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py)

**问题位置**:
- [server_all.py](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/server_all.py#L45)

**现象**:
- `Hunyuan3DSubmitRequest` 和 `RTASRRequest` 用的是 `ConfigDict`
- `DouBaoImageGenerateRequest` 还在用 `class Config / schema_extra`
- 当前运行会出现 warning

**建议**:
- 三个请求模型统一改成 Pydantic V2 写法
- 避免后续升级依赖后直接变成兼容性问题

---

## 7. 依赖说明要和代码一致

**文件**:
- [README.txt](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/README.txt)
- [requirements.txt](/Users/mac/Desktop/project/3_d_object/3-d_object_ai_back/requirements.txt)

**现状**:
- 这部分目前已经基本对齐
- 代码实际需要的是 `websocket-client`

**建议**:
- 后续继续保持“代码 import 什么，就写什么依赖”
- 不要同时保留同名但不同实现的 `websocket` 包

---

## 建议的最小修改顺序

1. 先修 Hunyuan 的 `image_base64` 解析
2. 再统一 `query/query_fast` 失败返回结构
3. 再统一 `enhance/colorize` 失败语义
4. 最后处理 `reload=True` 和 Pydantic warning
