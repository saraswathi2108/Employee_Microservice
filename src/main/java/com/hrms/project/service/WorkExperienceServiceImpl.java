package com.hrms.project.service;

import com.hrms.project.entity.Employee;
import com.hrms.project.entity.WorkExperienceDetails;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.WorkExperienceDTO;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.WorkExperienceRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class WorkExperienceServiceImpl {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private WorkExperienceRepository workExperienceRepository;

    @Autowired
    private ModelMapper modelMapper;

    public WorkExperienceDetails createExperenceByEmployeId(String employeeId,
                                                            WorkExperienceDTO workExperienceDTO)  {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() ->
                new EmployeeNotFoundException("Employee not found with id " + employeeId));

        WorkExperienceDetails workExperienceDetails = modelMapper.map(workExperienceDTO, WorkExperienceDetails.class);

        workExperienceDetails.setEmployee(employee);

        return workExperienceRepository.save(workExperienceDetails);
    }

    public WorkExperienceDetails updateExperience(String employeeId,
                                                  WorkExperienceDTO updatedData,
                                                  String id) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        WorkExperienceDetails existing = workExperienceRepository.findById(id)
                .orElseThrow(() -> new APIException("Work experience not found"));

        if (!existing.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new APIException("This work experience does not belong to employee with ID: " + employeeId);
        }
        modelMapper.map(updatedData, existing);
        existing.setId(id);
        existing.setEmployee(employee);
        return workExperienceRepository.save(existing);
    }


    public List<WorkExperienceDTO> getExperience(String employeeId) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));
        List<WorkExperienceDetails>experienceDetails=employee.getWorkExperienceDetails();
        return experienceDetails.stream()
                .map(exp->modelMapper.map(exp,WorkExperienceDTO.class))
                .toList();
    }

    public WorkExperienceDetails deleteExperienceById(String employeeId, String id) {
        Employee employee=employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        WorkExperienceDetails workExperienceDetails=workExperienceRepository.findById(id)
                .orElseThrow(()-> new APIException("Experience not found with ID: " + id));

        if (!workExperienceDetails.getEmployee().getEmployeeId().equals(employeeId)) {
            throw new APIException("This experience does not belong to the given employee.");
        }

        workExperienceRepository.delete(workExperienceDetails);
        return  workExperienceDetails;
    }
}