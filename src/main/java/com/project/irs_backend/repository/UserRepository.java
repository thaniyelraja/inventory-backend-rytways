package com.project.irs_backend.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.irs_backend.dto.UserResponseDto;
import com.project.irs_backend.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = """
			SELECT COUNT(*) FROM users
			""", nativeQuery = true)
	Long countUsers();

	Optional<User> findByEmail(String email);

	@Query(value = """
			SELECT * FROM users WHERE :search = ''
			OR LOWER(name) LIKE LOWER(CONCAT('%', :search, '%'))
			OR LOWER(email) LIKE LOWER(CONCAT('%', :search, '%'))
			""", countQuery = """
			SELECT COUNT(*) FROM users WHERE :search = ''
			OR LOWER(name) LIKE LOWER(CONCAT('%', :search, '%'))
			OR LOWER(email) LIKE LOWER(CONCAT('%', :search, '%'))
			""", nativeQuery = true)
	Page<User> searchUsers(@Param("search") String search, Pageable pageable);

}
