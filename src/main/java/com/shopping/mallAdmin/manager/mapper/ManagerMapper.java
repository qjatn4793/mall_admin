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

    List<ManagerVo> getCategoryList();

    int getTotalCount();

    ManagerVo getProductDetail(int productSeq);

    String getCategoryName(int categorySeq);

    int createProduct(ManagerVo managerVo);

    int deleteProduct(ManagerVo managerVo);

    int createCategory(ManagerVo managerVo);

    int updateCategory(ManagerVo managerVo);

    int deleteCategory(int categorySeq);

    int updateThumbImg(ManagerVo managerVo);

    int updateProductImg(ManagerVo managerVo);
}
