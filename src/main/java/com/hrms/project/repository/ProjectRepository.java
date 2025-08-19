package com.hrms.project.repository;


import com.hrms.project.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ProjectRepository extends JpaRepository<Project, String> {


    Page<Project> findByEmployees_EmployeeId(String employeeId, Pageable pageable);
}
