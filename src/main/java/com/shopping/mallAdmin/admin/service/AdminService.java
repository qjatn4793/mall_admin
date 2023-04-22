package com.shopping.mallAdmin.admin.service;

import com.shopping.mallAdmin.admin.mapper.AdminMapper;
import com.shopping.mallAdmin.admin.vo.AdminVo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Transactional
@Service("adminService")
public class AdminService {

    @Autowired
    private AdminMapper adminMapper;

    public boolean loginCheck(String adminId, String encryptedPassword) {
        AdminVo adminVo = adminMapper.selectAdminById(adminId);
        if (adminVo != null) {
            String dbPassword = adminVo.getAdminPw();
            if (encryptedPassword.equals(dbPassword)) {
                return true; // 로그인 성공
            }
        }
        return false; // 로그인 실패
    }

    public int adminLoginCheck(AdminVo adminVo){

        return adminMapper.adminLoginCheck(adminVo);
    }

    public String adminSelectOne(String adminId){

        return adminMapper.adminSelectOne(adminId);
    }

}
