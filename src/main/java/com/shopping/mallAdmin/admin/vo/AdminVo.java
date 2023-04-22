package com.shopping.mallAdmin.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminVo {
    private String adminId;
    private String adminPw;
    private int status;
    /*private String adminIp;
    private String name;
    private String regDate;
    private String regTime;
    private int roleId;*/
}
