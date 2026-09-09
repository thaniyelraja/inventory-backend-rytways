package com.project.irs_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.irs_backend.entity.UserDepartment;

public interface UserDepartmentRepository extends JpaRepository<UserDepartment, Long> {

	List<UserDepartment> findByUser_UserId(Long userId);

	List<UserDepartment> findByDepartment_DepartmentId(Long departmentId);

	Optional<UserDepartment> findByUser_UserIdAndDepartment_DepartmentId(Long userId, Long departmentId);

}
