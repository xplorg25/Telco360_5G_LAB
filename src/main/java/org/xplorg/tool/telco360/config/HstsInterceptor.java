package org.xplorg.tool.telco360.config;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HstsInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Enable HSTS for one year (in seconds) and include subdomains
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        return true;
    }
}
