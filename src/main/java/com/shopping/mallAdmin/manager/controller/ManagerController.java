package com.shopping.mallAdmin.manager.controller;

import com.shopping.mallAdmin.manager.service.ManagerService;
import com.shopping.mallAdmin.manager.vo.ManagerVo;
import com.shopping.mallAdmin.manager.vo.OrderVo;
import com.shopping.mallAdmin.manager.vo.UserVo;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@AllArgsConstructor
@ResponseBody
@RestController
@RequestMapping("/manager")
public class ManagerController {

    @Autowired
    private Environment env;

    @Autowired
    ManagerService managerService;

    @PostMapping("/product")
    public int createProduct(@RequestBody ManagerVo managerVo) {

        return managerService.createProduct(managerVo);
    }

    @DeleteMapping("/product")
    public int deleteProduct(@RequestBody ManagerVo managerVo) {

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

    @PostMapping("/thumbUploadImg")
    public ResponseEntity<String> thumbUploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        ManagerVo managerVo = new ManagerVo();

        Path path = Paths.get("/manager/image" + "/thumb/" + file.getOriginalFilename());
        String productSeq = request.getParameter("productSeq");

        if (productSeq == null) {
            return null;
        }

        managerVo.setProductSeq(Integer.parseInt(productSeq));
        managerVo.setThumbContents(path.toString());

        managerService.updateThumbImg(managerVo);

        return uploadFile(file);
    }

    @PostMapping("/productUploadImg")
    public ResponseEntity<String> productUploadFile(@RequestParam("file") MultipartFile file, HttpServletRequest request) {

        ManagerVo managerVo = new ManagerVo();

        Path path = Paths.get("/manager/image" + "/product/" + file.getOriginalFilename());
        String productSeq = request.getParameter("productSeq");

        if (productSeq == null) {
            return null;
        }

        managerVo.setProductSeq(Integer.parseInt(productSeq));
        managerVo.setProductContents(path.toString());

        managerService.updateProductImg(managerVo);

        return uploadFile(file);
    }

    @GetMapping("/image/{contents}/{filename:.+}")
    public void getImage(@PathVariable String contents, @PathVariable String filename, HttpServletResponse response) {

        String uploadDir = env.getProperty("shared.image.upload-dir");
        Path path = Paths.get(uploadDir + "/" + contents + "/" + filename);

        try {
            InputStream inputStream = Files.newInputStream(path);
            if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                response.setContentType("image/jpeg");
            } else if (filename.endsWith(".png")) {
                response.setContentType("image/png");
            } else if (filename.endsWith(".gif")) {
                response.setContentType("image/gif");
            } else if (filename.endsWith(".bmp")) {
                response.setContentType("image/bmp");
            } else {
                response.setContentType("application/octet-stream");
            }
            IOUtils.copy(inputStream, response.getOutputStream()); // 이미지 파일 전송
        } catch (IOException e) {
            // 예외 처리
        }
    }

    /*파일업로드 공통 메소드*/
    private ResponseEntity<String> uploadFile(MultipartFile file) {
        try {
            String uploadDir = env.getProperty("shared.image.upload-dir");
            Path path = Paths.get(uploadDir + "/" + file.getOriginalFilename());

            Files.write(path, file.getBytes());
            return ResponseEntity.ok("File uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file");
        }
    }

    @PutMapping("updateProductStatus")
    public ResponseEntity<String> updateProductStatus(@RequestParam int productSeq, @RequestParam int productStatus) {

        ManagerVo managerVo = new ManagerVo();
        managerVo.setProductSeq(productSeq);
        managerVo.setProductStatus(productStatus);

        managerService.updateProductStatus(managerVo);

        return ResponseEntity.ok().body("상품 상태 업데이트 성공");
    }

    @PutMapping("updateUserStatus")
    public ResponseEntity<String> updateUserStatus(@RequestParam int userSeq, @RequestParam int status) {

        UserVo userVo = new UserVo();
        userVo.setUserSeq(userSeq);
        userVo.setStatus(status);

        managerService.updateUserStatus(userVo);

        return ResponseEntity.ok().body("사용자 상태 업데이트 성공");
    }

    @PutMapping("updateOrderStatus")
    public ResponseEntity<String> updateOrderStatus(@RequestParam int orderSeq, @RequestParam int orderStatus) {

        OrderVo orderVo = new OrderVo();
        orderVo.setOrderSeq(orderSeq);
        orderVo.setOrderStatus(orderStatus);

        managerService.updateOrderStatus(orderVo);

        return ResponseEntity.ok().body("주문 상태 업데이트 성공");
    }
}