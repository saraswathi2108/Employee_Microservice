package com.hrms.project.repository;

import com.hrms.project.entity.DegreeCertificates;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DegreeCertificatesRepository extends JpaRepository<DegreeCertificates,String> {
    List<DegreeCertificates> findByEmployee_EmployeeId(String employeeId);

    boolean existsByEmployeeEmployeeIdAndDegreeType(String employeeId, String degreeType);
}