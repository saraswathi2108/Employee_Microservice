package com.hrms.project.service;

import com.hrms.project.entity.Employee;
import com.hrms.project.entity.PassportDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.PassportDetailsDTO;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.PassportDetailsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class PassportDetailsServiceImpl {

    @Autowired
    private PassportDetailsRepository passportDetailsRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeRepository employeeRepository;


    @Autowired
    private S3Service s3Service;

    public PassportDetails createPassport(String employeeId, MultipartFile passportImage, PassportDetailsDTO passportDetailsDTO) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (passportDetailsRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has a Passport assigned");
        }

        if (passportImage == null || passportImage.isEmpty()) {
            throw new APIException("Passport image is required and cannot be null or empty");
        }

        Optional<PassportDetails> existingPassportOpt = passportDetailsRepository.findById(passportDetailsDTO.getPassportNumber());

        PassportDetails passportDetails;
        if (existingPassportOpt.isPresent()) {
            passportDetails = existingPassportOpt.get();
            if (passportDetails.getEmployee() == null) {
                passportDetails.setEmployee(employee);
            } else {
                throw new APIException("This Passport is already assigned to another employee");
            }
        } else {
            passportDetails = new PassportDetails();
            modelMapper.map(passportDetailsDTO, passportDetails);
            passportDetails.setEmployee(employee);
        }

        String s3Key = s3Service.uploadFile(employeeId, "passport", passportImage);
        passportDetails.setPassportImage(s3Key);

        return passportDetailsRepository.save(passportDetails);
    }





    public PassportDetailsDTO getPassportDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));
        PassportDetails details=employee.getPassportDetails();

        if(details==null)
        {
            throw new APIException("This employee does not have a PAN assigned");
        }

        if(details.getPassportImage()!=null){
            String presignedUrl=s3Service.generatePresignedUrl(details.getPassportImage());
            details.setPassportImage(presignedUrl);
        }

        return modelMapper.map(details,PassportDetailsDTO.class);

    }


    public PassportDetails updatePasswordDetails(String employeeId, MultipartFile passportImage, PassportDetailsDTO passportDetailsDTO) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PassportDetails existing = employee.getPassportDetails();
        if (existing == null) {
            throw new APIException("Passport Details not found for employee with ID: " + employeeId);
        }

        if (!existing.getPassportNumber().equals(passportDetailsDTO.getPassportNumber())) {
            throw new APIException("Passport number cannot be changed once submitted");
        }

        if (passportImage != null && !passportImage.isEmpty()) {
            String oldKey = existing.getPassportImage();
            String newKey = s3Service.uploadFile(employeeId, "passport", passportImage);
            if (oldKey != null && !oldKey.equals(newKey)) {
                s3Service.deleteFile(oldKey);
            }
            existing.setPassportImage(newKey);
        }

        existing.setCountryCode(passportDetailsDTO.getCountryCode());
        existing.setPassportType(passportDetailsDTO.getPassportType());
        existing.setDateOfBirth(passportDetailsDTO.getDateOfBirth());
        existing.setName(passportDetailsDTO.getName());
        existing.setGender(passportDetailsDTO.getGender());
        existing.setDateOfIssue(passportDetailsDTO.getDateOfIssue());
        existing.setPlaceOfIssue(passportDetailsDTO.getPlaceOfIssue());
        existing.setPlaceOfBirth(passportDetailsDTO.getPlaceOfBirth());
        existing.setDateOfExpiration(passportDetailsDTO.getDateOfExpiration());
        existing.setAddress(passportDetailsDTO.getAddress());

        return passportDetailsRepository.save(existing);
    }



    public PassportDetails deleteByEmployeeId(String employeeId) {
        Employee employee =employeeRepository.findById(employeeId)
                .orElseThrow(()->new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PassportDetails passportDetails=employee.getPassportDetails();
        if(passportDetails==null){
            throw  new APIException("No passport found for employee with ID: \" + employeeId");

        }
        if(passportDetails.getPassportImage()!=null){
            s3Service.deleteFile(passportDetails.getPassportImage());

        }
        employee.setPassportDetails(null);
        employeeRepository.save(employee);
        passportDetailsRepository.deleteById(passportDetails.getPassportNumber());
        return passportDetails;
    }
}