package com.shopping.mallAdmin.manager.controller;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@AllArgsConstructor
@Controller
@RequestMapping("/manager")
public class ManagerViewController {

    @GetMapping("/product_manager")
    public String product() {

        return "manager/product_manager.html";
    }

    @GetMapping("/user_manager")
    public String user() {

        return "manager/user_manager.html";
    }

    @GetMapping("/admin_manager")
    public String admin() {

        return "manager/admin_manager.html";
    }
}
