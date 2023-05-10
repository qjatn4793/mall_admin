package com.shopping.mallAdmin.setting.controller;

import com.shopping.mallAdmin.setting.service.SettingService;
import com.shopping.mallAdmin.setting.vo.SettingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/setting")
public class SettingController {

    @Autowired
    SettingService settingService;

    @PostMapping("/system_setting")
    public int insertSystem(@RequestBody SettingVo settingVo) throws Exception{

        if (settingVo.getSystemService1().trim().equals("")) {
            settingVo.setSystemService1(null);
        }
        if (settingVo.getSystemService2().trim().equals("")) {
            settingVo.setSystemService2(null);
        }
        if (settingVo.getSystemService3().trim().equals("")) {
            settingVo.setSystemService3(null);
        }

        return settingService.insertSystem(settingVo);
    }

    @PutMapping("/system_setting")
    public int updateSystem(@RequestBody SettingVo settingVo) throws Exception{

        return settingService.updateSystem(settingVo);
    }

    @DeleteMapping("/system_setting")
    public int deleteSystem(@RequestBody SettingVo settingVo) throws Exception{

        return settingService.deleteSystem(settingVo.getSystemSeq());
    }

    @GetMapping("/get_seq")
    public Map<Integer, Integer> getSeq() {

        return settingService.getSeq();
    }
}
