package com.shopping.mallAdmin.main.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MainVo {
    private String adminId;
    private String adminPw;
    private String adminPhone;
    private int status;
    private String regDate;
    private String loginDate;
}