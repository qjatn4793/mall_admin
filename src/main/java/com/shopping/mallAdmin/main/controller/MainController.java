package com.shopping.mallAdmin.main.controller;

import com.shopping.mallAdmin.configuration.PasswordUtil;
import com.shopping.mallAdmin.main.service.MainService;
import com.shopping.mallAdmin.main.vo.MainVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@AllArgsConstructor
@ResponseBody
@RestController
public class MainController {

    @Autowired
    MainService adminService;

    /*로그인 프로세스*/
    @PostMapping("/admin")
    public String loginProc(@RequestBody MainVo mainVo, @RequestParam(required = false) boolean rememberMe, HttpServletRequest request) {

        String encryptedPassword = PasswordUtil.sha256(mainVo.getAdminPw()); // 패스워드 암호화

        boolean loginCheck = adminService.loginCheck(mainVo.getAdminId(), encryptedPassword);

        request.getSession().setAttribute("adminLoginCheck", loginCheck);

        if (loginCheck) {
            return "1";
        }else {
            return "0";
        }
    }

    /*로그아웃 프로세스*/
    @DeleteMapping("/admin")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("adminLoginCheck");
        session.invalidate();
        return "1";
    }

}