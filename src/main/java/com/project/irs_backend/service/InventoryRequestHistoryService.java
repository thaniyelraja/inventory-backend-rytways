package com.project.irs_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.irs_backend.dto.InventoryRequestHistoryDto;
import com.project.irs_backend.dto.InventoryRequestHistoryItemDto;
import com.project.irs_backend.entity.InventoryRequestHistory;
import com.project.irs_backend.repository.InventoryRequestHistoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryRequestHistoryService {

	private final InventoryRequestHistoryRepository inventoryRequestHistoryRepository;

	public List<InventoryRequestHistory> getHistory(Long requestId) {

		return inventoryRequestHistoryRepository
				.findByInventoryRequest_InventoryRequestIdOrderByCreatedAtAsc(requestId);
	}

	public Page<InventoryRequestHistoryDto> getAllHistory(int page, int size, String search, LocalDateTime fromDate,
			LocalDateTime toDate) {

		Pageable pageable = PageRequest.of(page, size);

		Page<Long> requestIds = inventoryRequestHistoryRepository.findRequestIdsForHistory(
				search == null || search.trim().isEmpty() ? null : search.trim(), fromDate, toDate, pageable);

		List<InventoryRequestHistoryDto> content = requestIds.getContent().stream().map(requestId -> {

			List<InventoryRequestHistoryItemDto> history = inventoryRequestHistoryRepository
					.findByInventoryRequest_InventoryRequestIdOrderByCreatedAtAsc(requestId).stream()
					.map(item -> new InventoryRequestHistoryItemDto(item.getHistoryId(), item.getUser().getUserId(),
							item.getUser().getName(), item.getAction().name(), item.getCreatedAt()))
					.toList();

			return new InventoryRequestHistoryDto(requestId, history);
		}).toList();

		return new PageImpl<>(content, pageable, requestIds.getTotalElements());
	}

	public List<InventoryRequestHistoryItemDto> getHistoryForDownload(String search, LocalDateTime fromDate,
			LocalDateTime toDate) {
		return inventoryRequestHistoryRepository
				.findHistoryForDownload(search == null || search.trim().isEmpty() ? null : search.trim(), fromDate,
						toDate)
				.stream()
				.map(item -> new InventoryRequestHistoryItemDto(item.getHistoryId(), item.getUser().getUserId(),
						item.getUser().getName(), item.getAction().name(), item.getCreatedAt()))
				.toList();
	}
}