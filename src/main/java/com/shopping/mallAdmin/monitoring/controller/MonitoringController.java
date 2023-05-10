package com.shopping.mallAdmin.monitoring.controller;

import com.shopping.mallAdmin.setting.vo.SettingVo;
import com.shopping.mallAdmin.monitoring.service.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/monitoring")
public class MonitoringController {

    @Autowired
    MonitoringService monitoringService;

    @PostMapping("/diskCheck")
    public List<Map<String, String>> diskCheck(@RequestBody SettingVo settingVo) throws Exception{
        return monitoringService.diskCheck(settingVo);
    }

    @PostMapping("/totalCheck")
    public List<Map<String, String>> totalCheck(@RequestBody SettingVo settingVo) throws Exception{
        return monitoringService.totalCheck(settingVo);
    }

    @PostMapping("/serviceControl")
    public int serviceControl(@RequestBody SettingVo settingVo) throws Exception{

        if (settingVo.getSystemControl().equals("start")) {
            return monitoringService.serviceControl(settingVo.getSystemNum(), settingVo.getSystemSeq(), settingVo.getSystemControl());
        }
        if (settingVo.getSystemControl().equals("stop")) {
            return monitoringService.serviceControl(settingVo.getSystemNum(), settingVo.getSystemSeq(), settingVo.getSystemControl());
        }
        if (settingVo.getSystemControl().equals("restart")){
            return monitoringService.serviceControl(settingVo.getSystemNum(), settingVo.getSystemSeq(), settingVo.getSystemControl());
        }

        return 0;
    }
}
