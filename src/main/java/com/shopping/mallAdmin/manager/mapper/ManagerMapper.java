package com.shopping.mallAdmin.manager.mapper;

import com.shopping.mallAdmin.manager.vo.ManagerVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface ManagerMapper {

    List<ManagerVo> getProductList(@Param("startIndex") int startIndex, @Param("pageSize") int pageSize);

    int getTotalCount();

    ManagerVo getProductDetail(int productSeq);
}
