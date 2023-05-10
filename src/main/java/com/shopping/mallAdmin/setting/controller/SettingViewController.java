package com.shopping.mallAdmin.setting.controller;

import com.shopping.mallAdmin.setting.service.SettingService;
import com.shopping.mallAdmin.setting.vo.SettingVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/setting")
public class SettingViewController {

    @Autowired
    SettingService settingService;

    @GetMapping("/system_setting")
    public String system_setting(Model model){
        List<SettingVo> settingList = settingService.getSettingList();

        model.addAttribute("settingList", settingList);

        return "setting/system_setting.html";
    }

    /*@GetMapping("/user_setting")
    public String user_setting(){

        return "setting/user_setting.html";
    }

    @GetMapping("/user_access")
    public String user_access(){

        return "setting/user_setting.html";
    }

    @GetMapping("/user_audit")
    public String user_audit(){

        return "setting/user_audit.html";
    }*/
}
