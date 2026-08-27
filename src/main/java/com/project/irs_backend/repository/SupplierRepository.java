package com.project.irs_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.irs_backend.entity.Supplier;

public interface SupplierRepository extends JpaRepository<Supplier, Long>{

}
