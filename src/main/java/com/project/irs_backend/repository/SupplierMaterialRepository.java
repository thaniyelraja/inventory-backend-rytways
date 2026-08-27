package com.project.irs_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.irs_backend.entity.SupplierMaterial;

public interface SupplierMaterialRepository extends JpaRepository<SupplierMaterial, Long> {

	Optional<SupplierMaterial> findBySupplier_SupplierIdAndMaterial_MaterialId(Long suppliedId, Long materialId);

}
