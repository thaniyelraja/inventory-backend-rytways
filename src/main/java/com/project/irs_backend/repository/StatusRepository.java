package com.project.irs_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.irs_backend.entity.Status;

public interface StatusRepository extends JpaRepository<Status, Long> {

	Optional<Status> findByStatusCode(String statusCode);

}
