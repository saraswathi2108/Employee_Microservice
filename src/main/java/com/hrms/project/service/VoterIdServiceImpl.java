package com.hrms.project.service;

import com.hrms.project.entity.Employee;
import com.hrms.project.entity.VoterDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.VoterDTO;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.VoterIdRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class VoterIdServiceImpl {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private VoterIdRepository voterIdRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private S3Service s3Service;

    public VoterDetails createVoter(String employeeId, MultipartFile voterImage,
                                    VoterDTO voterDTO) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
        if (voterIdRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has a Voter ID assigned");
        }
        Optional<VoterDetails> existingVoterOpt = voterIdRepository.findById(voterDTO.getVoterIdNumber());
        VoterDetails cardDetails;

        if (existingVoterOpt.isEmpty()) {

            cardDetails = new VoterDetails();
            modelMapper.map(voterDTO, cardDetails);

            if (voterImage == null && voterImage.isEmpty()) {
                throw new APIException("Voter image is empty and it should not be null");
            }
                String fileName =s3Service.uploadFile(employeeId,"voterImage",voterImage);
                cardDetails.setUploadVoter(fileName);


            cardDetails.setEmployee(employee);
            voterIdRepository.save(cardDetails);

        } else {

            VoterDetails existing = existingVoterOpt.get();

            if (existing.getEmployee() == null) {
                existing.setEmployee(employee);
                modelMapper.map(voterDTO, existing);
                cardDetails = voterIdRepository.save(existing);
            } else {
                throw new APIException("This Voter ID is already assigned to another employee");
            }
        }

        return cardDetails;
    }


    public VoterDTO getVoterByEmployee(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        VoterDetails voterDetails = employee.getVoterDetails();

        if (voterDetails == null) {
            throw new APIException("Voter ID details not found for this employee");
        }

        if(voterDetails.getUploadVoter()!=null){
            String presignedUrl=s3Service.generatePresignedUrl(voterDetails.getUploadVoter());
            voterDetails.setUploadVoter(presignedUrl);
        }

        return modelMapper.map(voterDetails, VoterDTO.class);
    }

    public VoterDetails updateVoter(String employeeId, MultipartFile voterImage, VoterDTO voterDTO) throws IOException {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        VoterDetails existing = employee.getVoterDetails();

        if (existing == null) {
            throw new APIException("Voter ID details not found for this employee");
        }

        if (!existing.getVoterIdNumber().equals(voterDTO.getVoterIdNumber())) {
            throw new APIException("Voter ID number cannot be changed once submitted");
        }

        if (voterImage != null && !voterImage.isEmpty()) {
            String oldKey=existing.getUploadVoter();

            String newKey =s3Service.uploadFile(employeeId,"voterImage",voterImage);
            if(oldKey!=null && !oldKey.equals(newKey)) {
                s3Service.deleteFile(oldKey);
            }
            existing.setUploadVoter(newKey);
        }

        existing.setFullName(voterDTO.getFullName());
        existing.setRelationName(voterDTO.getRelationName());
        existing.setGender(voterDTO.getGender());
        existing.setDateOfBirth(voterDTO.getDateOfBirth());
        existing.setAddress(voterDTO.getAddress());
        existing.setIssuedDate(voterDTO.getIssuedDate());

        return voterIdRepository.save(existing);
    }


    public VoterDetails deleteByEmployeeId(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        VoterDetails voterDetails = employee.getVoterDetails();

        if (voterDetails == null) {
            throw new APIException("Voter ID details not found for this employee" + employeeId);
        }
        if(voterDetails.getUploadVoter()!=null) {
            s3Service.deleteFile(voterDetails.getUploadVoter());
        }
        employee.setVoterDetails(null);
        employeeRepository.save(employee);
        voterIdRepository.deleteById(voterDetails.getVoterIdNumber());
        return voterDetails;
    }
}