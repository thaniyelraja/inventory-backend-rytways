package com.project.irs_backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.irs_backend.dto.InventoryRequestApprovalDto;
import com.project.irs_backend.dto.InventoryRequestDto;
import com.project.irs_backend.dto.InventoryRequestHistoryDto;
import com.project.irs_backend.dto.InventoryRequestHistoryItemDto;
import com.project.irs_backend.dto.InventoryRequestMessageDto;
import com.project.irs_backend.entity.InventoryRequest;
import com.project.irs_backend.entity.InventoryRequestHistory;
import com.project.irs_backend.entity.InventoryRequestMessage;
import com.project.irs_backend.repository.InventoryRequestRepository;
import com.project.irs_backend.service.InventoryRequestHistoryService;
import com.project.irs_backend.service.InventoryRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory-request")
@RequiredArgsConstructor
public class InventoryRequestController {

	private final InventoryRequestService inventoryRequestService;

	private final InventoryRequestRepository inventoryRequestRepository;

	private final InventoryRequestHistoryService inventoryRequestHistoryService;

	@PostMapping("/manage/{requestId}/message")
	public ResponseEntity<InventoryRequestMessage> sendMessage(@PathVariable Long requestId,
			@RequestBody InventoryRequestMessageDto dto) {

		return ResponseEntity.ok(inventoryRequestService.sendMessage(requestId, dto));
	}

	@GetMapping("/manage/{requestId}/messages")
	public ResponseEntity<List<InventoryRequestMessage>> getMessages(@PathVariable Long requestId) {

		return ResponseEntity.ok(inventoryRequestService.getMessages(requestId));
	}

	@PostMapping("/request")
	public ResponseEntity<InventoryRequest> createRequest(@RequestBody InventoryRequestDto request) {
		return ResponseEntity.ok(inventoryRequestService.createRequest(request));
	}

	@PutMapping("/{requestId}/clarification/read")
	public ResponseEntity<InventoryRequest> markAsRead(@PathVariable Long requestId, @RequestParam Long userId) {
		return ResponseEntity.ok(inventoryRequestService.markAsRead(requestId, userId));
	}

	@GetMapping("/daily-report")
	public ResponseEntity<List<InventoryRequest>> getDailyReport(@RequestParam Long hodUserId,
			@RequestParam Long departmentId) {

		return ResponseEntity.ok(inventoryRequestService.getDailyReport(hodUserId, departmentId));
	}

	@GetMapping("{requestId}/history")
	public ResponseEntity<List<InventoryRequestHistory>> getHistory(@PathVariable Long requestId) {
		return ResponseEntity.ok(inventoryRequestHistoryService.getHistory(requestId));
	}

	@GetMapping("/history")
	public ResponseEntity<Page<InventoryRequestHistoryDto>> getAllHistory(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "9") int size, @RequestParam(required = false) String search,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
		LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;
		return ResponseEntity
				.ok(inventoryRequestHistoryService.getAllHistory(page, size, search, fromDateTime, toDateTime));
	}

	@GetMapping("/request")
	public ResponseEntity<Page<InventoryRequest>> getAllRequest(@RequestParam(defaultValue = "") String search,
			@RequestParam(defaultValue = "ALL") String status,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
			@RequestParam Long departmentId, @RequestParam Long userId, Pageable pageable) {
		return ResponseEntity.ok(inventoryRequestService.getAllRequest(search, status, fromDate, toDate, departmentId,
				userId, pageable));
	}

	@GetMapping("/manage")
	public ResponseEntity<Page<InventoryRequest>> getAllRequestsManage(@RequestParam Long hodUserId,
			@RequestParam Long departmentId, @RequestParam(defaultValue = "") String search, Pageable pageable) {
		return ResponseEntity
				.ok(inventoryRequestService.getAllRequestsManage(hodUserId, departmentId, search, pageable));
	}

	@GetMapping("/view")
	public ResponseEntity<Page<InventoryRequest>> getAllRequestsView(@RequestParam Long hodUserId,
			@RequestParam Long departmentId, @RequestParam(defaultValue = "") String search, Pageable pageable) {
		return ResponseEntity.ok(inventoryRequestService.getAllRequestsView(hodUserId, departmentId, search, pageable));
	}

	@PostMapping("/manage/{requestId}")
	public ResponseEntity<InventoryRequest> manageRequest(@PathVariable Long requestId, @RequestParam Long hodUserId,
			@RequestParam String action, @RequestBody InventoryRequestApprovalDto dto) {
		return ResponseEntity.ok(inventoryRequestService.manageRequest(requestId, hodUserId, action, dto));
	}

	@PutMapping("/update/{requestId}")
	public ResponseEntity<InventoryRequest> updateInventoryRequest(@PathVariable Long requestId,
			@RequestBody InventoryRequestDto dto) {
		return ResponseEntity.ok(inventoryRequestService.updateInventoryRequest(requestId, dto));
	}

	@GetMapping("/history/download")
	public ResponseEntity<List<InventoryRequestHistoryItemDto>> downloadhistory(
			@RequestParam(required = false) String search,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
		LocalDateTime fromDateTime = fromDate != null ? fromDate.atStartOfDay() : null;
		LocalDateTime toDateTime = toDate != null ? toDate.plusDays(1).atStartOfDay() : null;
		return ResponseEntity
				.ok(inventoryRequestHistoryService.getHistoryForDownload(search, fromDateTime, toDateTime));
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteInventoryRequest(@RequestParam Long requestId, @RequestParam Long userId) {
		inventoryRequestService.cancelRequest(requestId, userId);
		return ResponseEntity.noContent().build();
	}

//	@PostMapping("/requests")
//	public ResponseEntity<List<InventoryRequest>> createRequests(@RequestBody InventoryRequestDto request) {
//		return ResponseEntity.ok(inventoryRequestService.createRequests(request));
//	}
//
//	@GetMapping("/view")
//	public ResponseEntity<List<InventoryRequest>> getRequestDetails(@RequestParam Long userId,
//			@RequestParam Long materialId) {
//		List<InventoryRequest> requests = inventoryRequestService.getRequestDetails(userId, materialId);
//		if (requests == null) {
//			return ResponseEntity.notFound().build();
//		}
//		return ResponseEntity.ok(requests);
//	}
//	
//	@PutMapping("/manage/{requestId}/{actionType}")
//	public ResponseEntity<InventoryRequest> manageInventoryRequest(
//			@PathVariable Long requestId,
//			@PathVariable RequestStatus actionType,
//			@RequestBody InventoryRequestApprovalDto dto
//			){
//		return ResponseEntity.ok(
//				inventoryRequestService.manageInventoryRequest(requestId, actionType, dto));
//				
//	}
//
}
