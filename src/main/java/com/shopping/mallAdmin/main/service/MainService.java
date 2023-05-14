package com.shopping.mallAdmin.main.service;

import com.shopping.mallAdmin.main.mapper.MainMapper;
import com.shopping.mallAdmin.main.vo.MainVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Transactional
@Service("mainService")
public class MainService {

    @Autowired
    private MainMapper mainMapper;

    public MainVo loginCheck(String adminId, String encryptedPassword) {
        MainVo adminVo = mainMapper.selectAdminById(adminId);
        if (adminVo != null) {
            String dbPassword = adminVo.getAdminPw();
            if (encryptedPassword.equals(dbPassword)) {
                return adminVo; // 로그인 성공
            }
        }
        return null; // 로그인 실패
    }

    public int getRevenue() {

        return mainMapper.getRevenue();
    }

    public int getSales() {
        return mainMapper.getSales();
    }

    public int getProduct() {
        return mainMapper.getProduct();
    }

    public int getUsers() {
        return mainMapper.getUsers();
    }

    public int updateLoginDate(String adminId) {
        return mainMapper.updateLoginDate(adminId);
    }
}
