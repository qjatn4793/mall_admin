package com.shopping.mallAdmin.manager.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerVo {

    /*main_contents*/
    int mainSeq;
    String mainTitle;
    String mainRegDate;
    int mainViews;
    String mainContents;
    String thumbContents;
}
