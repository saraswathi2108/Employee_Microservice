package com.hrms.project.service;

import com.hrms.project.dto.DepartmentDTO;
import com.hrms.project.dto.EmployeeDepartmentDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface DepartmentService {


    DepartmentDTO saveDepartment(DepartmentDTO departmentDTO);

    EmployeeDepartmentDTO getEmployeesByDepartmentId(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder,String departmentId);

    DepartmentDTO updateDepartment(String departmentId, DepartmentDTO departmentDTO);

    List<DepartmentDTO> getAllDepartmentDetails(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder);

    DepartmentDTO getByDepartmentId(String departmentId);

  EmployeeDepartmentDTO getEmployeeByEmployeeId(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder,String employeeId);

   String deleteDepartment(String departmentId);
}
