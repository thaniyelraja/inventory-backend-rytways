package com.project.irs_backend.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.irs_backend.dto.InventoryRequestApprovalDto;
import com.project.irs_backend.dto.InventoryRequestApprovalItemDto;
import com.project.irs_backend.dto.InventoryRequestDto;
import com.project.irs_backend.dto.InventoryRequestItemDto;
import com.project.irs_backend.dto.InventoryRequestMessageDto;
import com.project.irs_backend.entity.Inventory;
import com.project.irs_backend.entity.InventoryRequest;
import com.project.irs_backend.entity.InventoryRequestItem;
import com.project.irs_backend.entity.InventoryRequestMessage;
import com.project.irs_backend.entity.Material;
import com.project.irs_backend.entity.Status;
import com.project.irs_backend.entity.User;
import com.project.irs_backend.enums.Role;
import com.project.irs_backend.repository.InventoryRepository;
import com.project.irs_backend.repository.InventoryRequestItemRepository;
import com.project.irs_backend.repository.InventoryRequestMessageRepository;
import com.project.irs_backend.repository.InventoryRequestRepository;
import com.project.irs_backend.repository.MaterialRepository;
import com.project.irs_backend.repository.StatusRepository;
import com.project.irs_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryRequestService {

	private final InventoryRequestRepository inventoryRequestRepository;

	private final InventoryRequestItemRepository inventoryRequestItemRepository;

	private final StatusRepository statusRepository;

	private final MaterialRepository materialRepository;

	private final InventoryRepository inventoryRepository;

	private final UserRepository userRepository;

	private final InventoryRequestMessageRepository inventoryRequestMessageRepository;

	private final JavaMailSender mailSender;

	@Transactional
	public InventoryRequest createRequest(InventoryRequestDto request) {

		// Get User
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new RuntimeException("Atleast one item is required");
		}

		Status pendingStatus = statusRepository.findByStatusCode("PENDING")
				.orElseThrow(() -> new RuntimeException("PENDING status not found"));

		// Create request header
		InventoryRequest newRequest = new InventoryRequest();
		newRequest.setUser(user);
		newRequest.setStatus(pendingStatus);
		newRequest.setRequestedAt(LocalDateTime.now());

		// save request first then create the request items
		InventoryRequest savedRequest = inventoryRequestRepository.save(newRequest);

		// create request items
		for (InventoryRequestItemDto itemDto : request.getItems()) {
			Inventory inventory = inventoryRepository.findById(itemDto.getInventoryId())
					.orElseThrow(() -> new RuntimeException("Inventory not found"));

			InventoryRequestItem item = new InventoryRequestItem();
			item.setInventoryRequest(savedRequest);
			item.setInventory(inventory);
			item.setRequestQuantity(itemDto.getQuantity());

			inventoryRequestItemRepository.save(item);
		}

		return savedRequest;
	}

	public Page<InventoryRequest> getAllRequest(String search, String status, Long userId, Pageable pageable) {

		return inventoryRequestRepository.searchAndFilterPerUser(search, status, userId, pageable);
	}

	public Page<InventoryRequest> getAllRequestsManage(Long hodUserId, String search, Pageable pageable) {
		return inventoryRequestRepository.searchAndFilterAllManage(hodUserId, search, pageable);
	}

	@Transactional
	public InventoryRequest manageRequest(Long requestId, Long hodUserId, String action,
			InventoryRequestApprovalDto dto) {

		InventoryRequest request = inventoryRequestRepository.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));

		User hod = userRepository.findById(hodUserId).orElseThrow(() -> new RuntimeException("HOD not found"));

		if (request.getUser().getDepartment() == null || hod.getDepartment() == null
				|| !request.getUser().getDepartment().getDepartmentId().equals(hod.getDepartment().getDepartmentId())) {

			throw new RuntimeException("You cannot manage this request");
		}

		if (!"PENDING".equals(request.getStatus().getStatusCode())) {
			throw new RuntimeException("Only pending requests can be managed");
		}

		if ("APPROVED".equals(action)) {

			if (dto.getItems() == null || dto.getItems().isEmpty()) {

				throw new RuntimeException("Approval items are required");
			}

			for (InventoryRequestApprovalItemDto approvalItem : dto.getItems()) {

				InventoryRequestItem item = inventoryRequestItemRepository
						.findById(approvalItem.getInventoryRequestItemId())
						.orElseThrow(() -> new RuntimeException("Request item not found"));

				if (!item.getInventoryRequest().getInventoryRequestId().equals(requestId)) {

					throw new RuntimeException("Invalid request item");
				}

				Integer approvedQuantity = approvalItem.getApprovedQuantity();

				if (approvedQuantity == null || approvedQuantity <= 0) {

					throw new RuntimeException("Approved quantity must be greater than 0");
				}

				if (approvedQuantity > item.getRequestQuantity()) {

					throw new RuntimeException("Approved quantity cannot exceed requested quantity");
				}

				Inventory inventory = item.getInventory();

				if (approvedQuantity > inventory.getAvailableQuantity()) {

					throw new RuntimeException("Insufficient stock for " + inventory.getMaterial().getMaterialName());
				}

				inventory.setAvailableQuantity(inventory.getAvailableQuantity() - approvedQuantity);

				Long quantity = inventory.getAvailableQuantity();

				String statusCode;

				if (quantity <= 0) {
					statusCode = "OUT_OF_STOCK";
				} else if (quantity <= inventory.getReorderLevel()) {
					statusCode = "LOW_STOCK";
				} else if (quantity > inventory.getMaxStockLevel()) {
					statusCode = "OVER_STOCK";
				} else {
					statusCode = "IN_STOCK";
				}

				Status stockStatus = statusRepository.findByStatusCode(statusCode)
						.orElseThrow(() -> new RuntimeException(statusCode + " status not found"));

				inventory.setStatus(stockStatus);
				item.setApprovedQuantity(approvedQuantity);

				inventoryRepository.save(inventory);
				inventoryRequestItemRepository.save(item);
			}

			Status approvedStatus = statusRepository.findByStatusCode("APPROVED")
					.orElseThrow(() -> new RuntimeException("APPROVED status not found"));

			request.setStatus(approvedStatus);
			request.setApprovedAt(LocalDateTime.now());
			request.setApprovalRemarks(dto.getApprovalRemarks());
			request.setUpdatedAt(LocalDateTime.now());

		} else if ("REJECTED".equals(action)) {

			if (dto.getRejectionRemarks() == null || dto.getRejectionRemarks().trim().isEmpty()) {

				throw new RuntimeException("Rejection remarks are required");
			}

			Status rejectedStatus = statusRepository.findByStatusCode("REJECTED")
					.orElseThrow(() -> new RuntimeException("REJECTED status not found"));

			request.setStatus(rejectedStatus);
			request.setRejectedAt(LocalDateTime.now());
			request.setRejectionRemarks(dto.getRejectionRemarks());
			request.setUpdatedAt(LocalDateTime.now());

		} else if ("CLARIFY".equals(action)) {

			if (dto.getClarificationMessage() == null || dto.getClarificationMessage().trim().isEmpty()) {

				throw new RuntimeException("Clarification message is required");
			}

			InventoryRequestMessage message = new InventoryRequestMessage();

			message.setInventoryRequest(request);
			message.setSenderUser(hod);
			message.setMessage(dto.getClarificationMessage().trim());

			inventoryRequestMessageRepository.save(message);

			request.setClarificationPending(true);
			request.setUpdatedAt(LocalDateTime.now());

		} else {
			throw new RuntimeException("Invalid action");
		}

		return inventoryRequestRepository.save(request);
	}

	@Transactional
	public InventoryRequestMessage sendMessage(Long requestId, InventoryRequestMessageDto dto) {
		InventoryRequest request = inventoryRequestRepository.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));
		User sender = userRepository.findById(dto.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));
		if (dto.getMessage() == null || dto.getMessage().trim().isEmpty()) {
			throw new RuntimeException("Message is required");
		}
		InventoryRequestMessage message = new InventoryRequestMessage();
		message.setInventoryRequest(request);
		message.setSenderUser(sender);
		message.setMessage(dto.getMessage().trim());
		if (sender.getRole() == Role.HOD) {
			request.setClarificationPending(true);
		} else if (sender.getRole() == Role.USER) {
			request.setClarificationPending(false);
		}

		inventoryRequestMessageRepository.save(message);
		inventoryRequestRepository.save(request);

		return message;
	}

	public List<InventoryRequestMessage> getMessages(Long requestId) {

		if (!inventoryRequestRepository.existsById(requestId)) {
			throw new RuntimeException("Request not found");
		}
		return inventoryRequestMessageRepository.findByInventoryRequest_InventoryRequestIdOrderBySendedAtAsc(requestId);
	}

	@Scheduled(fixedRate = 3600000)
	@Transactional
	public void sendPendingRequestReminders() {
		LocalDateTime cutoff = LocalDateTime.now().minusDays(3);

		List<InventoryRequest> requests = inventoryRequestRepository
				.findByStatus_StatusCodeAndRequestedAtBeforeAndReminderSentFalse("PENDING", cutoff);

		for (InventoryRequest request : requests) {
			User user = request.getUser();
			if (user == null || user.getEmail() == null) {
				continue;
			}

			try {
				SimpleMailMessage mail = new SimpleMailMessage();
				mail.setTo(user.getEmail());
				mail.setSubject("Inventory Request Pending - Request #" + request.getInventoryRequestId());
				mail.setText("Hello" + user.getName() + ", \n\n" + "Your inventory request #"
						+ String.format("%03d", request.getInventoryRequestId())
						+ " has been pending for more than 3 days. \n\n" + "Regards, \n"
						+ "Inventory Management System");

				mailSender.send(mail);
				request.setReminderSent(true);
				inventoryRequestRepository.save(request);
			} catch (Exception e) {
				System.err.println("Failed to send reminder for request" + request.getInventoryRequestId() + ": "
						+ e.getMessage());
			}
		}
	}

