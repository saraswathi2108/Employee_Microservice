package com.hrms.project.controller;

import com.hrms.project.dto.ProjectDTO;
import com.hrms.project.dto.ProjectStatusDataDTO;
import com.hrms.project.dto.ProjectTableDataDTO;
import com.hrms.project.security.CheckEmployeeAccess;
import com.hrms.project.service.ProjectService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")

public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping("/project")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        projectService.saveProject(projectDTO);
        return new ResponseEntity<>(projectDTO, HttpStatus.CREATED);
    }

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/projects")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD')")
    public ResponseEntity<List<ProjectDTO>> getAllProjects(@PathVariable Integer pageNumber,
                                                           @PathVariable Integer pageSize,
                                                           @PathVariable String sortBy,
                                                           @PathVariable String sortOrder) {
        List<ProjectDTO> response=projectService.getAllProjects(pageNumber,pageSize,sortBy,sortOrder);
        return ResponseEntity.ok().body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD')")
    @GetMapping("/project/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable String id) {
        ProjectDTO projectResponse= projectService.getProjectById(id);
        return ResponseEntity.ok().body(projectResponse);
    }

    @PutMapping("/project/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEAM_LEAD')")
   @CheckEmployeeAccess(param = "id", roles = {"ADMIN", "HR","MANAGER","TEAM_LEAD"})
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable String id,
                                                    @Valid @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
        return new ResponseEntity<>(updatedProject, HttpStatus.CREATED);
    }

    @DeleteMapping("/project/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProjectDTO> deleteProject(@PathVariable String id) {
        ProjectDTO deletedProject=projectService.deleteProject(id);
        return new ResponseEntity<>(deletedProject, HttpStatus.OK);
    }

    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/all/projectData/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD')")
    public ResponseEntity<List<ProjectTableDataDTO>>  getAllProjectData(@PathVariable Integer pageNumber,
                                                                        @PathVariable Integer pageSize,
                                                                        @PathVariable String sortBy,
                                                                        @PathVariable String sortOrder,@PathVariable String  employeeId) {
        return new ResponseEntity<>(projectService.getAllProjectsData(pageNumber,pageSize,sortBy,sortOrder,employeeId),HttpStatus.OK);
    }
    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/all/projectStatus/{employeeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD')")
    public ResponseEntity<List<ProjectStatusDataDTO>> getAllProjectStatus(@PathVariable Integer pageNumber,
                                                                          @PathVariable Integer pageSize,
                                                                          @PathVariable String sortBy,
                                                                          @PathVariable String sortOrder,
                                                                          @PathVariable String employeeId) {
        return new ResponseEntity<>(projectService.getProjectStatusData(pageNumber,pageSize,sortBy,sortOrder,employeeId),HttpStatus.OK);
    }
}