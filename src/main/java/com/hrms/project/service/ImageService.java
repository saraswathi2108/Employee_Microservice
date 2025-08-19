package com.hrms.project.service;


import com.hrms.project.dto.EmployeeDTO;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.handlers.ImageNotFoundException;
import com.hrms.project.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ImageService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ModelMapper modelMapper;

    public EmployeeDTO uploadEmployeeImage(MultipartFile employeeImage, String employeeId) throws IOException {
        Employee employee=employeeRepository.findById(employeeId).
                orElseThrow(()->new EmployeeNotFoundException("Employee Not Found with id:"+employeeId));

        String s3Key = s3Service.uploadFile(employeeId, "employeeImage", employeeImage);
        employee.setEmployeeImage(s3Key);
        Employee savedEmployee=employeeRepository.save(employee);
        String presignedUrl = s3Service.generatePresignedUrl(s3Key);
        EmployeeDTO dto = modelMapper.map(employee, EmployeeDTO.class);
        dto.setEmployeeImage(presignedUrl);

        return dto;
    }

    public EmployeeDTO deleteEmployeeImage(String employeeId) {
        Employee employee=employeeRepository.findById(employeeId).orElseThrow(()->new EmployeeNotFoundException("Employee Not Found with id:"+employeeId));

        if (employee.getEmployeeImage() != null) {
            s3Service.deleteFile(employee.getEmployeeImage());
            employee.setEmployeeImage(null);
            employeeRepository.save(employee);
        }
        return  modelMapper.map(employee,EmployeeDTO.class);
    }

    public String getEmployeeImage(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with id: " + employeeId));

        String s3Key = employee.getEmployeeImage();
        if (s3Key == null || s3Key.isBlank()) {
            throw new ImageNotFoundException("No image uploaded for employee: " + employeeId);
        }

        return s3Service.generatePresignedUrl(s3Key);
    }

}
