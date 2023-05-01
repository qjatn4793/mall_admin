package com.shopping.mallAdmin.manager.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserVo {
    private int userSeq;
    private String userId;
    private String userPw;
    private String userName;
    private String userProfile;
    private String userBirth;
    private String userPhone;
    private String regDate;
    private int status;
}
