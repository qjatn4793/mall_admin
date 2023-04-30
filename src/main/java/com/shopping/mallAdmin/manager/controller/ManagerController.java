package com.shopping.mallAdmin.manager.controller;

import com.shopping.mallAdmin.manager.service.ManagerService;
import com.shopping.mallAdmin.manager.vo.ManagerVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@ResponseBody
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    ManagerService managerService;

    @PostMapping("/product")
    public int createProduct(@RequestBody ManagerVo managerVo) {

        return managerService.createProduct(managerVo);
    }

    @DeleteMapping("/product")
    public int deleteProduct(@RequestBody ManagerVo managerVo) {

        System.out.println(managerVo);

        return managerService.deleteProduct(managerVo);
    }


    @PostMapping("/category")
    public int createCategory(@RequestBody ManagerVo managerVo) {

        return managerService.createCategory(managerVo);
    }

    @PutMapping("/category")
    public int updateCategory(@RequestBody ManagerVo managerVo) {

        return managerService.updateCategory(managerVo);
    }

    @DeleteMapping("/category")
    public int deleteCategory(@RequestBody ManagerVo managerVo) {

        return managerService.deleteCategory(managerVo.getCategorySeq());
    }

}