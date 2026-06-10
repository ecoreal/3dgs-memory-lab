package com.cqq._3dobject.config;

import com.cqq._3dobject.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtils jwtUtils;

    private static final long EXPIRES_SOON_THRESHOLD_MS = 2 * 60 * 1000; // 2 分钟

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            Claims claims = jwtUtils.validateToken(token);

            if (claims != null) {
                Long uid = claims.get("uid", Long.class);
                String sid = claims.get("sid", String.class);
                request.setAttribute("current_uid", uid);
                request.setAttribute("current_sid", sid);

                // 如果 access token 剩余 < 2 分钟，提示前端刷新
                long remaining = claims.getExpiration().getTime() - System.currentTimeMillis();
                if (remaining < EXPIRES_SOON_THRESHOLD_MS) {
                    response.setHeader("X-Token-Expires-Soon", "true");
                }

                return true;
            }
        }

        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\":\"Unauthorized: Access Token 无效或已过期\"}");
        return false;
    }
}
