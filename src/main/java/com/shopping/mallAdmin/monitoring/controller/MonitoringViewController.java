package com.shopping.mallAdmin.monitoring.controller;

import com.shopping.mallAdmin.setting.service.SettingService;
import com.shopping.mallAdmin.setting.vo.SettingVo;
import com.shopping.mallAdmin.monitoring.service.MonitoringService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Controller
@RequestMapping("/monitoring")
public class MonitoringViewController {

    @Autowired
    SettingService settingService;

    @Autowired
    MonitoringService monitoringService;

    @GetMapping("/serverMonitor")
    public String serverMonitor(Model model){
        List<SettingVo> settingList = settingService.getSettingList();
        model.addAttribute("settingList", settingList);

        return "monitoring/serverMonitor.html";
    }

    /*@GetMapping("/detailServerMonitor")
    public String detailServerMonitor(Model model){
        List<SettingVo> settingList = settingService.getSettingList();
        model.addAttribute("settingList", settingList);

        return "monitoring/detailServerMonitor.html";
    }*/

    @GetMapping("/detailServerMonitor")
    public String detailServerMonitor(@RequestParam(name = "systemSeq") String systemSeq, Model model) throws Exception{
        SettingVo settingVo = settingService.getSetting(systemSeq);
        model.addAttribute("settingVo", settingVo);

        return "monitoring/detailServerMonitor.html";
    }

    @GetMapping("/serviceMonitor")
    public String serviceMonitor(Model model) throws Exception{
        List<SettingVo> serviceList = settingService.getServiceList();
        for (SettingVo settingVo : serviceList) {

            Map<String, String> serviceCheckResult = monitoringService.serviceCheck(settingVo.getSystemSeq());
            settingVo.setSystemStatus1(Integer.parseInt(serviceCheckResult.get("serviceResult1-" + settingVo.getSystemSeq())));
            settingVo.setSystemStatus2(Integer.parseInt(serviceCheckResult.get("serviceResult2-" + settingVo.getSystemSeq())));
            settingVo.setSystemStatus3(Integer.parseInt(serviceCheckResult.get("serviceResult3-" + settingVo.getSystemSeq())));
        }
        model.addAttribute("serviceList", serviceList);

        return "monitoring/serviceMonitor.html";
    }

    /*@GetMapping("/detection")
    public String detection(){

        return "monitoring/detection.html";
    }*/

}