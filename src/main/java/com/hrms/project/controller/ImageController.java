package com.hrms.project.controller;

import com.hrms.project.dto.EmployeeDTO;
import com.hrms.project.entity.Employee;
import com.hrms.project.service.ImageService;
import com.hrms.project.service.S3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ImageController {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ImageService imageService;

    @PostMapping("/{employeeId}/upload")
    public ResponseEntity<EmployeeDTO> uploadEmployeeImage(@RequestParam(value="employeeImage",required = false) MultipartFile employeeImage,
                                                           @PathVariable("employeeId") String employeeId) throws IOException {
        EmployeeDTO employee=imageService.uploadEmployeeImage(employeeImage,employeeId);
        return ResponseEntity.ok(employee);

    }
    @DeleteMapping("/{employeeId}/deleteImage")
    public ResponseEntity<String> deleteEmployeeImage(@PathVariable("employeeId") String employeeId){
        EmployeeDTO employeeDTO=imageService.deleteEmployeeImage(employeeId);
        return ResponseEntity.ok("Image deleted successfully for employeeId: " + employeeId);
    }

    @GetMapping("/{employeeId}/image")
    public ResponseEntity<String> getEmployeeImage(@PathVariable("employeeId") String employeeId){
        String preSignedUrl=imageService.getEmployeeImage(employeeId);
        return ResponseEntity.ok(preSignedUrl);
    }
}
