package com.project.irs_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.irs_backend.entity.InventoryRequestItem;

public interface InventoryRequestItemRepository extends JpaRepository<InventoryRequestItem, Long> {

}
