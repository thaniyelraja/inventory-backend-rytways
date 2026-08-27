package com.project.irs_backend.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.irs_backend.dto.InventoryRequestApprovalDto;
import com.project.irs_backend.dto.InventoryRequestDto;
import com.project.irs_backend.entity.InventoryRequest;
import com.project.irs_backend.entity.Material;
import com.project.irs_backend.entity.User;
import com.project.irs_backend.enums.RequestStatus;
import com.project.irs_backend.repository.InventoryRequestRepository;
import com.project.irs_backend.repository.MaterialRepository;
import com.project.irs_backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryRequestService {

	private final InventoryRequestRepository inventoryRequestRepository;

	private final MaterialRepository materialRepository;

	private final UserRepository userRepository;

	@Transactional
	public InventoryRequest manageInventoryRequest(Long requestId, RequestStatus actionType,
			InventoryRequestApprovalDto dto) {
		InventoryRequest request = inventoryRequestRepository.findById(requestId)
				.orElseThrow(() -> new RuntimeException("Request not found"));

		if (actionType == RequestStatus.APPROVED) {
			Material material = request.getMaterial();
			int maxQuantity = Math.min(request.getRequestQuantity(), material.getAvailableQuantity());

			if (dto.getApprovedQuantity() == null) {
				throw new RuntimeException("Approved quantity is required");
			}
			if (dto.getApprovedQuantity() < 1 || dto.getApprovedQuantity() > maxQuantity) {
				throw new RuntimeException("Approved quantity can't exceed " + maxQuantity);
			}

			request.setApprovedQuantity(dto.getApprovedQuantity());
			request.setApprovalRemarks(dto.getApprovalRemarks());
			request.setRequestStatus(RequestStatus.APPROVED);
			request.setApprovedAt(LocalDateTime.now());

			material.setAvailableQuantity(material.getAvailableQuantity() - dto.getApprovedQuantity());
			materialRepository.save(material);
		} else if (actionType == RequestStatus.REJECTED) {
			if (dto.getRejectionRemarks() == null || dto.getRejectionRemarks().trim().isEmpty()) {
				throw new RuntimeException("Rejection remarks is required");
			}
			request.setRequestStatus(RequestStatus.REJECTED);
			request.setRejectedAt(LocalDateTime.now());
			request.setRejectionRemarks(dto.getRejectionRemarks());
		} else {
			throw new RuntimeException("Invalid request action");
		}
		return inventoryRequestRepository.save(request);

	}

	public List<InventoryRequest> createRequests(InventoryRequestDto dto) {
		User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new RuntimeException("User not found"));
		List<InventoryRequest> requests = dto.getItems().stream().map(itemDto -> {
			Material material = materialRepository.findById(itemDto.getMaterialId())
					.orElseThrow(() -> new RuntimeException("Material not found"));
			InventoryRequest request = new InventoryRequest();
			BeanUtils.copyProperties(itemDto, request);
			request.setRequestQuantity(itemDto.getQuantity());
			request.setUser(user);
			request.setMaterial(material);
			request.setRequestStatus(RequestStatus.PENDING);
			return request;
		}).toList();

		return inventoryRequestRepository.saveAll(requests);

	}

	public InventoryRequest createRequest(InventoryRequest request) {

		Long materialId = request.getMaterial().getMaterialId();
		Material material = materialRepository.findById(materialId)
				.orElseThrow(() -> new RuntimeException("Material Not found"));

		InventoryRequest newRequest = new InventoryRequest();
		BeanUtils.copyProperties(request, newRequest);
		newRequest.setMaterial(material);
		newRequest.setRequestStatus(RequestStatus.PENDING);
		newRequest.setRequestedAt(LocalDateTime.now());
		return inventoryRequestRepository.save(newRequest);
	}

	public Page<InventoryRequest> getAllRequest(String search, String status, Long userId, Pageable pageable) {
		return inventoryRequestRepository.searchAndFilterPerUser(search, status, userId, pageable);
	}

	public Page<InventoryRequest> getAllRequestsManage(String search, Pageable pageable) {
		return inventoryRequestRepository.searchAndFilterAllManage(search, pageable);
	}

	public Page<InventoryRequest> getAllRequestsView(String search, Pageable pageable) {
		return inventoryRequestRepository.searchAndFilterAllView(search, pageable);
	}

	public List<InventoryRequest> getRequestDetails(Long userId, Long materialId) {
		return inventoryRequestRepository.findByUser_UserIdAndMaterial_MaterialId(userId, materialId);
	}

}
