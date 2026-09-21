package com.project.irs_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.irs_backend.entity.InventoryRequestItem;

public interface InventoryRequestItemRepository extends JpaRepository<InventoryRequestItem, Long> {

	List<InventoryRequestItem> findByInventoryRequest_InventoryRequestId(Long requestId);

}
