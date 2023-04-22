package com.shopping.mallAdmin.admin.controller;

import com.shopping.mallAdmin.admin.service.AdminService;
import com.shopping.mallAdmin.configuration.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@AllArgsConstructor
@Controller
public class AdminViewController {

    @Autowired
    AdminService adminService;

    @GetMapping("/")
    public String admin(HttpServletRequest request){

        return "login/login.html";
    }

    @PostMapping("/loginProc")
    public String loginProc(@RequestParam String userId, @RequestParam String password, @RequestParam(required = false) boolean rememberMe, Model model, HttpServletRequest request) {

        String encryptedPassword = PasswordUtil.sha256(password); // 패스워드 암호화

        boolean loginCheck = adminService.loginCheck(userId, encryptedPassword);

        request.getSession().setAttribute("adminLoginCheck", loginCheck);

        if(loginCheck){
            return "admin/admin.html";
        }else {
            return "/";
        }
    }
}