//	public List<InventoryRequest> createRequests(InventoryRequestDto dto) {
//		User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
//		List<InventoryRequest> requests = dto.getItems().stream().map(itemDto -> {
//			Material material = materialRepository.findById(itemDto.getMaterialId())
//					.orElseThrow(() -> new RuntimeException("Material not found"));
//			InventoryRequest request = new InventoryRequest();
//
//			BeanUtils.copyProperties(itemDto, request);
//			request.setRequestQuantity(itemDto.getQuantity());
//			request.setUser(user);
//			request.setMaterial(material);
//			request.setRequestStatus(RequestStatus.PENDING);
//			return request;
//		}).toList();
//
//		return inventoryRequestRepository.saveAll(requests);
//
//	}
//
//	

//
//	@Transactional
//	public InventoryRequest manageInventoryRequest(Long requestId, RequestStatus actionType,
//			InventoryRequestApprovalDto dto) {
//		InventoryRequest request = inventoryRequestRepository.findById(requestId)
//				.orElseThrow(() -> new RuntimeException("Request not found"));
//
////		if (actionType == RequestStatus.APPROVED) {
////			Material material = request.getMaterial();
////			int maxQuantity = Math.min(request.getRequestQuantity(), material.getAvailableQuantity());
////
////			if (dto.getApprovedQuantity() == null) {
////				throw new RuntimeException("Approved quantity is required");
////			}
////			if (dto.getApprovedQuantity() < 1 || dto.getApprovedQuantity() > maxQuantity) {
////				throw new RuntimeException("Approved quantity can't exceed " + maxQuantity);
////			}
////
////			request.setApprovedQuantity(dto.getApprovedQuantity());
////			request.setApprovalRemarks(dto.getApprovalRemarks());
////			request.setRequestStatus(RequestStatus.APPROVED);
////			request.setApprovedAt(LocalDateTime.now());
////
////			material.setAvailableQuantity(material.getAvailableQuantity() - dto.getApprovedQuantity());
////			materialRepository.save(material);
////		} else if (actionType == RequestStatus.REJECTED) {
////			if (dto.getRejectionRemarks() == null || dto.getRejectionRemarks().trim().isEmpty()) {
////				throw new RuntimeException("Rejection remarks is required");
////			}
////			request.setRequestStatus(RequestStatus.REJECTED);
////			request.setRejectedAt(LocalDateTime.now());
////			request.setRejectionRemarks(dto.getRejectionRemarks());
////		} else {
////			throw new RuntimeException("Invalid request action");
////		}
//		return inventoryRequestRepository.save(request);
//
//	}
//
//	public InventoryRequest updateInventoryRequest(Long requestId, InventoryRequestDto dto) {
//		InventoryRequest request = inventoryRequestRepository.findById(requestId)
//				.orElseThrow(() -> new RuntimeException("Request not found"));
////		if (request.getRequestStatus() != RequestStatus.PENDING) {
////			throw new RuntimeException("Only pending request can be edited");
////		}
//
//		if (dto.getItems() == null || dto.getItems().isEmpty()) {
//			throw new RuntimeException("Request item is required");
//		}
//
//		InventoryRequestItemDto item = dto.getItems().get(0);
//		Integer quantity = item.getQuantity();
//
//		if (quantity == null || quantity < 1) {
//			throw new RuntimeException("Request quantity must be atleast 1");
//		}
////		Material material = request.getMaterial();
////		if (quantity > material.getAvailableQuantity()) {
////			throw new RuntimeException("Quantity can't be higher than avilable quantity");
////		}
////		request.setRequestQuantity(quantity);
//		return inventoryRequestRepository.save(request);
//
//	}
//
//	public void deleteInventoryRequest(Long requestId) {
//		InventoryRequest request = inventoryRequestRepository.findById(requestId)
//				.orElseThrow(() -> new RuntimeException("Request not found"));
//
////		if (request.getRequestStatus() != RequestStatus.PENDING) {
////			throw new RuntimeException("Only pending requests can be cancelled");
////		}
//		inventoryRequestRepository.delete(request);
//	}
//
//	public Page<InventoryRequest> getAllRequestsView(String search, Pageable pageable) {
//		return inventoryRequestRepository.searchAndFilterAllView(search, pageable);
//	}
//
//	public List<InventoryRequest> getRequestDetails(Long userId, Long materialId) {
//		return inventoryRequestRepository.findByUser_UserIdAndMaterial_MaterialId(userId, materialId);
//	}
//
}
