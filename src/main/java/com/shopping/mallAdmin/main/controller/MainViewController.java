package com.shopping.mallAdmin.main.controller;

import com.shopping.mallAdmin.main.service.MainService;
import com.shopping.mallAdmin.configuration.PasswordUtil;
import com.shopping.mallAdmin.main.vo.MainVo;
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

    @Autowired
    MainService mainService;

    @GetMapping("/")
    public String admin(HttpServletRequest request, Model model) {
        MainVo adminLoginCheck = (MainVo) request.getSession().getAttribute("mainVo");
        if (adminLoginCheck != null) {

            int revenue = mainService.getRevenue();
            int sales = mainService.getSales();
            int product = mainService.getProduct();
            int users = mainService.getUsers();

            model.addAttribute("revenue", revenue);
            model.addAttribute("sales", sales);
            model.addAttribute("products", product);
            model.addAttribute("users", users);

            return "main/main.html";
        } else {

            return "login/login.html";
        }
    }
}
