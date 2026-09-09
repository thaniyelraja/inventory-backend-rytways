package com.project.irs_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.irs_backend.entity.InventoryRequestMessage;

public interface InventoryRequestMessageRepository extends JpaRepository<InventoryRequestMessage, Long> {

	List<InventoryRequestMessage> findByInventoryRequest_InventoryRequestIdOrderBySendedAtAsc(Long requestId);

	Optional<InventoryRequestMessage> findFirstByInventoryRequest_InventoryRequestIdOrderBySendedAtDesc(Long requestId);

}
