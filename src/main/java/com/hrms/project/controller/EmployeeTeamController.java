package com.hrms.project.controller;

import com.hrms.project.dto.EmployeeTeamDTO;
import com.hrms.project.dto.TeamController;
import com.hrms.project.dto.TeamResponse;
import com.hrms.project.security.CheckEmployeeAccess;
import com.hrms.project.service.TeamServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/employee")

public class EmployeeTeamController {

    @Autowired
    private TeamServiceImpl teamService;

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER')")
    @PostMapping("/team")
    public ResponseEntity<TeamController> createTeam(@Valid @RequestBody TeamController teamController) {
        TeamController createdTeam = teamService.saveTeam(teamController);
        return new ResponseEntity<>(createdTeam, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('HR','ADMIN','MANAGER','TEAM_LEAD', 'EMPLOYEE')")
    @GetMapping("/team/{employeeId}")
    public ResponseEntity<List<TeamResponse>> getTeamAllEmployees(@PathVariable String employeeId) {
        List<TeamResponse> teamList = teamService.getTeamAllEmployees(employeeId);
        return ResponseEntity.ok(teamList);
   }

   @GetMapping("/team/employee/{teamId}")
   @PreAuthorize("hasAnyRole('HR','ADMIN','MANAGER','TEAM_LEAD', 'EMPLOYEE')")
   public ResponseEntity<List<TeamResponse>> getTeamById(@PathVariable String teamId) {
        List<TeamResponse> employeeList = teamService.getAllTeamEmployees(teamId);
        return ResponseEntity.ok(employeeList);
   }


    @PutMapping("/team/employee/{teamId}")
    @PreAuthorize("hasAnyRole('HR','ADMIN', 'MANAGER')")
    @CheckEmployeeAccess(param = "id", roles = {"ADMIN", "HR","TEAM_LEAD","MANAGER"})
    public ResponseEntity<String> updateTeam(@PathVariable String teamId,
                                             @Valid @RequestBody TeamController teamDTO) {
        return new ResponseEntity<>(teamService.UpdateTeam(teamId,teamDTO), HttpStatus.OK);
    }

 @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/{teamId}/team/employee")
 @PreAuthorize("hasAnyRole('ADMIN','EMPLOYEE','HR','TEAM_LEAD','MANAGER')")
 public ResponseEntity<EmployeeTeamDTO> getEmployeeByTeamId(@PathVariable Integer pageSize,
                                                            @PathVariable Integer pageNumber,
                                                            @PathVariable String sortBy,
                                                            @PathVariable String sortOrder,
                                                            @PathVariable String teamId) {
     return new ResponseEntity<>(teamService.getEmployeeByTeamId(pageNumber,pageSize,sortBy,sortOrder,teamId),HttpStatus.OK);
 }

    @GetMapping("/team/projects/{teamId}")
    @PreAuthorize("hasAnyRole('EMPLOYEE', 'HR','TEAM_LEAD','MANAGER')")
    public ResponseEntity<List<String>> getProjectsByTeam(@PathVariable String teamId) {
       return new ResponseEntity<>(teamService.getProjectsByTeam(teamId),HttpStatus.OK) ;
    }

    @DeleteMapping("/{teamId}/team")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<String> deleteTeam(@PathVariable String teamId) {
        return new ResponseEntity<>(teamService.deleteTeam(teamId),HttpStatus.OK);
    }
}