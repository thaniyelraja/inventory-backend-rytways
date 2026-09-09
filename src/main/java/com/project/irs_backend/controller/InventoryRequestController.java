package com.project.irs_backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import com.project.irs_backend.dto.InventoryRequestMessageDto;
import com.project.irs_backend.entity.InventoryRequest;
import com.project.irs_backend.entity.InventoryRequestMessage;
import com.project.irs_backend.repository.InventoryRequestRepository;
import com.project.irs_backend.service.InventoryRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory-request")
@RequiredArgsConstructor
public class InventoryRequestController {

	private final InventoryRequestService inventoryRequestService;

	private final InventoryRequestRepository inventoryRequestRepository;

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

	@GetMapping("/request")
	public ResponseEntity<Page<InventoryRequest>> getAllRequest(@RequestParam(defaultValue = "") String search,
			@RequestParam(defaultValue = "ALL") String status, @RequestParam Long userId, Pageable pageable) {
		return ResponseEntity.ok(inventoryRequestService.getAllRequest(search, status, userId, pageable));
	}

	@GetMapping("/manage")
	public ResponseEntity<Page<InventoryRequest>> getAllRequestsManage(@RequestParam Long hodUserId,
			@RequestParam(defaultValue = "") String search, Pageable pageable) {
		return ResponseEntity.ok(inventoryRequestService.getAllRequestsManage(hodUserId, search, pageable));
	}

	@PostMapping("/manage/{requestId}")
	public ResponseEntity<InventoryRequest> manageRequest(@PathVariable Long requestId, @RequestParam Long hodUserId,
			@RequestParam String action, @RequestBody InventoryRequestApprovalDto dto) {
		return ResponseEntity.ok(inventoryRequestService.manageRequest(requestId, hodUserId, action, dto));
	}

//	@PostMapping("/requests")
//	public ResponseEntity<List<InventoryRequest>> createRequests(@RequestBody InventoryRequestDto request) {
//		return ResponseEntity.ok(inventoryRequestService.createRequests(request));
//	}

//	
//	@PutMapping("/update/{requestId}")
//	public ResponseEntity<InventoryRequest> updateInventoryRequest(
//			@PathVariable Long requestId,
//			@RequestBody InventoryRequestDto dto
//			){
//		return ResponseEntity.ok(inventoryRequestService.updateInventoryRequest(requestId, dto));
//	}
//	
//	@DeleteMapping("/delete/{requestId}")
//	public ResponseEntity<Void> deleteInventoryRequest(@PathVariable Long requestId){
//		inventoryRequestService.deleteInventoryRequest(requestId);
//		return ResponseEntity.noContent().build();
//	}
//
//	@GetMapping("/requests-view")
//	public ResponseEntity<Page<InventoryRequest>> getAllRequestsView(@RequestParam(defaultValue = "") String search,
//			@RequestParam(defaultValue = "ALL") String status, Pageable pageable) {
//		return ResponseEntity.ok(inventoryRequestService.getAllRequestsView(search, pageable));
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
