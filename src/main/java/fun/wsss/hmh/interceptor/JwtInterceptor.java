package fun.wsss.hmh.interceptor;

import com.auth0.jwt.JWT;
import fun.wsss.hmh.entity.User;
import fun.wsss.hmh.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class JwtInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            User user = new User();
            user.setId(JWT.decode(token).getClaim("userId").asLong());
            user.setUserName(JWT.decode(token).getClaim("userName").asString());
            user.setTrueName(JWT.decode(token).getClaim("trueName").asString());
            user.setRoleName(JWT.decode(token).getClaim("roleName").asString());
            UserContext.setCurrentUser(user);
            return true;
        }
        return false;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }
} 