package com.hrms.project.controller;

import com.hrms.project.dto.EmployeeTeamDTO;
import com.hrms.project.dto.TeamController;
import com.hrms.project.dto.TeamResponse;
//import com.hrms.project.service.TeamService;
import com.hrms.project.service.TeamServiceImpl;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")


public class EmployeeTeamController {

    @Autowired
    private TeamServiceImpl teamService;



    //@PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping("/team")
    public ResponseEntity<TeamController> createTeam(@Valid @RequestBody TeamController teamController) {
        TeamController createdTeam = teamService.saveTeam(teamController);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    //@PreAuthorize("hasAnyRole('HR', 'EMPLOYEE')")
    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/employee/team/{employeeId}")
    public ResponseEntity<List<TeamResponse>> getAllEmployees(@PathVariable Integer pageSize,
                                                              @PathVariable Integer pageNumber,
                                                              @PathVariable String sortBy,
                                                              @PathVariable String sortOrder,
                                                              @PathVariable String employeeId) {
        List<TeamResponse> teamList = teamService.getTeamAllEmployees(pageNumber,pageSize,sortBy,sortOrder,employeeId);
        return ResponseEntity.ok(teamList);
   }

    //@PreAuthorize("hasAnyRole( 'HR', 'MANAGER')")
   @GetMapping("/team/employee/{teamId}")
   public ResponseEntity<List<TeamResponse>> getTeamById(@PathVariable String teamId) {
        List<TeamResponse> employeeList = teamService.getAllTeamEmployees(teamId);
        return ResponseEntity.ok(employeeList);
   }


 //   @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    @PutMapping("/team/employee/{teamId}")
    public ResponseEntity<String> updateTeam(@PathVariable String teamId,
                                             @Valid @RequestBody TeamController teamDTO) {
        return new ResponseEntity<>(teamService.UpdateTeam(teamId,teamDTO), HttpStatus.OK);
    }


 //   @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE')")
 @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/{teamId}/team/employee")
 public ResponseEntity<EmployeeTeamDTO> getEmployeeByTeamId(@PathVariable Integer pageSize,
                                                            @PathVariable Integer pageNumber,
                                                            @PathVariable String sortBy,
                                                            @PathVariable String sortOrder,
                                                            @PathVariable String teamId) {
     return new ResponseEntity<>(teamService.getEmployeeByTeamId(pageNumber,pageSize,sortBy,sortOrder,teamId),HttpStatus.OK);

 }

 //   @PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")
    @GetMapping("/team/projects/{teamId}")
    public ResponseEntity<List<String>> getProjectsByTeam(@PathVariable String teamId) {
       return new ResponseEntity<>(teamService.getProjectsByTeam(teamId),HttpStatus.OK) ;

    }

    @DeleteMapping("/{teamId}/team")
    public ResponseEntity<String> deleteTeam(@PathVariable String teamId) {
        return new ResponseEntity<>(teamService.deleteTeam(teamId),HttpStatus.OK);
    }


}