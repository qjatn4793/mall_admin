package com.shopping.mallAdmin.configuration.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        boolean loginCheck =false;

        HttpSession session = request.getSession();
        if(!StringUtils.isEmpty(session.getAttribute("adminLoginCheck"))){
            if(session.getAttribute("adminLoginCheck").equals("success")){
                if(session.getAttribute("adminId") != null && session.getAttribute("adminId") != "" && session.getAttribute("adminPw") != null && session.getAttribute("adminPw") != "") {
                    loginCheck = true;
                }else {
                    response.sendRedirect("/");
                    session.invalidate();
                }
            }else{
                response.sendRedirect("/");
                session.invalidate();
            }
        }else{
            response.sendRedirect("/");
            session.invalidate();
        }

        return loginCheck;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object object, Exception arg3) throws Exception {

    }
}