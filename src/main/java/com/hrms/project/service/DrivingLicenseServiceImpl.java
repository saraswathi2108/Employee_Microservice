package com.hrms.project.service;

import com.hrms.project.entity.DrivingLicense;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.DrivingLicenseDTO;
import com.hrms.project.repository.DrivingLicenseRepository;
import com.hrms.project.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class DrivingLicenseServiceImpl {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DrivingLicenseRepository drivingLicenseRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private S3Service  s3Service;

    public DrivingLicense createDrivingLicense(String employeeId, MultipartFile licenseImage, DrivingLicenseDTO drivingLicenseDTO) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (drivingLicenseRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has a Driving License assigned");
        }

        if (licenseImage == null || licenseImage.isEmpty()) {
            throw new APIException("Driving License image is required and cannot be null or empty");
        }

        Optional<DrivingLicense> existingLicenseOpt = drivingLicenseRepository.findById(drivingLicenseDTO.getLicenseNumber());

        DrivingLicense drivingLicense;
        if (existingLicenseOpt.isPresent()) {
            drivingLicense = existingLicenseOpt.get();
            if (drivingLicense.getEmployee() == null) {
                drivingLicense.setEmployee(employee);
            }
            else {
                throw new APIException("This Driving License is already assigned to another employee");
            }
        } else {
            drivingLicense = new DrivingLicense();
            modelMapper.map(drivingLicenseDTO, drivingLicense);
            drivingLicense.setEmployee(employee);
        }

        String s3Key = s3Service.uploadFile(employeeId, "drivingLicense", licenseImage);
        drivingLicense.setLicenseImage(s3Key);

        return drivingLicenseRepository.save(drivingLicense);
    }



    public DrivingLicenseDTO getDrivingDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));
        DrivingLicense details=employee.getDrivingLicense();

        if(details==null)
        {
            throw new APIException("This employee does not have a Driving License assigned");
        }
        if(details.getLicenseImage()!=null){
            String presignedUrl=s3Service.generatePresignedUrl(details.getLicenseImage());
            details.setLicenseImage(presignedUrl);
        }
        return modelMapper.map(details,DrivingLicenseDTO.class);

    }


    public DrivingLicense updatedrivingDetails(String employeeId, MultipartFile licenseImage, DrivingLicenseDTO drivingLicenseDTO) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        DrivingLicense existing = employee.getDrivingLicense();
        if (existing == null) {
            throw new APIException("Driving License details not found for employee: " + employeeId);
        }

        if (!existing.getLicenseNumber().equals(drivingLicenseDTO.getLicenseNumber())) {
            throw new APIException("License number cannot be changed once submitted");
        }

        if (licenseImage != null && !licenseImage.isEmpty()) {
            String oldKey = existing.getLicenseImage();
            String newKey = s3Service.uploadFile(employeeId, "drivingLicense", licenseImage);
            if (!oldKey.equals(newKey)) {
                s3Service.deleteFile(oldKey);
            }
            existing.setLicenseImage(newKey);
        }
        existing.setName(drivingLicenseDTO.getName());
        existing.setDateOfBirth(drivingLicenseDTO.getDateOfBirth());
        existing.setBloodGroup(drivingLicenseDTO.getBloodGroup());
        existing.setFatherName(drivingLicenseDTO.getFatherName());
        existing.setIssueDate(drivingLicenseDTO.getIssueDate());
        existing.setExpiresOn(drivingLicenseDTO.getExpiresOn());
        existing.setAddress(drivingLicenseDTO.getAddress());

        return  drivingLicenseRepository.save(existing);
    }



    public DrivingLicense deleteByEmployeeId(String employeeId) {
        Employee employee =employeeRepository.findById(employeeId).
                orElseThrow(()->new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        DrivingLicense drivingLicense=employee.getDrivingLicense();

        if(drivingLicense==null){
            throw new APIException("No Driving License found for employee with ID: " + employeeId);
        }
        if(drivingLicense.getLicenseImage()!=null){
            s3Service.deleteFile(drivingLicense.getLicenseImage());
        }

        employee.setDrivingLicense(null);
        employeeRepository.save(employee);

        drivingLicenseRepository.deleteById(drivingLicense.getLicenseNumber());
        return  drivingLicense;

    }
}