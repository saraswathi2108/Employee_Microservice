package com.hrms.project.controller;

import com.hrms.project.dto.PublicEmployeeDetails;
import com.hrms.project.service.PublicEmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employee")

public class PublicController {

    @Autowired
    private PublicEmployeeService publicEmployeeService;

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/public/employees")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER','TEAM_LEAD')")
    public ResponseEntity<List<PublicEmployeeDetails>> getAllEmployees(@PathVariable Integer pageNumber,
                                                                       @PathVariable Integer pageSize,
                                                                       @PathVariable String sortBy,
                                                                       @PathVariable String sortOrder) {
        return new ResponseEntity<>(publicEmployeeService.getAllEmployees(pageNumber,pageSize,sortBy,sortOrder), HttpStatus.OK);
    }

        @GetMapping("public/{employeeId}/details")
        @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER','TEAM_LEAD')")
        public ResponseEntity<PublicEmployeeDetails> getEmployeeDetails(@PathVariable String employeeId) {
            return new ResponseEntity<>(publicEmployeeService.getEmployeeDetails(employeeId),HttpStatus.OK);
        }

        @GetMapping("/{employeeId}/header")
        @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER','TEAM_LEAD')")
        public  ResponseEntity<PublicEmployeeDetails> getHeaderDetails(@PathVariable String employeeId) {
            return new ResponseEntity<>(publicEmployeeService.getHeaderDetails(employeeId),HttpStatus.OK);
        }
}
