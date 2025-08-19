package com.hrms.project.service;

import com.hrms.project.dto.ProjectStatusDataDTO;
import com.hrms.project.dto.ProjectTableDataDTO;
import com.hrms.project.entity.Employee;
import com.hrms.project.entity.Project;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.handlers.ProjectNotFoundException;
import com.hrms.project.dto.ProjectDTO;
import com.hrms.project.repository.DepartmentRepository;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.ProjectRepository;
import com.hrms.project.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private S3Service s3Service;

    @Override
    public ProjectDTO saveProject(ProjectDTO projectDTO) {
        if(projectRepository.findById(projectDTO.getProjectId()).isPresent())
        {
            throw new APIException("Project already exists with ID "+projectDTO.getProjectId());
        }

        Project project = new Project();

        project.setProjectId(projectDTO.getProjectId());
        project.setTitle(projectDTO.getTitle());
        project.setDescription(projectDTO.getDescription());
        project.setProjectStatus(projectDTO.getProjectStatus());
        project.setProjectPriority(projectDTO.getProjectPriority());
        project.setClient(projectDTO.getClient());
        project.setStartDate(projectDTO.getStartDate());
        project.setEndDate(projectDTO.getEndDate());

        projectRepository.save(project);

        return projectDTO;
    }


    @Override
    public List<ProjectDTO> getAllProjects(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAndOrder);


        List<Project> projects = projectRepository.findAll(pageable).getContent();

        List<ProjectDTO> responseList=new ArrayList<>();

        for (Project project : projects) {
            ProjectDTO response=new ProjectDTO();

            response.setProjectId(project.getProjectId());
            response.setTitle(project.getTitle());
            response.setClient(project.getClient());
            response.setDescription(project.getDescription());
            response.setProjectStatus(project.getProjectStatus());
            response.setStartDate(project.getStartDate());
            response.setEndDate(project.getEndDate());
            response.setProjectPriority(project.getProjectPriority());


            responseList.add(response);

        }


        return responseList;


    }



    @Override
    public ProjectDTO getProjectById(String id) {
        Project project=projectRepository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException("Project not found with id "+id));

        ProjectDTO response=new ProjectDTO();
        response.setProjectId(project.getProjectId());
        response.setTitle(project.getTitle());
        response.setClient(project.getClient());
        response.setDescription(project.getDescription());
        response.setProjectStatus(project.getProjectStatus());

        response.setStartDate(project.getStartDate());
        response.setEndDate(project.getEndDate());

        response.setProjectPriority(project.getProjectPriority());




        return response;
    }





    @Override
    public ProjectDTO updateProject(String id, ProjectDTO projectDTO) {

        Project projectByID=projectRepository.findById(id)
                .orElseThrow(()->new ProjectNotFoundException("Project not found with id "+id));

        projectByID.setProjectId(projectDTO.getProjectId());
        projectByID.setTitle(projectDTO.getTitle());
        projectByID.setClient(projectDTO.getClient());

        projectByID.setDescription(projectDTO.getDescription());

        projectByID.setProjectPriority(projectDTO.getProjectPriority());
        projectByID.setProjectStatus(projectDTO.getProjectStatus());
        projectByID.setStartDate(projectDTO.getStartDate());
        projectByID.setEndDate(projectDTO.getEndDate());

        Project savedProject = projectRepository.save(projectByID);

        projectRepository.save(savedProject);
        return projectDTO;
    }


    @Override
    public ProjectDTO deleteProject(String id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id "+id));

        for (Employee emp : project.getEmployees()) {
            emp.getProjects().remove(project);
            employeeRepository.save(emp);
        }
        project.getEmployees().clear();

        projectRepository.delete(project);
        return modelMapper.map(project, ProjectDTO.class);
    }
    @Override
    public List<ProjectTableDataDTO> getAllProjectsData(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder,String employeeId) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAndOrder);

        Page<Project> projectPage = projectRepository.findByEmployees_EmployeeId(employeeId, pageable);

        return projectPage.stream()
                .map(project -> {
                    ProjectTableDataDTO response = new ProjectTableDataDTO();
                    response.setProject_id(project.getProjectId());
                    response.setProject_name(project.getTitle());
                    response.setStatus(project.getProjectStatus());
                    response.setStart_date(project.getStartDate());
                    response.setEnd_date(project.getEndDate());
                    response.setDetails(project.getDescription());
                    response.setPriority(project.getProjectPriority());


                    List<String> employeeTeam = project.getEmployees().stream()
                            .map(Employee->
                                    s3Service.generatePresignedUrl(Employee.getEmployeeImage()))
                            .toList();

                    response.setEmployee_team(employeeTeam);

                    return response;
                })
                .toList();
    }



    @Override
    public List<ProjectStatusDataDTO> getProjectStatusData(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder,String employeeId) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Project> projectPage = projectRepository.findByEmployees_EmployeeId(employeeId, pageable);

        return projectPage.stream()
                .map(project -> {
                    ProjectStatusDataDTO dto = new ProjectStatusDataDTO();
                    dto.setProject_id(project.getProjectId());
                    dto.setProject_name(project.getTitle());
                    dto.setProject_status(project.getProjectStatus());

                    Period period = Period.between(project.getStartDate(), project.getEndDate());

                    StringBuilder durationBuilder = new StringBuilder();
                    if (period.getYears() > 0) {
                        durationBuilder.append(period.getYears()).append(" year");
                        if (period.getYears() > 1) durationBuilder.append("s");
                        durationBuilder.append(" ");
                    }
                    if (period.getMonths() > 0) {
                        durationBuilder.append(period.getMonths()).append(" month");
                        if (period.getMonths() > 1) durationBuilder.append("s");
                    }
                    if (period.getYears() == 0 && period.getMonths() == 0) {
                        durationBuilder.append(period.getDays()).append(" days");
                    }

                    dto.setDuration(durationBuilder.toString().trim());

                    return dto;
                })
                .toList();
    }

}