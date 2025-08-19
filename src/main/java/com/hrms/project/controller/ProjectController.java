package com.hrms.project.controller;

import com.hrms.project.dto.ProjectDTO;
import com.hrms.project.dto.ProjectStatusDataDTO;
import com.hrms.project.dto.ProjectTableDataDTO;
import com.hrms.project.service.ProjectService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")

public class ProjectController {

    @Autowired
    private ProjectService projectService;


    //    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEAM_LEAD')")
    @PostMapping("/project")
    public ResponseEntity<ProjectDTO> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        projectService.saveProject(projectDTO);
        return new ResponseEntity<>(projectDTO, HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD')")
    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/projects")
    public ResponseEntity<List<ProjectDTO>> getAllProjects(@PathVariable Integer pageNumber,
                                                           @PathVariable Integer pageSize,
                                                           @PathVariable String sortBy,
                                                           @PathVariable String sortOrder) {
        List<ProjectDTO> response=projectService.getAllProjects(pageNumber,pageSize,sortBy,sortOrder);
        return ResponseEntity.ok().body(response);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'HR', 'MANAGER', 'TEAM_LEAD')")
    @GetMapping("/project/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable String id) {
        ProjectDTO projectResponse= projectService.getProjectById(id);
        return ResponseEntity.ok().body(projectResponse);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEAM_LEAD')")
    @PutMapping("/project/{id}")
    public ResponseEntity<ProjectDTO> updateProject(@PathVariable String id, @Valid @RequestBody ProjectDTO projectDTO) {
        ProjectDTO updatedProject = projectService.updateProject(id, projectDTO);
        return new ResponseEntity<>(updatedProject, HttpStatus.CREATED);
    }

    //    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/project/{id}")
    public ResponseEntity<ProjectDTO> deleteProject(@PathVariable String id) {
        ProjectDTO deletedProject=projectService.deleteProject(id);
        return new ResponseEntity<>(deletedProject, HttpStatus.OK);
    }
    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/all/projectData/{employeeId}")
    public ResponseEntity<List<ProjectTableDataDTO>>  getAllProjectData(@PathVariable Integer pageNumber,
                                                                        @PathVariable Integer pageSize,
                                                                        @PathVariable String sortBy,
                                                                        @PathVariable String sortOrder,@PathVariable String  employeeId) {
        return new ResponseEntity<>(projectService.getAllProjectsData(pageNumber,pageSize,sortBy,sortOrder,employeeId),HttpStatus.OK);
    }
    @GetMapping("{pageNumber}/{pageSize}/{sortBy}/{sortOrder}/all/projectStatus/{employeeId}")
    public ResponseEntity<List<ProjectStatusDataDTO>> getAllProjectStatus(@PathVariable Integer pageNumber,
                                                                          @PathVariable Integer pageSize,
                                                                          @PathVariable String sortBy,
                                                                          @PathVariable String sortOrder,
                                                                          @PathVariable String employeeId) {

        return new ResponseEntity<>(projectService.getProjectStatusData(pageNumber,pageSize,sortBy,sortOrder,employeeId),HttpStatus.OK);

    }
}