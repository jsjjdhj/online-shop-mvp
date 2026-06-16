package com.shop.interceptor;

import com.shop.common.BusinessException;
import com.shop.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token == null || !token.startsWith("Bearer ")) {
            throw new BusinessException(401, "未登录或token已过期");
        }
        token = token.substring(7);
        Claims claims = jwtUtil.parseToken(token);
        request.setAttribute("userId", Long.valueOf(claims.getSubject()));
        request.setAttribute("username", claims.get("username"));
        request.setAttribute("role", claims.get("role"));
        return true;
    }
}
