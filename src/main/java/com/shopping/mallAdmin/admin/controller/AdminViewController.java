package com.shopping.mallAdmin.admin.controller;

import com.shopping.mallAdmin.admin.service.AdminService;
import com.shopping.mallAdmin.configuration.PasswordUtil;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
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
    public String admin(HttpServletRequest request) {
        Boolean adminLoginCheck = (Boolean) request.getSession().getAttribute("adminLoginCheck");
        if (adminLoginCheck != null && adminLoginCheck) {

            return "admin/admin.html";
        } else {

            return "login/login.html";
        }
    }

    /*로그인 프로세스*/
    @PostMapping("/admin")
    public String loginProc(@RequestParam String userId, @RequestParam String password, @RequestParam(required = false) boolean rememberMe, Model model, HttpServletRequest request) {

        String encryptedPassword = PasswordUtil.sha256(password); // 패스워드 암호화

        boolean loginCheck = adminService.loginCheck(userId, encryptedPassword);

        request.getSession().setAttribute("adminLoginCheck", loginCheck);

        return "redirect:/";
    }

    /*로그아웃 프로세스*/
    @DeleteMapping("/admin")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("adminLoginCheck");
        session.removeAttribute("adminId");
        session.removeAttribute("adminPw");
        session.invalidate();
        return "/";
    }
}
