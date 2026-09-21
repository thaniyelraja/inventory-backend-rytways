package com.project.irs_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.irs_backend.entity.InventoryRequestHistory;

public interface InventoryRequestHistoryRepository extends JpaRepository<InventoryRequestHistory, Long> {

	List<InventoryRequestHistory> findByInventoryRequest_InventoryRequestIdOrderByCreatedAtAsc(Long inventoryRequestId);

	List<InventoryRequestHistory> findAllByOrderByCreatedAtDesc();

	@Query(value = """
			SELECT DISTINCT h.inventory_request_id FROM inventory_request_history h WHERE
			(:search IS NULL OR CAST(h.inventory_request_id AS CHAR) LIKE CONCAT('%', :search, '%'))
			AND (:fromDate IS NULL OR h.created_at >= :fromDate)
			AND (:toDate IS NULL OR h.created_at < :toDate)
			ORDER BY h.inventory_request_id DESC
			""", countQuery = """
			SELECT COUNT(DISTINCT h.inventory_request_id)
			FROM inventory_request_history h
			WHERE (:search IS NULL OR CAST(h.inventory_request_id AS CHAR) LIKE CONCAT('%', :search, '%'))
			AND (:fromDate IS NULL OR h.created_at >= :fromDate)
			AND (:toDate IS NULL OR h.created_at < :toDate)
			""", nativeQuery = true)
	Page<Long> findRequestIdsForHistory(@Param("search") String search, @Param("fromDate") LocalDateTime fromDate,
			@Param("toDate") LocalDateTime toDate, Pageable pageable);

	@Query(value = """
			SELECT h.* FROM inventory_request_history h WHERE (:search IS NULL OR
				CAST(h.inventory_request_id AS CHAR) LIKE CONCAT('%', :search, '%'))
				AND (:fromDate IS NULL OR h.created_at >= :fromDate)
				AND (:toDate IS NULL OR h.created_at < :toDate)
				ORDER BY h.created_at ASC
			""", nativeQuery = true)
	List<InventoryRequestHistory> findHistoryForDownload(@Param("search") String search,
			@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

}
