package com.hrms.project.controller;

import com.hrms.project.dto.EmployeeDTO;
import com.hrms.project.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/employee")
public class ImageController {

    @Autowired
    private ImageService imageService;

    @PostMapping("/{employeeId}/upload")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")
    public CompletableFuture< ResponseEntity<EmployeeDTO>> uploadEmployeeImage(@RequestParam(value="employeeImage",required = false) MultipartFile employeeImage,
                                                                             @PathVariable("employeeId") String employeeId) throws IOException {
        return imageService.uploadEmployeeImage(employeeImage, employeeId)
                .thenApply(ResponseEntity::ok);
    }

    @DeleteMapping("/{employeeId}/deleteImage")
    @PreAuthorize("hasAnyRole('EMPLOYEE')")

    public CompletableFuture<ResponseEntity<String>> deleteEmployeeImage(@PathVariable("employeeId") String employeeId) {
        return imageService.deleteEmployeeImage(employeeId)
                .thenApply(dto -> ResponseEntity.ok("Image deleted successfully for employeeId: " + employeeId));

    }

    @GetMapping("/{employeeId}/image")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER','TEAM_LEAD')")
    public CompletableFuture<ResponseEntity<String>> getEmployeeImage(@PathVariable("employeeId") String employeeId){
            return imageService.getEmployeeImage(employeeId)
                    .thenApply(ResponseEntity::ok);
    }
}
