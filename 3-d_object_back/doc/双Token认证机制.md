# 双 Token 认证机制

## 概述

系统采用 **Access Token + Refresh Token** 双 Token 认证方案，遵循 OAuth / OWASP 推荐实践。

| Token 类型 | 格式 | 有效期 | 用途 |
|-----------|------|--------|------|
| Access Token | JWT | 15 分钟 | 携带在每次业务请求中 |
| Refresh Token | 随机 UUID | 15 天 | 仅用于换取新的 Token 对 |

## 核心特性

- **Refresh Token Rotation**：每次刷新都签发新的 Refresh Token，旧的立即失效
- **重放检测**：如果已使用的 Refresh Token 再次出现，判定为泄露，整个会话吊销
- **BCrypt 密码加密**：使用 Spring Security 的 BCryptPasswordEncoder
- **会话级控制**：支持单设备登出和全设备登出
- **即将过期提示**：Access Token 剩余不足 2 分钟时，响应头返回 `X-Token-Expires-Soon: true`

## 数据库表

### refresh_sessions

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT PK | 自增主键 |
| session_id | VARCHAR(64) | 会话标识（同一登录的多次刷新共享） |
| uid | BIGINT FK | 用户 ID |
| refresh_token_hash | VARCHAR(128) | Refresh Token 的 SHA-256 哈希 |
| status | ENUM | active / used / revoked |
| expires_at | TIMESTAMP | 过期时间 |
| device_info | VARCHAR(255) | 设备 User-Agent |
| created_at | TIMESTAMP | 创建时间 |
| last_used_at | TIMESTAMP | 最后使用时间 |

## 接口说明

### 登录

```
POST /api/users/login
Content-Type: application/json

{
    "account": "demo@example.com",
    "password": "123456"
}
```

响应：
```json
{
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "expiresIn": 900,
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000",
    "refreshExpiresIn": 1296000,
    "userInfo": {
        "uid": 1,
        "nickname": "TimeTraveler",
        "email": "demo@example.com",
        "avatarUrl": "...",
        "bio": "..."
    }
}
```

### 注册

```
POST /api/users/register
Content-Type: application/json

{
    "nickname": "新用户",
    "email": "new@example.com",
    "password": "123456"
}
```

响应（201，精简信息，不返回密码）：
```json
{
    "uid": 10,
    "nickname": "新用户",
    "email": "new@example.com"
}
```

### 刷新 Token

```
POST /api/users/refresh
Content-Type: application/json

{
    "refreshToken": "550e8400-e29b-41d4-a716-446655440000"
}
```

响应（返回全新的 Token 对，旧 Refresh Token 立即失效）：
```json
{
    "accessToken": "新的JWT...",
    "expiresIn": 900,
    "refreshToken": "新的UUID...",
    "refreshExpiresIn": 1296000,
    "userInfo": { ... }
}
```

### 登出当前设备

```
POST /api/users/logout
Authorization: Bearer <accessToken>
```

### 登出所有设备

```
POST /api/users/logout-all
Authorization: Bearer <accessToken>
```

## 认证流程

### 正常请求

```
客户端 → 携带 Access Token → 拦截器验证 JWT
  → 有效：注入 uid/sid，放行
  → 剩余<2分钟：响应头 X-Token-Expires-Soon: true
  → 过期：返回 401
```

### 刷新流程

```
客户端收到 401 或 X-Token-Expires-Soon
  → POST /api/users/refresh {refreshToken}
  → 服务端查找 hash
    → active + 未过期 → 旧记录标为 used → 签发新 Token 对
    → used（重放检测）→ 整个 session 全部 revoked → 401
    → revoked / expired → 401
```

### 前端推荐实现

```javascript
// 请求拦截器
axios.interceptors.response.use(
  response => {
    // 检查即将过期标记
    if (response.headers['x-token-expires-soon'] === 'true') {
      refreshTokens(); // 后台静默刷新
    }
    return response;
  },
  async error => {
    if (error.response?.status === 401 && !error.config._retry) {
      error.config._retry = true;
      try {
        const res = await axios.post('/api/users/refresh', {
          refreshToken: localStorage.getItem('refreshToken')
        });
        localStorage.setItem('accessToken', res.data.accessToken);
        localStorage.setItem('refreshToken', res.data.refreshToken);
        error.config.headers.Authorization = 'Bearer ' + res.data.accessToken;
        return axios(error.config); // 重试原请求
      } catch {
        // Refresh 也失败，跳转登录
        router.push('/login');
      }
    }
    return Promise.reject(error);
  }
);

// 单飞控制：同一时刻只允许一个刷新请求
let refreshPromise = null;
function refreshTokens() {
  if (!refreshPromise) {
    refreshPromise = axios.post('/api/users/refresh', {
      refreshToken: localStorage.getItem('refreshToken')
    }).then(res => {
      localStorage.setItem('accessToken', res.data.accessToken);
      localStorage.setItem('refreshToken', res.data.refreshToken);
    }).finally(() => {
      refreshPromise = null;
    });
  }
  return refreshPromise;
}
```

## 安全要点

| 风险 | 应对措施 |
|------|----------|
| Access Token 泄露 | 有效期仅 15 分钟，窗口极小 |
| Refresh Token 泄露 | Rotation + 重放检测，一旦发现立刻吊销整个会话 |
| 并发刷新竞态 | 前端单飞控制，只发一个刷新请求 |
| 密码泄露 | BCrypt 加密存储，即使数据库泄露也无法逆向 |
| 改密码/踢下线 | 调用 logout-all 吊销用户所有 Refresh Token |

## 配置项

```properties
# Access Token 有效期（毫秒），默认 15 分钟
app.jwt.access-token-ttl=900000

# Refresh Token 有效期（毫秒），默认 15 天
app.jwt.refresh-token-ttl=1296000000

# JWT 签名密钥
app.jwt.secret=your-very-secure-secret-key-at-least-32-chars-long
```

## 修改文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `entity/RefreshSession.java` | 新增 | 刷新会话实体 |
| `repository/RefreshSessionRepository.java` | 新增 | 数据访问：查找/吊销 |
| `service/AuthService.java` | 新增 | 登录/刷新/登出/重放检测 |
| `dto/LoginResponse.java` | 新增 | 登录响应 DTO |
| `entity/User.java` | 修改 | passwordHash 加 @JsonIgnore |
| `utils/JwtUtils.java` | 重写 | 短期 Access Token + sessionId |
| `service/UserService.java` | 重写 | BCrypt 加密，移除 login 逻辑 |
| `controller/UserController.java` | 重写 | 双 Token 登录、刷新、登出接口 |
| `config/LoginInterceptor.java` | 修改 | 注入 sid、X-Token-Expires-Soon 头 |
| `config/WebConfig.java` | 修改 | 放行 refresh 接口，暴露自定义头 |
| `DataInitializer.java` | 修改 | BCrypt 密码 + 旧密码自动迁移 |
| `application.properties` | 修改 | 双 Token TTL 配置 |
| 数据库 `refresh_sessions` 表 | 新增 | 存储刷新会话状态 |
