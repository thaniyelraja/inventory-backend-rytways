package com.project.irs_backend.service;

import java.time.LocalDate;
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
import com.project.irs_backend.entity.Department;
import com.project.irs_backend.entity.Inventory;
import com.project.irs_backend.entity.InventoryRequest;
import com.project.irs_backend.entity.InventoryRequestHistory;
import com.project.irs_backend.entity.InventoryRequestItem;
import com.project.irs_backend.entity.InventoryRequestMessage;
import com.project.irs_backend.entity.Material;
import com.project.irs_backend.entity.Status;
import com.project.irs_backend.entity.User;
import com.project.irs_backend.entity.UserDepartment;
import com.project.irs_backend.enums.HistoryAction;
import com.project.irs_backend.enums.Role;
import com.project.irs_backend.repository.DepartmentRepository;
import com.project.irs_backend.repository.InventoryRepository;
import com.project.irs_backend.repository.InventoryRequestHistoryRepository;
import com.project.irs_backend.repository.InventoryRequestItemRepository;
import com.project.irs_backend.repository.InventoryRequestMessageRepository;
import com.project.irs_backend.repository.InventoryRequestRepository;
import com.project.irs_backend.repository.MaterialRepository;
import com.project.irs_backend.repository.StatusRepository;
import com.project.irs_backend.repository.UserDepartmentRepository;
import com.project.irs_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryRequestService {

	private final InventoryRequestRepository inventoryRequestRepository;

	private final InventoryRequestItemRepository inventoryRequestItemRepository;

	private final UserDepartmentRepository userDepartmentRepository;

	private final StatusRepository statusRepository;

	private final MaterialRepository materialRepository;

	private final InventoryRepository inventoryRepository;

	private final UserRepository userRepository;

	private final InventoryRequestMessageRepository inventoryRequestMessageRepository;

	private final DepartmentRepository departmentRepository;

	private final JavaMailSender mailSender;

	private final InventoryRequestHistoryRepository inventoryRequestHistoryRepository;

	@Transactional
	public InventoryRequest createRequest(InventoryRequestDto request) {

		// Get User
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		if (request.getItems() == null || request.getItems().isEmpty()) {
			throw new RuntimeException("Atleast one item is required");
		}

		Department department = departmentRepository.findById(request.getDepartmentId())
				.orElseThrow(() -> new RuntimeException("Department not found"));

		userDepartmentRepository
				.findByUser_UserIdAndDepartment_DepartmentId(request.getUserId(), request.getDepartmentId())
				.orElseThrow(() -> new RuntimeException("User is not assigned to this department"));

		Status pendingStatus = statusRepository.findByStatusCode("PENDING")
				.orElseThrow(() -> new RuntimeException("PENDING status not found"));

		Status idleClarificationStatus = statusRepository.findByStatusCode("IDLE")
				.orElseThrow(() -> new RuntimeException("IDLE clarification status not found"));

		// Create request header
		InventoryRequest newRequest = new InventoryRequest();
		newRequest.setUser(user);
		newRequest.setDepartment(department);
		newRequest.setStatus(pendingStatus);
		newRequest.setClarificationStatus(idleClarificationStatus);
		newRequest.setRequestedAt(LocalDateTime.now());

		// save request first then create the request items
		InventoryRequest savedRequest = inventoryRequestRepository.save(newRequest);
		saveHistory(savedRequest, user, HistoryAction.CREATED);

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

	public Page<InventoryRequest> getAllRequest(String search, String status, LocalDate fromDate, LocalDate toDate,
			Long departmentId, Long userId, Pageable pageable) {

		return inventoryRequestRepository.searchAndFilterPerUser(search, status, fromDate, toDate, departmentId, userId,
				pageable);
	}

	public Page<InventoryRequest> getAllRequestsManage(Long hodUserId, Long departmentId, String search,
			Pageable pageable) {
		UserDepartment hodDepartment = userDepartmentRepository
				.findByUser_UserIdAndDepartment_DepartmentId(hodUserId, departmentId)
				.orElseThrow(() -> new RuntimeException("You are not assigned to this department"));
		if (hodDepartment.getRole() != Role.HOD) {
			throw new RuntimeException("You are not authorized to manage requests");
		}
		return inventoryRequestRepository.searchAndFilterAllManage(departmentId, search, pageable);
	}

	@Transactional
	public InventoryRequest manageRequest(Long requestId, Long hodUserId, String action,
			InventoryRequestApprovalDto dto) {

		InventoryRequest request = inventoryRequestRepository.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));

		User hod = userRepository.findById(hodUserId).orElseThrow(() -> new RuntimeException("HOD not found"));

		Long departmentId = request.getDepartment().getDepartmentId();

		UserDepartment hodDepartment = userDepartmentRepository
				.findByUser_UserIdAndDepartment_DepartmentId(hodUserId, departmentId)
				.orElseThrow(() -> new RuntimeException("You cannot manage this request"));

		if (hodDepartment.getRole() != Role.HOD) {
			throw new RuntimeException("You cannot manage this request");
		}

		if (!"PENDING".equals(request.getStatus().getStatusCode())) {
			throw new RuntimeException("Only pending requests can be managed");
		}

		if ("APPROVED".equals(action)) {

			if (dto.getItems() == null || dto.getItems().isEmpty()) {

				throw new RuntimeException("Approval items are required");
			}

			if (dto.getItems().size() != request.getItems().size()) {
				throw new RuntimeException("All request items are required");
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
			saveHistory(request, hod, HistoryAction.APPROVED);

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
			saveHistory(request, hod, HistoryAction.REJECTED);

		} else if ("CLARIFY".equals(action)) {

			if (dto.getClarificationMessage() == null || dto.getClarificationMessage().trim().isEmpty()) {

				throw new RuntimeException("Clarification message is required");
			}

			InventoryRequestMessage message = new InventoryRequestMessage();

			message.setInventoryRequest(request);
			message.setSenderUser(hod);
			message.setMessage(dto.getClarificationMessage().trim());

			inventoryRequestMessageRepository.save(message);

			Status requestedStatus = statusRepository.findByStatusCode("REQUESTED")
					.orElseThrow(() -> new RuntimeException("REQUESTED status not found"));
			request.setClarificationStatus(requestedStatus);
			request.setUpdatedAt(LocalDateTime.now());
			saveHistory(request, hod, HistoryAction.CLARIFICATION_REQUESTED);

		} else {
			throw new RuntimeException("Invalid action");
		}

		return inventoryRequestRepository.save(request);
	}

	public List<InventoryRequest> getDailyReport(Long hodUserId, Long departmentId) {

		UserDepartment hodDepartment = userDepartmentRepository
				.findByUser_UserIdAndDepartment_DepartmentId(hodUserId, departmentId)
				.orElseThrow(() -> new RuntimeException("You are not assigned to this department"));

		if (hodDepartment.getRole() != Role.HOD) {
			throw new RuntimeException("You are not authorized to view daily report");
		}

		LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
		LocalDateTime endOfDay = LocalDate.now().plusDays(1).atStartOfDay();

		return inventoryRequestRepository
				.findByDepartment_DepartmentIdAndRequestedAtGreaterThanEqualAndRequestedAtLessThan(departmentId,
						startOfDay, endOfDay);
	}

	@Transactional
	public InventoryRequest markAsRead(Long requestId, Long userId) {
		InventoryRequest request = inventoryRequestRepository.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));
		if (!request.getUser().getUserId().equals(userId)) {
			throw new RuntimeException("You cannot update this request");
		}
		if (!"REQUESTED".equals(request.getClarificationStatus().getStatusCode())) {
			throw new RuntimeException("No clarification is pending");
		}
		Status viewedStatus = statusRepository.findByStatusCode("VIEWED")
				.orElseThrow(() -> new RuntimeException("VIEWED status not found"));

		request.setClarificationStatus(viewedStatus);
		saveHistory(request, request.getUser(), HistoryAction.CLARIFICATION_VIEWED);
		return inventoryRequestRepository.save(request);
	}

	private void saveHistory(InventoryRequest request, User user, HistoryAction action) {
		InventoryRequestHistory history = new InventoryRequestHistory();

		history.setInventoryRequest(request);
		history.setUser(user);
		history.setAction(action);
		history.setCreatedAt(LocalDateTime.now());

		inventoryRequestHistoryRepository.save(history);
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

		Long departmentId = request.getDepartment().getDepartmentId();

		UserDepartment userDepartment = userDepartmentRepository
				.findByUser_UserIdAndDepartment_DepartmentId(sender.getUserId(), departmentId)
				.orElseThrow(() -> new RuntimeException("User is not assigned to this department"));

		InventoryRequestMessage message = new InventoryRequestMessage();

		message.setInventoryRequest(request);
		message.setSenderUser(sender);
		message.setMessage(dto.getMessage().trim());

		if (userDepartment.getRole() == Role.HOD) {
			Status requestedStatus = statusRepository.findByStatusCode("REQUESTED")
					.orElseThrow(() -> new RuntimeException("REQUESTED status not found"));
			request.setClarificationStatus(requestedStatus);
			saveHistory(request, sender, HistoryAction.CLARIFICATION_REQUESTED);

		} else if (userDepartment.getRole() == Role.USER) {
			if (!request.getUser().getUserId().equals(sender.getUserId())) {
				throw new RuntimeException("You cannot reply to this request");
			}
			Status repliedStatus = statusRepository.findByStatusCode("REPLIED")
					.orElseThrow(() -> new RuntimeException("REPLIED Status not found"));
			request.setClarificationStatus(repliedStatus);
			saveHistory(request, sender, HistoryAction.CLARIFICATION_REPLIED);

		} else {
			throw new RuntimeException("Invalid role");
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

	@Scheduled(cron = "0 0 12 * * *")
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
			Long departmentId = request.getDepartment().getDepartmentId();

			List<UserDepartment> hodDepartments = userDepartmentRepository
					.findByDepartment_DepartmentIdAndRole(departmentId, Role.HOD);

			try {
				String requestNumber = String.format("%03d", request.getInventoryRequestId());
				String subject = "Inventory Request Pending - Request #" + request.getInventoryRequestId();
				String message = "Hello, \n\n" + "Inventory request #" + requestNumber
						+ " has been pending for more than 3 days.\n\n" + "Requetsed By: " + user.getName() + "\n"
						+ "Department: " + request.getDepartment().getDepartmentName() + "\n\n"
						+ "Please review this request.\n\n" + "Regards, \n" + "Inventory Management System,\n"
						+ "Rytways.";
				if (user.getEmail() != null && !user.getEmail().isBlank()) {
					SimpleMailMessage userMail = new SimpleMailMessage();
					userMail.setTo(user.getEmail());
					userMail.setSubject(subject);
					userMail.setText(message);
					mailSender.send(userMail);
				}
				for (UserDepartment hodDepartment : hodDepartments) {
					User hod = hodDepartment.getUser();

					if (hod == null || hod.getEmail() == null || hod.getEmail().isBlank()) {
						continue;
					}
					SimpleMailMessage hodMail = new SimpleMailMessage();
					hodMail.setTo(hod.getEmail());
					hodMail.setSubject(subject);
					hodMail.setText(message);
					mailSender.send(hodMail);
				}
				request.setReminderSent(true);
				inventoryRequestRepository.save(request);
			} catch (Exception e) {
				System.err.println("Failed to send reminder for request" + request.getInventoryRequestId() + ": "
						+ e.getMessage());
			}
		}
	}

	@Transactional
	public InventoryRequest updateInventoryRequest(Long requestId, InventoryRequestDto dto) {
		InventoryRequest request = inventoryRequestRepository.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));
		if (!"PENDING".equals(request.getStatus().getStatusCode())) {
			throw new RuntimeException("Only pending request can be edited");
		}

		if (dto.getItems() == null || dto.getItems().isEmpty()) {
			throw new RuntimeException("Request item is required");
		}

		Long userId = request.getUser().getUserId();
		Long departmentId = request.getDepartment().getDepartmentId();

		userDepartmentRepository.findByUser_UserIdAndDepartment_DepartmentId(userId, departmentId)
				.orElseThrow(() -> new RuntimeException("User is not assigned to this department"));

		List<Long> inventoryIds = dto.getItems().stream().map(InventoryRequestItemDto::getInventoryId).toList();
		if (inventoryIds.size() != inventoryIds.stream().distinct().count()) {
			throw new RuntimeException("Duplicate invetory items not allowed");
		}

		List<InventoryRequestItem> existingItems = inventoryRequestItemRepository
				.findByInventoryRequest_InventoryRequestId(requestId);

		java.util.Set<Long> updatedInventoryIds = new java.util.HashSet<>();

		for (InventoryRequestItemDto itemDto : dto.getItems()) {

			Long inventoryId = itemDto.getInventoryId();
			Integer quantity = itemDto.getQuantity();

			if (inventoryId == null) {
				throw new RuntimeException("Inventory is required");
			}

			if (quantity == null || quantity < 1) {
				throw new RuntimeException("Request quantity must be at least 1");
			}

			Inventory inventory = inventoryRepository.findById(inventoryId)
					.orElseThrow(() -> new RuntimeException("Inventory not found"));

			if (quantity > inventory.getAvailableQuantity()) {
				throw new RuntimeException("Quantity can't be higher than available quantity for "
						+ inventory.getMaterial().getMaterialName());
			}

			updatedInventoryIds.add(inventoryId);

			InventoryRequestItem existingItem = existingItems.stream()
					.filter(item -> item.getInventory().getInventoryId().equals(inventoryId)).findFirst().orElse(null);

			if (existingItem != null) {
				existingItem.setRequestQuantity(quantity);
				inventoryRequestItemRepository.save(existingItem);
			} else {
				InventoryRequestItem newItem = new InventoryRequestItem();
				newItem.setInventoryRequest(request);
				newItem.setInventory(inventory);
				newItem.setRequestQuantity(quantity);
				inventoryRequestItemRepository.save(newItem);
			}
		}

		for (InventoryRequestItem existingItem : existingItems) {

			Long inventoryId = existingItem.getInventory().getInventoryId();

			if (!updatedInventoryIds.contains(inventoryId)) {
				inventoryRequestItemRepository.delete(existingItem);
			}
		}

		request.setUpdatedAt(LocalDateTime.now());
		saveHistory(request, request.getUser(), HistoryAction.UPDATED);
		return inventoryRequestRepository.save(request);

	}

	public Page<InventoryRequest> getAllRequestsView(Long hodUserId, Long departmentId, String search,
			Pageable pageable) {
		UserDepartment hodDepartment = userDepartmentRepository
				.findByUser_UserIdAndDepartment_DepartmentId(hodUserId, departmentId)
				.orElseThrow(() -> new RuntimeException("You are not assigned to this department"));
		if (hodDepartment.getRole() != Role.HOD) {
			throw new RuntimeException("You are not authorized to manage requests");
		}
		return inventoryRequestRepository.searchAndFilterAllView(departmentId, search, pageable);
	}

	@Transactional
	public InventoryRequest cancelRequest(Long requestId, Long userId) {

		InventoryRequest request = inventoryRequestRepository.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));

		if (!request.getUser().getUserId().equals(userId)) {
			throw new RuntimeException("You cannot cancel this request");
		}

		if (!"PENDING".equals(request.getStatus().getStatusCode())) {
			throw new RuntimeException("Only pending requests can be cancelled");
		}

		Status cancelledStatus = statusRepository.findByStatusCode("CANCELLED")
				.orElseThrow(() -> new RuntimeException("CANCELLED status not found"));

		request.setStatus(cancelledStatus);

		InventoryRequestHistory history = new InventoryRequestHistory();
		history.setInventoryRequest(request);
		history.setUser(request.getUser());
		history.setAction(HistoryAction.CANCELLED);
		inventoryRequestHistoryRepository.save(history);

		return inventoryRequestRepository.save(request);
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
//	public List<InventoryRequest> getRequestDetails(Long userId, Long materialId) {
//		return inventoryRequestRepository.findByUser_UserIdAndMaterial_MaterialId(userId, materialId);
//	}
//
}
