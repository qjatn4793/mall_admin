package com.shopping.mallAdmin.admin.controller;

import com.shopping.mallAdmin.admin.service.AdminService;
import com.shopping.mallAdmin.configuration.PasswordUtil;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;

@AllArgsConstructor
@ResponseBody
@RestController
public class AdminController {

    @Autowired
    AdminService adminService;

    @DeleteMapping("/admin")
    public String logout(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.removeAttribute("adminLoginCheck");
        session.removeAttribute("adminId");
        session.removeAttribute("adminPw");
        session.invalidate();
        return "adminLogout";
    }

    @PostMapping("/admin/adminDesign/{fileName}")
    public int adminDesign(@PathVariable("fileName") String fileName, @RequestBody HashMap<String, String> uploadPath) throws Exception{
        String uploadFileName = uploadPath.get("uploadPath");
        String realFileName = fileName + ".jpg";

        // TODO : 프로젝트 경로가 바뀌면 상기 경로 확인 후 변경해줘야함
        String path = System.getProperty("user.dir"); // 현재 경로 > C:\Users\PHS-SECURUS\Desktop\mallAdmin // C:\Users\kimbeomsoo\Desktop\apache-tomcat-9.0.60\bin
        /*개발시*/
        //String divPath = "\\src\\main\\resources\\static\\common\\assets\\img\\";
        /*끝*/
        /*배포시 windows*/
        //path = path.replace("bin", "webapps");
        //String divPath = "\\ROOT\\WEB-INF\\classes\\static\\common\\assets\\img\\";
        /*끝*/
        /*배포시 linux*/
        path = path.replace("bin", "webapps");
        String divPath = "/ROOT/WEB-INF/classes/static/common/assets/img/";
        /*끝*/
        String realPath = path + divPath;

        uploadFileName = uploadFileName.replace("C:\\uploadPath\\", "");

        //File to Multipartfile
        try {

            File file = new File("/temp/uploadPath/" + uploadFileName); // String to File
            FileInputStream input = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile("file", uploadFileName, "text/plain", IOUtils.toByteArray(input));

            if(multipartFile != null){
                File target = new File(realPath, realFileName);
                FileCopyUtils.copy(multipartFile.getBytes(), target);

                return 1;
            }else {
                return 0;
            }
        }catch (Exception e) {
            System.out.println(e);
            return 0;
        }
        //여기까지
    }
}
