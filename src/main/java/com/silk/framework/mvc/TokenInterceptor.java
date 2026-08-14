package com.silk.framework.mvc;

import com.silk.entity.User;
import com.silk.framework.exception.MyException;
import com.silk.framework.jwt.JwtUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LindaSilk
 * @date 2021年3月10日, 周三
 * @description Token拦截器
 */
public class TokenInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();

        // 1. 静态页面和资源 - 免Token
        if (requestURI.contains(".") && (
                requestURI.endsWith(".html") ||
                requestURI.endsWith(".js")   ||
                requestURI.endsWith(".css")  ||
                requestURI.endsWith(".map")  ||
                requestURI.endsWith(".png")  ||
                requestURI.endsWith(".jpg")  ||
                requestURI.endsWith(".jpeg") ||
                requestURI.endsWith(".gif")  ||
                requestURI.endsWith(".svg")  ||
                requestURI.endsWith(".ico")  ||
                requestURI.endsWith(".woff") ||
                requestURI.endsWith(".woff2")||
                requestURI.endsWith(".ttf")  ||
                requestURI.endsWith(".eot"))) {
            return true;
        }

        // 2. 免Token的接口
        if (requestURI.endsWith("/login")            ||
            requestURI.endsWith("/file/upload")      ||
            requestURI.contains("/uploads/")) {
            return true;
        }

        String token = request.getHeader(JwtUtil.token);
        User user = JwtUtil.getUser(token);             // 根据token获取用户对象
        if (user == null) {
            throw new MyException("超时或不合法的Token！");
        }
        String newToken = JwtUtil.sign(user);           // 生成一个新的token
        response.setHeader(JwtUtil.token, newToken);
        response.setHeader("Access-Control-Expose-Headers", JwtUtil.token); // 将token暴露出来(用于跨域)
        request.setAttribute("user", user);          // 设置用户信息
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
