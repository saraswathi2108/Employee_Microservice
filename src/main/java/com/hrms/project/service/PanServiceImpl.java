package com.hrms.project.service;

import com.hrms.project.entity.Employee;
import com.hrms.project.entity.PanDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.PanDTO;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.PanDetailsRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class PanServiceImpl {
    @Autowired
    private PanDetailsRepository panDetailsRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private ModelMapper modelMapper;

   @Autowired
   private S3Service s3Service;

    public PanDetails createPan(String employeeId, MultipartFile panImage, PanDTO panDTO) throws IOException {




        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (panDetailsRepository.findByEmployee_EmployeeId(employeeId).isPresent()) {
            throw new APIException("This employee already has a PAN assigned");
        }

        if (panImage == null || panImage.isEmpty()) {
            throw new APIException("PAN image is required and cannot be null or empty");
        }

        Optional<PanDetails> existingPanOpt = panDetailsRepository.findById(panDTO.getPanNumber());

        PanDetails panDetails;
        if (existingPanOpt.isPresent()) {
            panDetails = existingPanOpt.get();
            if (panDetails.getEmployee() == null)
            {
                panDetails.setEmployee(employee);
            }
            else
            {
                throw new APIException("This PAN is already assigned to another employee");
            }
        }
        else
        {
            panDetails = new PanDetails();
            modelMapper.map(panDTO,panDetails);
            panDetails.setEmployee(employee);
        }

        String s3Key = s3Service.uploadFile(employeeId, "panCard", panImage);
        panDetails.setPanImage(s3Key);

        return panDetailsRepository.save(panDetails);
    }



    public PanDTO getPanDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PanDetails details=employee.getPanDetails();

        if(details!=null)
        {
            PanDTO panDTO=modelMapper.map(details,PanDTO.class);
            if(details.getPanImage()!=null){
                String presignedUrl=s3Service.generatePresignedUrl(details.getPanImage());
                panDTO.setPanImage(presignedUrl);
            }
            return panDTO;
        }
        else
        {
            throw new APIException("This employee does not have a PAN assigned");
        }

    }

    public PanDetails UpdatePanDetails(String employeeId, MultipartFile panImage, PanDTO panDTO) throws IOException {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PanDetails existing = employee.getPanDetails();
        if (existing == null) {
            throw new APIException("PAN Details not found for employee: " + employeeId);
        }

        if (!existing.getPanNumber().equals(panDTO.getPanNumber())) {
            throw new APIException("PAN number cannot be changed once submitted");
        }

        if (panImage != null && !panImage.isEmpty()) {
            String oldKey = existing.getPanImage();
            String newKey = s3Service.uploadFile(employeeId, "panCard", panImage);
            if (!oldKey.equals(newKey)) {
                s3Service.deleteFile(oldKey);
            }
            existing.setPanImage(newKey);
        }

        existing.setPanName(panDTO.getPanName());
        existing.setDateOfBirth(panDTO.getDateOfBirth());
        existing.setParentsName(panDTO.getParentsName());

        PanDetails updated = panDetailsRepository.save(existing);
        return updated;
    }


    public PanDetails deletePanByEmployeeId(String employeeId) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(()-> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        PanDetails panDetails=employee.getPanDetails();
        if(panDetails==null){
            throw  new APIException("PAN details not found for employeeId"+ employeeId);
        }
        if(panDetails.getPanImage()!=null){
            s3Service.deleteFile(panDetails.getPanImage());
        }

        employee.setPanDetails(null);
        employeeRepository.save(employee);
        panDetailsRepository.deleteById(panDetails.getPanNumber());
        return  panDetails;
    }
}