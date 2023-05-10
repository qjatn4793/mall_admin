package com.shopping.mallAdmin.setting.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingVo {
    private String systemSeq;

    private String systemName;

    private String systemHost;

    private String systemUser;

    private String systemPassword;

    private String systemPort;

    private String systemOs;

    private String systemService1;

    private String systemService2;

    private String systemService3;

    private int systemStatus1;

    private int systemStatus2;

    private int systemStatus3;

    private String systemControl;

    private String systemNum;

}