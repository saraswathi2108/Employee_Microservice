package com.hrms.project.repository;

import com.hrms.project.entity.TaskUpdate;
import com.hrms.project.configuration.TaskUpdateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskUpdateRepository extends JpaRepository<TaskUpdate, TaskUpdateId> {

}

