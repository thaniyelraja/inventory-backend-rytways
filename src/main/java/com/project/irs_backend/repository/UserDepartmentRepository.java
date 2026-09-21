package com.project.irs_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.irs_backend.entity.UserDepartment;
import com.project.irs_backend.enums.Role;

public interface UserDepartmentRepository extends JpaRepository<UserDepartment, Long> {

	List<UserDepartment> findByUser_UserId(Long userId);

	List<UserDepartment> findByDepartment_DepartmentId(Long departmentId);

	Optional<UserDepartment> findByUser_UserIdAndDepartment_DepartmentId(Long userId, Long departmentId);

	boolean existsByUser_UserIdAndDepartment_DepartmentId(Long userId, Long departmentId);

	List<UserDepartment> findByDepartment_DepartmentIdAndRole(Long departmentId, Role role);

	@Query(value = """
			SELECT COUNT(*) FROM user_department ud where ud.department_id = :departmentId
			and lower(ud.role) = lower(:role)
			and ud.user_id <> :userId
			""", nativeQuery = true)
	long countOtherHodsInDepartment(@Param("departmentId") Long departmentId, @Param("role") String role,
			@Param("userId") Long userId);

	@Query(value = """
			SELECT COUNT(DISTINCT user_id) FROM user_department WHERE LOWER(role) = LOWER(:role) AND user_id <> :userId
			""", nativeQuery = true)
	long countByRoleIgnoreCaseAndUser_UserIdNot(@Param("role") Role role, @Param("userId") Long userId);

	@Query(value = """
			SELECT COUNT(*) FROM user_department WHERE LOWER(role) = LOWER(:role)
			""", nativeQuery = true)
	long countByRole(@Param("role") Role role);

}
