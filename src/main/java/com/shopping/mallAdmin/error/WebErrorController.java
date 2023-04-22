package com.shopping.mallAdmin.error;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class WebErrorController implements ErrorController {

    /*@Override
    public String getErrorPath() {
        return null;
    }*/

    @GetMapping("/error")
    public String handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status != null){
            int statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()){
                return "error/404";
            }else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()){
                return "error/500";
            }else if(statusCode == HttpStatus.UNAUTHORIZED.value()){
                return "error/401";
            }else {
                return "error/404";
            }
        }else {
            return "error/404";
        }
    }
}
