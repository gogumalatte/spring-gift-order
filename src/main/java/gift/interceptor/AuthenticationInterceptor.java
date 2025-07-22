package gift.interceptor;

import gift.exception.AuthenticationException;
import gift.login.Authenticated;
import gift.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    public AuthenticationInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod) || !isAuthenticationRequired(handlerMethod)) {
            return true;
        }

        String token = Optional.ofNullable(request.getHeader("Authorization"))
            .filter(h -> h.startsWith("Bearer "))
            .map(h -> h.substring(7))
            .orElseGet(() -> extractTokenFromCookie(request));

        if (token == null || !jwtUtil.validateToken(token)) {
            throw new AuthenticationException("유효하지 않은 토큰이거나 토큰이 없습니다.");
        }

        request.setAttribute("userEmail", jwtUtil.getClaims(token).getSubject());
        return true;
    }

    private String extractTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
            .filter(cookie -> "jwt-token".equals(cookie.getName()))
            .findFirst()
            .map(Cookie::getValue)
            .orElse(null);
    }

    private boolean isAuthenticationRequired(HandlerMethod handlerMethod) {
        return handlerMethod.hasMethodAnnotation(Authenticated.class) ||
            handlerMethod.getBeanType().isAnnotationPresent(Authenticated.class);
    }
}