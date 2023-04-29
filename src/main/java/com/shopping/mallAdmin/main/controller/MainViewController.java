package com.shopping.mallAdmin.main.controller;

import com.shopping.mallAdmin.main.service.MainService;
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
public class MainViewController {

    @GetMapping("/")
    public String admin(HttpServletRequest request) {
        Boolean adminLoginCheck = (Boolean) request.getSession().getAttribute("adminLoginCheck");
        if (adminLoginCheck != null && adminLoginCheck) {

            return "main/main.html";
        } else {

            return "login/login.html";
        }
    }
}
