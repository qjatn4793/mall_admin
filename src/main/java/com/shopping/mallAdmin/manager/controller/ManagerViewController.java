package com.shopping.mallAdmin.manager.controller;

import com.shopping.mallAdmin.manager.service.ManagerService;
import com.shopping.mallAdmin.manager.vo.ManagerVo;
import com.shopping.mallAdmin.manager.vo.UserVo;
import com.shopping.mallAdmin.util.PageInfo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@AllArgsConstructor
@Controller
@RequestMapping("/manager")
public class ManagerViewController {

    @Autowired
    ManagerService managerService;

    @GetMapping("/product_manager")
    public String product(@RequestParam(defaultValue = "1") int pageNum, Model model) {

        int pageSize = 12; // 한 페이지에 보여줄 데이터 개수
        int totalItemCount = managerService.getTotalCount(); // 전체 데이터 개수
        int totalPages = (int) Math.ceil((double) totalItemCount / pageSize); // 전체 페이지 개수
        int startIndex = (pageNum - 1) * pageSize;

        List<ManagerVo> productList = managerService.getProductList(startIndex, pageSize); // pageNum 에 해당하는 페이지 데이터 가져오기

        List<ManagerVo> categoryList = managerService.getCategoryList(); // category 데이터 가져오기

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(pageNum);
        pageInfo.setTotalPages(totalPages);

        model.addAttribute("productList", productList);
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("pageInfo", pageInfo);

        return "manager/product_manager.html";
    }

    @GetMapping("/product_detail")
    public String productDetail(@RequestParam("productSeq") int productSeq, Model model){

        // productSeq 값을 이용해 데이터 조회 및 처리
        ManagerVo managerVo = managerService.getProductDetail(productSeq);

        List<ManagerVo> categoryList = managerService.getCategoryList(); // category 데이터 가져오기

        model.addAttribute("managerVo", managerVo);
        model.addAttribute("categoryList", categoryList);
        return "manager/product_detail.html";
    }

    @GetMapping("/user_manager")
    public String user(@RequestParam(defaultValue = "1") int pageNum, Model model) {

        int pageSize = 12; // 한 페이지에 보여줄 데이터 개수
        int totalItemCount = managerService.getTotalUserCount(); // 전체 데이터 개수
        int totalPages = (int) Math.ceil((double) totalItemCount / pageSize); // 전체 페이지 개수
        int startIndex = (pageNum - 1) * pageSize;

        List<UserVo> userList = managerService.getUserList(startIndex, pageSize); // pageNum 에 해당하는 페이지 데이터 가져오기

        PageInfo pageInfo = new PageInfo();
        pageInfo.setPageNum(pageNum);
        pageInfo.setTotalPages(totalPages);

        model.addAttribute("userList", userList);
        model.addAttribute("pageInfo", pageInfo);

        return "manager/user_manager.html";
    }

    @GetMapping("/admin_manager")
    public String admin() {

        return "manager/admin_manager.html";
    }
}
