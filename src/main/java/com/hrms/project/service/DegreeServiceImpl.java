package com.hrms.project.service;

import com.hrms.project.entity.DegreeCertificates;
import com.hrms.project.entity.Employee;
import com.hrms.project.handlers.DegreeNotFoundException;
import com.hrms.project.handlers.UnauthorizedAccessException;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.DegreeDTO;
import com.hrms.project.repository.DegreeCertificatesRepository;
import com.hrms.project.repository.EmployeeRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class DegreeServiceImpl {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DegreeCertificatesRepository degreeCertificatesRepository;


    public List<DegreeDTO> getDegree(String employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        List<DegreeCertificates> degreeCertificates = employee.getDegreeCertificates();
        if (degreeCertificates == null || degreeCertificates.isEmpty()) {
            return Collections.emptyList();
        }
        return degreeCertificates.stream()
                .map(d -> modelMapper
                        .map(d, DegreeDTO.class)).toList();
    }


    public DegreeCertificates deleteById(String employeeId, String id) {


        DegreeCertificates degreeCertificates = degreeCertificatesRepository.findById(id)
                .orElseThrow(() -> new APIException("Degree certificate not found with ID: " + id));

        if (!degreeCertificates.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new APIException("This degree certificate does not belong to the given employee." + employeeId);
        }

        if (degreeCertificates.getAddFiles() != null && !degreeCertificates.getAddFiles().isEmpty()) {
            s3Service.deleteFile(degreeCertificates.getAddFiles());
        }

        degreeCertificatesRepository.deleteById(id);


        return degreeCertificates;
    }


    public DegreeCertificates addDegree(String employeeId, MultipartFile addFiles, @Valid DegreeCertificates degreeCertificates) throws IOException {
        if (degreeCertificatesRepository.existsByEmployeeEmployeeIdAndDegreeType(employeeId, degreeCertificates.getDegreeType())) {
            throw new IllegalArgumentException("This degree type already exists for the employee.");
        }
        String fileKey = s3Service.uploadDegreeFile(employeeId, degreeCertificates.getDegreeType(), "degree",addFiles);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found"));
        degreeCertificates.setEmployee(employee);
        degreeCertificates.setAddFiles(fileKey);

        return degreeCertificatesRepository.save(degreeCertificates);

    }

    public DegreeCertificates updateDegree(String employeeId, MultipartFile addFiles, String id, @Valid DegreeCertificates degreeCertificates) throws IOException {

        DegreeCertificates existingDegree = degreeCertificatesRepository.findById(id)
                .orElseThrow(() -> new APIException("Degree not found with ID: " + id));


        if (!existingDegree.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new APIException("This degree does not belong to the given employee");
        }

        if (!existingDegree.getDegreeType().equals(degreeCertificates.getDegreeType())) {
            boolean exists = degreeCertificatesRepository.existsByEmployeeEmployeeIdAndDegreeType(employeeId, degreeCertificates.getDegreeType());
            if (exists) {
                throw new IllegalArgumentException("This degree type already exists for the employee.");
            }
            existingDegree.setDegreeType(degreeCertificates.getDegreeType());
        }

        existingDegree.setDegree(degreeCertificates.getDegree());
        existingDegree.setBranchOrSpecialization(degreeCertificates.getBranchOrSpecialization());
        existingDegree.setStartMonth(degreeCertificates.getStartMonth());
        existingDegree.setEndMonth(degreeCertificates.getEndMonth());
        existingDegree.setStartYear(degreeCertificates.getStartYear());
        existingDegree.setEndYear(degreeCertificates.getEndYear());
        existingDegree.setCgpaOrPercentage(degreeCertificates.getCgpaOrPercentage());
        existingDegree.setUniversityOrCollege(degreeCertificates.getUniversityOrCollege());

        if (addFiles != null && !addFiles.isEmpty()) {
            if (existingDegree.getAddFiles() != null) {
                s3Service.deleteFile(existingDegree.getAddFiles());
            }
            String fileKey = s3Service.uploadDegreeFile(employeeId, existingDegree.getDegreeType(), "degree",addFiles);
            existingDegree.setAddFiles(fileKey);
        }

        return degreeCertificatesRepository.save(existingDegree);
    }

    public DegreeDTO getById(String employeeId, String id) {
        DegreeCertificates degreeCertificates = degreeCertificatesRepository.findById(id)
                .orElseThrow(() -> new DegreeNotFoundException("Degree not found with id :" + id));

        if (!degreeCertificates.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new APIException("This degree does not belong to the given employee");

        }
        return modelMapper.map(degreeCertificates,DegreeDTO.class);

    }
}