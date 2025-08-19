package com.hrms.project.service;



import com.hrms.project.dto.ProjectDTO;
import com.hrms.project.dto.ProjectStatusDataDTO;
import com.hrms.project.dto.ProjectTableDataDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ProjectService {
    ProjectDTO saveProject(ProjectDTO projectDTO);

    List<ProjectDTO> getAllProjects(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder);

    ProjectDTO getProjectById(String id);

    ProjectDTO updateProject(String id, ProjectDTO projectDTO);

    ProjectDTO deleteProject(String id);

    List<ProjectTableDataDTO> getAllProjectsData(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder,String employeeId);

    List<ProjectStatusDataDTO> getProjectStatusData(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder,String employeeId);
}