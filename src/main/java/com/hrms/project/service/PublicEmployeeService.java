package com.hrms.project.service;

import com.hrms.project.dto.AchievementsDTO;
import com.hrms.project.entity.Achievements;
import com.hrms.project.entity.Employee;
import com.hrms.project.entity.Project;
import com.hrms.project.handlers.EmployeeNotFoundException;
import com.hrms.project.dto.PublicEmployeeDetails;
import com.hrms.project.dto.PublicProjectDTO;
import com.hrms.project.repository.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublicEmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private S3Service s3Service;


    public List<PublicEmployeeDetails> getAllEmployees(Integer pageNumber,Integer pageSize, String sortBy, String sortOrder) {

        Sort sortByAndOrder=sortOrder.equalsIgnoreCase("asc")
                ?Sort.by(sortBy).ascending()
                :Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        List<Employee> employees=employeeRepository.findAll(pageable).getContent();

        return employees.stream()
                .map(emp ->
                {
                    PublicEmployeeDetails publicEmployeeDetails = new PublicEmployeeDetails();

                    publicEmployeeDetails.setEmployeeId(emp.getEmployeeId());
                    publicEmployeeDetails.setName(emp.getDisplayName());
                    publicEmployeeDetails.setWorkEmail(emp.getWorkEmail());
                    publicEmployeeDetails.setLocation(emp.getLocation());
                    publicEmployeeDetails.setJobTitlePrimary(emp.getJobTitlePrimary());

                    if(emp.getDepartment() != null) {
                        publicEmployeeDetails.setDepartment(emp.getDepartment().getDepartmentName());
                    }
                    if(emp.getEmployeeImage()!=null) {
                        publicEmployeeDetails.setEmployeeImage(s3Service.generatePresignedUrl(emp.getEmployeeImage()));
                    }

                    return publicEmployeeDetails;
                })
                .toList();
    }


    public PublicEmployeeDetails getEmployeeDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id: " + employeeId + " not found"));

        PublicEmployeeDetails publicEmployeeDetails = new PublicEmployeeDetails();

        publicEmployeeDetails.setEmployeeId(employee.getEmployeeId());
        publicEmployeeDetails.setName(employee.getDisplayName());
        publicEmployeeDetails.setWorkEmail(employee.getWorkEmail());
        publicEmployeeDetails.setLocation(employee.getLocation());

        if(employee.getEmployeeImage()!=null) {
            publicEmployeeDetails.setEmployeeImage(s3Service.generatePresignedUrl(employee.getEmployeeImage()));
        }

        publicEmployeeDetails.setJobTitlePrimary(employee.getJobTitlePrimary());
        if(employee.getDepartment() != null) {
            publicEmployeeDetails.setDepartment(employee.getDepartment().getDepartmentName());
        }

        publicEmployeeDetails.setContact(employee.getWorkNumber());

        publicEmployeeDetails.setSkills(employee.getSkills());
        List<Achievements> achievements=employee.getAchievements();

        publicEmployeeDetails.setAchievements(
                achievements.stream().map(
                        achievement ->{
                            AchievementsDTO dto=new AchievementsDTO();
                            dto.setCertificationName(achievement.getCertificationName());
                            dto.setIssuingAuthorityName(achievement.getIssuingAuthorityName());
                            return dto;
                        }

                ).toList()
        );



        List<Project> projects=employee.getProjects();

        publicEmployeeDetails.setProjects( projects.stream().map(
                project ->
                {

                    PublicProjectDTO projectDTO = new  PublicProjectDTO();
                    projectDTO.setProjectName(project.getTitle());
                    projectDTO.setProjectDescription(project.getDescription());
                    return projectDTO;
                }).toList()
        );

        return publicEmployeeDetails;
        }

    public PublicEmployeeDetails getHeaderDetails(String employeeId) {

        Employee employee=employeeRepository.findById(employeeId).orElseThrow(
                () -> new EmployeeNotFoundException("Employee with id: " + employeeId + " not found"));

        PublicEmployeeDetails publicEmployeeDetails = new PublicEmployeeDetails();
        publicEmployeeDetails.setEmployeeId(employee.getEmployeeId());
        publicEmployeeDetails.setName(employee.getDisplayName());
        publicEmployeeDetails.setWorkEmail(employee.getWorkEmail());
        publicEmployeeDetails.setLocation(employee.getLocation());
        if(employee.getEmployeeImage()!=null) {
            publicEmployeeDetails.setEmployeeImage(s3Service.generatePresignedUrl(employee.getEmployeeImage()));
        }
        publicEmployeeDetails.setJobTitlePrimary(employee.getJobTitlePrimary());

        if(employee.getDepartment() != null) {
            publicEmployeeDetails.setDepartment(employee.getDepartment().getDepartmentName());
        }        publicEmployeeDetails.setContact(employee.getWorkNumber());

        return publicEmployeeDetails;

    }
}
