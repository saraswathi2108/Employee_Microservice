package com.hrms.project.controller;

import com.hrms.project.dto.DepartmentDTO;
import com.hrms.project.dto.EmployeeDepartmentDTO;
import com.hrms.project.security.CheckEmployeeAccess;
import com.hrms.project.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employee")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    @PostMapping("/department")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<DepartmentDTO> createDepartment(@Valid @RequestBody DepartmentDTO departmentDTO) {

        DepartmentDTO departmentList = departmentService.saveDepartment(departmentDTO);
        return new ResponseEntity<>(departmentList, HttpStatus.CREATED);
    }

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/department/{departmentId}/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD')")
    public ResponseEntity<EmployeeDepartmentDTO> getEmployeesByDepartmentId(@PathVariable Integer pageNumber,
                                                                            @PathVariable Integer pageSize,
                                                                            @PathVariable String sortBy,
                                                                            @PathVariable String sortOrder,
                                                                            @PathVariable String departmentId) {
        EmployeeDepartmentDTO employeeDepartmentDTO = departmentService.getEmployeesByDepartmentId(pageNumber,pageSize,sortBy,sortOrder,departmentId);
        return new ResponseEntity<>(employeeDepartmentDTO, HttpStatus.OK);
    }

    @PutMapping("/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    @CheckEmployeeAccess(param = "id", roles = {"ADMIN", "HR"})
    public ResponseEntity<DepartmentDTO> updateDepartment(@PathVariable String departmentId, @Valid @RequestBody DepartmentDTO departmentDTO) {
        return new ResponseEntity<>(departmentService.updateDepartment(departmentId, departmentDTO), HttpStatus.CREATED);
    }


    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/all/departments")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments(@PathVariable Integer pageSize,
                                                                 @PathVariable Integer pageNumber,
                                                                 @PathVariable String sortBy,
                                                                 @PathVariable String sortOrder) {
        return new ResponseEntity<>(departmentService.getAllDepartmentDetails(pageNumber,pageSize,sortBy,sortOrder), HttpStatus.OK);
    }

    @GetMapping("/get/department/{departmentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<DepartmentDTO> getDepartmentById(@PathVariable String departmentId) {
        return new ResponseEntity<>(departmentService.getByDepartmentId(departmentId), HttpStatus.OK);
    }

    @GetMapping("/{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/{employeeId}/department/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'EMPLOYEE')")
    public ResponseEntity<EmployeeDepartmentDTO> getEmployeesByEmployeeId(@PathVariable Integer pageSize,
                                                                              @PathVariable Integer pageNumber,
                                                                              @PathVariable String sortBy,
                                                                              @PathVariable String sortOrder,
                                                                              @PathVariable String employeeId) {
        return new ResponseEntity<>(departmentService.getEmployeeByEmployeeId(pageNumber,pageSize,sortBy,sortOrder,employeeId),HttpStatus.OK);
        }

        @DeleteMapping("/{departmentId}/department")
        @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
         public ResponseEntity<String> deleteDepartment(@PathVariable String departmentId) {
        return new ResponseEntity<>(departmentService.deleteDepartment(departmentId),HttpStatus.OK);
        }

}

