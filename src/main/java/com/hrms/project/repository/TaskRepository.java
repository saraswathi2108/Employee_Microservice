package com.hrms.project.repository;

import com.hrms.project.configuration.TaskId;
import com.hrms.project.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, TaskId> {

    

    Page<Task> findByEmployee_EmployeeId(String employeeId, Pageable pageable);

    Page<Task> findByCreatedBy(String tlId, Pageable pageable);
}

