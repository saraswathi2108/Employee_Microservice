package com.hrms.project.service;

import com.hrms.project.entity.AadhaarCardDetails;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.AadhaarDTO;
import com.hrms.project.repository.AadhaarDetailsRepository;
import com.hrms.project.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class AadhaarServiceImpl {

    @Autowired
    private AadhaarDetailsRepository aadhaarDetailsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ModelMapper modelMapper;


    @Autowired
    private S3Service s3Service;

    public AadhaarCardDetails createAadhaar(String employeeId, MultipartFile aadhaarImage, AadhaarDTO aadhaarCardDetails) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        if (aadhaarDetailsRepository.findByEmployee_EmployeeId(employeeId) != null) {
            throw new APIException("This employee already has Aadhaar assigned");
        }

        if (aadhaarCardDetails.getAadhaarNumber() == null || aadhaarCardDetails.getAadhaarNumber().trim().isEmpty()) {
            throw new APIException("Aadhaar number cannot be null or empty");
        }

        AadhaarCardDetails savedDetails;

        if (aadhaarDetailsRepository.findById(aadhaarCardDetails.getAadhaarNumber()).isEmpty()) {

            if (aadhaarImage == null || aadhaarImage.isEmpty()) {
                throw new APIException("Aadhaar image is required and cannot be null or empty");
            }

            AadhaarCardDetails cardDetails = new AadhaarCardDetails();
            modelMapper.map(aadhaarCardDetails, cardDetails);

            String s3Key = s3Service.uploadFile(employeeId, "aadhaar", aadhaarImage);
            cardDetails.setUploadAadhaar(s3Key);
            cardDetails.setEmployee(employee);

            savedDetails = aadhaarDetailsRepository.save(cardDetails);

        } else {
            AadhaarCardDetails details = aadhaarDetailsRepository.findById(aadhaarCardDetails.getAadhaarNumber()).get();

            if (details.getEmployee() == null) {
                details.setEmployee(employee);
                modelMapper.map(aadhaarCardDetails, details);
                savedDetails = aadhaarDetailsRepository.save(details);
            } else {
                throw new APIException("Current Aadhaar card is already assigned to another employee");
            }
        }

        return savedDetails;
    }

    public AadhaarDTO getAadhaarByEmployeeId(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId).orElseThrow(() ->
                new EmployeeNotFoundException("Employee Not Found with id: " + employeeId));

        AadhaarCardDetails aadhaarCardDetails=employee.getAadhaarCardDetails();
        if(aadhaarCardDetails==null)
        {
            throw new APIException("Aadhaar card Details not found for the employee with id: " + employeeId);
        }
        AadhaarDTO aadhaarDTO = modelMapper.map(aadhaarCardDetails, AadhaarDTO.class);


        if (aadhaarCardDetails.getUploadAadhaar() != null) {
            String presignedUrl = s3Service.generatePresignedUrl(aadhaarCardDetails.getUploadAadhaar());
            aadhaarDTO.setUploadAadhaar(presignedUrl);
        }

        return aadhaarDTO;
    }



    public AadhaarCardDetails updateAadhaar(String employeeId,MultipartFile aadhaarImage, AadhaarDTO aadhaarCardDetails) throws IOException {
       System.out.println(aadhaarCardDetails);

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee Not Found with the id: " + employeeId));

        AadhaarCardDetails existing=employee.getAadhaarCardDetails();
        if(existing==null)
        {
            throw new APIException("Aadhaar card Details not found for employee with id: " + employeeId);
        }

        if (!existing.getAadhaarNumber().equals(aadhaarCardDetails.getAadhaarNumber())) {
            throw new APIException("Aadhaar number cannot be changed once submitted");
        }
        if (aadhaarImage != null && !aadhaarImage.isEmpty()) {
            String oldKey = existing.getUploadAadhaar();

            String newKey = s3Service.uploadFile(employeeId, "aadhaar", aadhaarImage);

            if (oldKey != null && !oldKey.equals(newKey)) {
                s3Service.deleteFile(oldKey);
            }

            existing.setUploadAadhaar(newKey);
        }

        existing.setDateOfBirth(aadhaarCardDetails.getDateOfBirth());
        existing.setEnrollmentNumber(aadhaarCardDetails.getEnrollmentNumber());
        existing.setAadhaarName(aadhaarCardDetails.getAadhaarName());
        existing.setGender(aadhaarCardDetails.getGender());
        existing.setAddress(aadhaarCardDetails.getAddress());


        return aadhaarDetailsRepository.save(existing);

    }

    public AadhaarCardDetails deleteAadharByEmployeeId(String employeeId) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()->new  EmployeeNotFoundException("Employee Not Found with the id: " + employeeId));


        AadhaarCardDetails aadhaarDetails = employee.getAadhaarCardDetails();

        if (aadhaarDetails == null) {
            throw new APIException("No Aadhaar details found for employee with id: " + employeeId);
        }

        if(aadhaarDetails.getUploadAadhaar() != null) {
            s3Service.deleteFile(aadhaarDetails.getUploadAadhaar());
        }

        employee.setAadhaarCardDetails(null);
        employeeRepository.save(employee);
        aadhaarDetailsRepository.deleteById(aadhaarDetails.getAadhaarNumber());
        return aadhaarDetails;
    }


}