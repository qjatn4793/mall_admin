package com.shopping.mallAdmin.main.mapper;

import com.shopping.mallAdmin.main.vo.MainVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface MainMapper {

    MainVo selectAdminById(String adminId);

    int getRevenue();

    int getSales();

    int getProduct();

    int getUsers();

}
