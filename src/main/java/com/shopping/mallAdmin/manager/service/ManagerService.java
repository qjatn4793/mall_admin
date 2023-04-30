package com.shopping.mallAdmin.manager.service;

import com.shopping.mallAdmin.manager.mapper.ManagerMapper;
import com.shopping.mallAdmin.manager.vo.ManagerVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Transactional
@Service("managerService")
public class ManagerService {

    @Autowired
    ManagerMapper managerMapper;

    public List<ManagerVo> getProductList(int startIndex, int pageSize) {

        return managerMapper.getProductList(startIndex, pageSize);
    }

    public int getTotalCount() {

        return managerMapper.getTotalCount();
    }

    public ManagerVo getProductDetail(int productSeq) {

        return managerMapper.getProductDetail(productSeq);
    }

}
