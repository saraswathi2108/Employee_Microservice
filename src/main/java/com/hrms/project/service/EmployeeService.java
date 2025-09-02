package com.hrms.project.service;

import com.hrms.project.dto.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.List;


@Component
public interface EmployeeService {

    EmployeeDTO createData(EmployeeDTO employeeDTO) throws IOException;

    EmployeeDTO getEmployeeById(String id);

    List<EmployeeDTO> getAllEmployees( Integer pageNumber,Integer pageSize, String sortBy, String sortOrder);


    EmployeeDTO deleteEmployee(String id);

    EmployeeDTO updateEmployee(String id,EmployeeDTO employeeDTO);

    ContactDetailsDTO getEmployeeContactDetails(String employeeId);

    List<ContactDetailsDTO> getAllEmployeeContactDetails(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder);

    ContactDetailsDTO updateContactDetails(String employeeId,ContactDetailsDTO contactDetailsDTO);

    AddressDTO getAddress(String employeeId);

    List<AddressDTO> getAllAddress(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder);

    AddressDTO updateEmployeeAddress(String employeeId, AddressDTO addressDTO);

    EmployeePrimaryDetailsDTO getEmployeePrimaryDetails(String employeeId);

    EmployeePrimaryDetailsDTO updateEmployeeDetails(String employeeId, EmployeePrimaryDetailsDTO employeePrimaryDetailsDTO);

    JobDetailsDTO getJobDetails(String employeeId);

    JobDetailsDTO updateJobDetails(String employeeId, JobDetailsDTO jobDetailsDTO);

}