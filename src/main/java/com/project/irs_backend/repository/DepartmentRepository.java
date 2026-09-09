package com.project.irs_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.irs_backend.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

}
