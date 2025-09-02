package com.hrms.project.service;

import com.hrms.project.client.TeamClient;
import com.hrms.project.entity.Employee;
import com.hrms.project.entity.Project;
import com.hrms.project.entity.Team;
import com.hrms.project.handlers.APIException;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.handlers.ProjectNotFoundException;
import com.hrms.project.handlers.TeamNotFoundException;
import com.hrms.project.dto.EmployeeTeamDTO;
import com.hrms.project.dto.EmployeeTeamResponse;
import com.hrms.project.dto.TeamController;
import com.hrms.project.dto.TeamResponse;
import com.hrms.project.repository.EmployeeRepository;
import com.hrms.project.repository.ProjectRepository;
import com.hrms.project.repository.TeamRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class TeamServiceImpl {
    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TeamClient teamClient;

    public TeamController saveTeam(TeamController teamController) {
        if (teamRepository.findById(teamController.getTeamId()).isPresent()) {
            throw new APIException("Team already exists with ID " + teamController.getTeamId());
        }

        Project project = projectRepository.findById(teamController.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID doensn't exists " + teamController.getProjectId()));
        if (project.getTeam() != null) {
            throw new APIException("Project is already assigned to team  with ID " + project.getTeam().getTeamId());
        }

        Set<Employee> employees = new HashSet<>();
        if (teamController.getEmployeeIds() != null && !teamController.getEmployeeIds().isEmpty()) {
            for (String employeeId : teamController.getEmployeeIds()) {
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));
                employees.add(employee);
            }
        }

        Team team = new Team();
        team.setTeamId(teamController.getTeamId());
        team.setTeamName(teamController.getTeamName());
        team.setTeamDescription(teamController.getTeamDescription());
        team.setEmployees(employees);
        Team savedTeam = teamRepository.save(team);


        project.setTeam(savedTeam);
        projectRepository.save(project);

        for (Employee employee : employees) {
            if (!employee.getProjects().contains(project)) {
                employee.getProjects().add(project);
            }
        }

        employeeRepository.saveAll(employees);
        teamClient.notifyTeamUpdate(savedTeam.getTeamId());
        return teamController;
    }


    public List<TeamResponse> getTeamAllEmployees(String employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id " + employeeId));

        List<Team> teamList = employee.getTeams();

        return teamList.stream().map(team -> {
            List<EmployeeTeamResponse> employeeList = team.getEmployees().stream()
                    .map(emp -> {
                        EmployeeTeamResponse response = new EmployeeTeamResponse();
                        response.setEmployeeId(emp.getEmployeeId());
                        response.setDisplayName(emp.getDisplayName());
                        response.setWorkEmail(emp.getWorkEmail());
                        response.setWorkNumber(emp.getWorkNumber());
                        response.setJobTitlePrimary(emp.getJobTitlePrimary());
                        return response;
                    }).toList();

            TeamResponse teamResponse = new TeamResponse();
            teamResponse.setTeamId(team.getTeamId());
            teamResponse.setTeamName(team.getTeamName());
            teamResponse.setEmployees(employeeList);

            return teamResponse;
        }).toList();
    }



    public List<TeamResponse> getAllTeamEmployees(String teamId) {
        Team teamDetails = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id " + teamId));

        Set<Employee> employeeDetails = teamDetails.getEmployees();


        List<EmployeeTeamResponse> EmployeeTeamResponseList = employeeDetails.stream()
                .map(employee -> {
                    EmployeeTeamResponse EmployeeTeamResponse = new EmployeeTeamResponse();

                    EmployeeTeamResponse.setEmployeeId(employee.getEmployeeId());
                    EmployeeTeamResponse.setDisplayName(employee.getDisplayName());
                    EmployeeTeamResponse.setWorkEmail(employee.getWorkEmail());
                    EmployeeTeamResponse.setWorkNumber(employee.getWorkNumber());
                    EmployeeTeamResponse.setJobTitlePrimary(employee.getJobTitlePrimary());

                    return EmployeeTeamResponse;
                })
                .toList();

        TeamResponse teamResponse = new TeamResponse();
        teamResponse.setTeamId(teamDetails.getTeamId());
        teamResponse.setTeamName(teamDetails.getTeamName());
        teamResponse.setEmployees(EmployeeTeamResponseList);
        return List.of(teamResponse);


    }

    public String UpdateTeam(String teamId, TeamController teamController) {


        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id " + teamId));

        Set<Employee> employees = new HashSet<>();
        if (teamController.getEmployeeIds() != null && !teamController.getEmployeeIds().isEmpty()) {
            for (String employeeId : teamController.getEmployeeIds()) {
                Employee employee = employeeRepository.findById(employeeId)
                        .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));
                employees.add(employee);
            }
        }
        team.setTeamName(teamController.getTeamName());
        team.setTeamDescription(teamController.getTeamDescription());
       team.getEmployees().addAll(employees);
        teamRepository.save(team);


//
//        List<Project> ongoingProjects = team.getProjects().stream()
//                .filter(project -> !"Completed".equalsIgnoreCase(project.getProjectStatus()))
//                .toList();


        Project project = projectRepository.findById(teamController.getProjectId())
                .orElseThrow(() -> new ProjectNotFoundException("Project with ID doensn't exists " + teamController.getProjectId()));

        if(project.getTeam() != null && !project.getTeam().getTeamId().equals(team.getTeamId())) {
            throw new APIException("Project is already assigned to another team");
        }


            project.setTeam(team);
            projectRepository.save(project);


        for (Employee employee : employees) {

            employee.getProjects().add(project);

        }
        employeeRepository.saveAll(employees);


        return "Data added to team successfully.";
    }

//    public void addProject(String teamId, TeamController teamController) {
//
//        Team team = teamRepository.findById(teamId)
//                .orElseThrow(() -> new TeamNotFoundException("Team not found with id " + teamId));
//
//        Project project = projectRepository.findById(teamController.getProjectId())
//                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id " + teamController.getProjectId()));
//
//        project.setTeam(team);
//        projectRepository.save(project);
//    }

    public List<String> getProjectsByTeam(String teamId) {


        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id " + teamId));

        return team.getProjects().stream()
                .map(Project->Project.getProjectId())
                .toList();
    }

    public EmployeeTeamDTO getEmployeeByTeamId(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder,String teamId) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAndOrder);


        List<String> employeeId=new ArrayList<>();

        List<Employee> employees=employeeRepository.findByTeams_TeamId(teamId,pageable).getContent();
        for(Employee employee:employees){
            employeeId.add(employee.getEmployeeId());
        }

        EmployeeTeamDTO employeeTeamDTO = new EmployeeTeamDTO();
        employeeTeamDTO.setTeamId(teamId);
        employeeTeamDTO.setEmployeeId(employeeId);
        return employeeTeamDTO;
    }

    public String deleteTeam(String teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found with id " + teamId));

        for (Employee employee : new HashSet<>(team.getEmployees())) {
            employee.getTeams().remove(team);
        }
        team.getEmployees().clear();

        for (Project project : team.getProjects()) {
            project.setTeam(null);
        }

        teamRepository.save(team);
        teamRepository.delete(team);

        return "Data removed from team successfully.";
    }



}


