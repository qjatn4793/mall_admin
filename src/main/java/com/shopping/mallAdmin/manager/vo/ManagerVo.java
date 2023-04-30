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

    /*product_contents*/
    int productSeq;
    String productTitle;
    String productRegDate;
    int productViews;
    String productContents;
    String thumbContents;
}
