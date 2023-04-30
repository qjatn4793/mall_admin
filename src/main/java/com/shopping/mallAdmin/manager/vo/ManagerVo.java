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
    private int productSeq;
    private String productTitle;
    private String productRegDate;
    private int productViews;
    private String productContents;
    private String thumbContents;
    private int productStatus;

    /*category_contents*/
    private int categorySeq;
    private String categoryName;
}
