package com.shopping.mallAdmin.manager.service;

import com.shopping.mallAdmin.manager.mapper.ManagerMapper;
import com.shopping.mallAdmin.manager.vo.ManagerVo;
import com.shopping.mallAdmin.manager.vo.OrderVo;
import com.shopping.mallAdmin.manager.vo.UserVo;
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

        List<ManagerVo> productList = managerMapper.getProductList(startIndex, pageSize);
        for (ManagerVo product : productList) {
            if (product.getCategorySeq() == 0) {

            }else {
                product.setCategoryName(managerMapper.getCategoryName(product.getCategorySeq()));
            }
        }

        return productList;
    }

    public List<OrderVo> getOrderList(int startIndex, int pageSize) {

        List<OrderVo> orderList = managerMapper.getOrderList(startIndex, pageSize);

        for (OrderVo order : orderList) {
            OrderVo orderVo = managerMapper.getSelectVo(order.getProductSeq());
            order.setCategorySeq(orderVo.getCategorySeq());
            order.setProductPrice(orderVo.getProductPrice());
            order.setProductTitle(orderVo.getProductTitle());
            order.setCategoryName(managerMapper.getCategoryName(order.getCategorySeq()));
        }

        return orderList;
    }

    public List<UserVo> getUserList(int startIndex, int pageSize) {

        List<UserVo> productList = managerMapper.getUserList(startIndex, pageSize);

        return productList;
    }

    public List<ManagerVo> getCategoryList() {

        return managerMapper.getCategoryList();
    }

    public int getTotalCount() {

        return managerMapper.getTotalCount();
    }

    public int getTotalUserCount() {

        return managerMapper.getTotalUserCount();
    }

    public int getOrderTotalCount() {

        return managerMapper.getOrderTotalCount();
    }

    public ManagerVo getProductDetail(int productSeq) {

        return managerMapper.getProductDetail(productSeq);
    }

    public int createProduct(ManagerVo managerVo) {

        return managerMapper.createProduct(managerVo);
    }

    public int deleteProduct(ManagerVo managerVo) {

        return managerMapper.deleteProduct(managerVo);
    }

    public int createCategory(ManagerVo managerVo) {

        return managerMapper.createCategory(managerVo);
    }

    public int updateCategory(ManagerVo managerVo) {

        return managerMapper.updateCategory(managerVo);
    }

    public int deleteCategory(int categorySeq) {

        return managerMapper.deleteCategory(categorySeq);
    }

    public int updateThumbImg(ManagerVo managerVo){

        return managerMapper.updateThumbImg(managerVo);
    }

    public int updateProductImg(ManagerVo managerVo){

        return managerMapper.updateProductImg(managerVo);
    }

    public int updateProductStatus(ManagerVo managerVo) {

        return managerMapper.updateProductStatus(managerVo);
    }

    public int updateUserStatus(UserVo userVo) {

        return managerMapper.updateUserStatus(userVo);
    }

    public int updateOrderStatus(OrderVo orderVo) {

        return managerMapper.updateOrderStatus(orderVo);
    }

}
