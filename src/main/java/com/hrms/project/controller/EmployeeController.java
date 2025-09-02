package com.hrms.project.controller;

import com.hrms.project.dto.*;
import com.hrms.project.security.CheckEmployeeAccess;
import com.hrms.project.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/employee")

public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping
    public ResponseEntity<EmployeeDTO> createEmployee(
            @Valid @RequestBody EmployeeDTO employeeDTO) throws IOException {
        EmployeeDTO saved = employeeService.createData(employeeDTO);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDTO> getEmployeeById(@PathVariable String id) {
        EmployeeDTO employeeDetails = employeeService.getEmployeeById(id);
        return new ResponseEntity<>(employeeDetails, HttpStatus.OK);
    }

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/employees")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD', 'EMPLOYEE')")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(@PathVariable Integer pageSize,
                                                             @PathVariable Integer pageNumber,
                                                             @PathVariable String sortBy,
                                                             @PathVariable String sortOrder) {
        List<EmployeeDTO> employeeResponse = employeeService.getAllEmployees(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(employeeResponse, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    @CheckEmployeeAccess(param = "id", roles = {"ADMIN", "HR", "MANAGER"})
    public ResponseEntity<EmployeeDTO> updateEmployee(
                                                      @PathVariable String id,
                                                      @Valid @RequestBody EmployeeDTO employeeDTO){
        EmployeeDTO updatedEmployeeDetails = employeeService.updateEmployee(id, employeeDTO);
        return new ResponseEntity<>(updatedEmployeeDetails, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    public ResponseEntity<EmployeeDTO> deleteEmployee(@PathVariable String id) {
        EmployeeDTO deletedEmployee = employeeService.deleteEmployee(id);
        return new ResponseEntity<>(deletedEmployee, HttpStatus.OK);
    }

    @GetMapping("/{employeeId}/address")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD', 'EMPLOYEE')")
    public ResponseEntity<AddressDTO> getAddress(@PathVariable String employeeId) {
        AddressDTO addressDTO = employeeService.getAddress(employeeId);
        return new ResponseEntity<>(addressDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/all/employee/address")
    public ResponseEntity<List<AddressDTO>> getAllAddress(@PathVariable Integer pageSize,
                                                          @PathVariable Integer pageNumber,
                                                          @PathVariable String sortBy,
                                                          @PathVariable String sortOrder) {
        List<AddressDTO> addressDTOList = employeeService.getAllAddress(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(addressDTOList, HttpStatus.OK);
    }

    @PutMapping("/{employeeId}/address")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    @CheckEmployeeAccess(param = "id", roles = {"EMPLOYEE","ADMIN", "HR", "MANAGER"})
    public ResponseEntity<AddressDTO> updateAddress(@PathVariable String employeeId,
                                                    @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO updatedAddressDTO = employeeService.updateEmployeeAddress(employeeId, addressDTO);
        return new ResponseEntity<>(updatedAddressDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{employeeId}/primary/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD', 'EMPLOYEE')")
    public ResponseEntity<EmployeePrimaryDetailsDTO> getEmployeePrimaryDetails(@PathVariable String employeeId) {
        EmployeePrimaryDetailsDTO primaryDetails = employeeService.getEmployeePrimaryDetails(employeeId);
        return new ResponseEntity<>(primaryDetails, HttpStatus.OK);
    }

    @PutMapping("/{employeeId}/primary/details")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    @CheckEmployeeAccess(param = "id", roles = {"EMPLOYEE","ADMIN", "HR", "MANAGER"})
    public ResponseEntity<EmployeePrimaryDetailsDTO> updateEmployeePrimaryDetails(@PathVariable String employeeId,
                                                                                  @Valid @RequestBody EmployeePrimaryDetailsDTO employeePrimaryDetailsDTO) {
        EmployeePrimaryDetailsDTO updatedPrimaryDetails = employeeService.updateEmployeeDetails(employeeId, employeePrimaryDetailsDTO);
        return new ResponseEntity<>(updatedPrimaryDetails, HttpStatus.CREATED);
    }

    @GetMapping("{employeeId}/job/details")
    public ResponseEntity<JobDetailsDTO> getJobDetails(@PathVariable String employeeId) {
        JobDetailsDTO jobDetailsDTO = employeeService.getJobDetails(employeeId);
        return new ResponseEntity<>(jobDetailsDTO, HttpStatus.OK);
    }

    @PutMapping("/{employeeId}/job/details")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    @CheckEmployeeAccess(param = "id", roles = {"EMPLOYEE","ADMIN", "HR", "MANAGER"})
    public ResponseEntity<JobDetailsDTO> updateJobDetails(@PathVariable String employeeId,
                                                          @Valid  @RequestBody JobDetailsDTO jobDetailsDTO) {
        JobDetailsDTO updatedJobDetails = employeeService.updateJobDetails(employeeId, jobDetailsDTO);
        return new ResponseEntity<>(updatedJobDetails, HttpStatus.OK);
    }

    @GetMapping("/{employeeId}/contact")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD', 'EMPLOYEE')")
    public ResponseEntity<ContactDetailsDTO> getContactDetails(@PathVariable String employeeId) {
        ContactDetailsDTO contactDetailsDTO = employeeService.getEmployeeContactDetails(employeeId);
        return new ResponseEntity<>(contactDetailsDTO, HttpStatus.OK);
    }

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/all/employee/contact")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD', 'EMPLOYEE')")
    public ResponseEntity<List<ContactDetailsDTO>> getAllContactDetails(@PathVariable Integer pageNumber,
                                                                        @PathVariable Integer pageSize,
                                                                        @PathVariable String sortBy,
                                                                        @PathVariable String sortOrder) {
        List<ContactDetailsDTO> contactDetailsDTO = employeeService.getAllEmployeeContactDetails(pageNumber,pageSize,sortBy,sortOrder);
        return new ResponseEntity<>(contactDetailsDTO, HttpStatus.OK);
    }

    @PutMapping("/{employeeId}/contact")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'ADMIN', 'HR', 'MANAGER')")
    @CheckEmployeeAccess(param = "id", roles = {"EMPLOYEE","ADMIN", "HR", "MANAGER"})
    public ResponseEntity<ContactDetailsDTO> updateContactDetails(@PathVariable String employeeId,
                                                                  @Valid @RequestBody ContactDetailsDTO contactDetailsDTO) {
        ContactDetailsDTO updatedContactDetails = employeeService.updateContactDetails(employeeId, contactDetailsDTO);
        return new ResponseEntity<>(updatedContactDetails, HttpStatus.CREATED);
    }
}
