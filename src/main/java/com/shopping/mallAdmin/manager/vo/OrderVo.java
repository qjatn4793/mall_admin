package com.shopping.mallAdmin.manager.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderVo {
    private int orderSeq;
    private String orderUserId;
    private String orderRegDate;
    private int orderCount;
    private int orderStatus;
    private int productSeq;
    private String productTitle;
    private int productPrice;
    private int categorySeq;

    private String categoryName;
}
