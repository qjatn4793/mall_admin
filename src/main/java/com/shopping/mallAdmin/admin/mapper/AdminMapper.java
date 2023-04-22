package com.shopping.mallAdmin.admin.mapper;

import com.shopping.mallAdmin.admin.vo.AdminVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AdminMapper {

    int adminLoginCheck(AdminVo adminVo);

    String adminSelectOne(String adminId);

    int adminGetUserCount();

}
