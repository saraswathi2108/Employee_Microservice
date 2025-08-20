package com.hrms.project.service;


import com.hrms.project.dto.EmployeeDTO;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.handlers.ImageNotFoundException;
import com.hrms.project.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ImageService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ModelMapper modelMapper;

    @Async("imageTaskExecutor")
    public CompletableFuture<EmployeeDTO> uploadEmployeeImage(MultipartFile employeeImage, String employeeId) throws IOException {
        log.info("Started upload on thread: {}", Thread.currentThread().getName());

        Employee employee=employeeRepository.findById(employeeId).
                orElseThrow(()->new EmployeeNotFoundException("Employee Not Found with id:"+employeeId));

        if (employee.getEmployeeImage() != null) {
            s3Service.deleteFile(employee.getEmployeeImage());
        }
        String s3Key = s3Service.uploadFile(employeeId, "employeeImage", employeeImage);
        employee.setEmployeeImage(s3Key);
        Employee savedEmployee=employeeRepository.save(employee);
        String presignedUrl = s3Service.generatePresignedUrl(s3Key);
        EmployeeDTO dto = modelMapper.map(employee, EmployeeDTO.class);
        dto.setEmployeeImage(presignedUrl);
        log.info("Finished upload on thread: {}", Thread.currentThread().getName());

        return CompletableFuture.completedFuture(dto);
    }

    public CompletableFuture<EmployeeDTO> deleteEmployeeImage(String employeeId) {
        log.info("Delete started for employeeId={} on thread={}", employeeId, Thread.currentThread().getName());

        Employee employee=employeeRepository.findById(employeeId).orElseThrow(()->new EmployeeNotFoundException("Employee Not Found with id:"+employeeId));

        if (employee.getEmployeeImage() != null) {
            s3Service.deleteFile(employee.getEmployeeImage());
            employee.setEmployeeImage(null);
            employeeRepository.save(employee);
        }

        log.info("Delete finished for employeeId={}", employeeId);
        return  CompletableFuture.completedFuture(modelMapper.map(employee,EmployeeDTO.class));
    }

    public CompletableFuture<String> getEmployeeImage(String employeeId) {
        log.info("Fetching image for employeeId={} on thread={}", employeeId, Thread.currentThread().getName());
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with id: " + employeeId));

        String s3Key = employee.getEmployeeImage();
        if (s3Key == null || s3Key.isBlank()) {
            log.warn("No image found for employeeId={}", employeeId);
            throw new ImageNotFoundException("No image uploaded for employee: " + employeeId);
        }

        return CompletableFuture.completedFuture(s3Service.generatePresignedUrl(s3Key));
    }

}
