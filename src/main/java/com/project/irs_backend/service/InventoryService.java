package com.project.irs_backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.irs_backend.dto.InventoryAddRequestDto;
import com.project.irs_backend.dto.InventoryRequestDto;
import com.project.irs_backend.entity.Inventory;
import com.project.irs_backend.entity.Material;
import com.project.irs_backend.entity.Status;
import com.project.irs_backend.repository.InventoryRepository;
import com.project.irs_backend.repository.MaterialRepository;
import com.project.irs_backend.repository.StatusRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService {

	private final InventoryRepository inventoryRepository;

	private final StatusRepository statusRepository;

	private final MaterialRepository materialRepository;

	public Page<Inventory> getStocks(String search, Long categoryId, Pageable pageable) {

		return inventoryRepository.getStocks(search == null ? "" : search.trim(), categoryId, pageable);

	}

	public Status calculateStockStatus(Long availableQuantity, Long reorderLevel, Long maxStockLevel) {

		if (availableQuantity <= 0) {
			return statusRepository.findByStatusCode("OUT_OF_STOCK")
					.orElseThrow(() -> new RuntimeException("Stock status not found"));
		}

		if (availableQuantity <= reorderLevel && availableQuantity > 0) {
			return statusRepository.findByStatusCode("LOW_STOCK")
					.orElseThrow(() -> new RuntimeException("Stock status not found"));
		}
		if (availableQuantity >= maxStockLevel) {
			return statusRepository.findByStatusCode("OVER_STOCK")
					.orElseThrow(() -> new RuntimeException("Stock status not found"));
		}

		return statusRepository.findByStatusCode("IN_STOCK")
				.orElseThrow(() -> new RuntimeException("Stock status not found"));
	}

	public InventoryAddRequestDto addInventory(InventoryAddRequestDto dto) {
		Material material = materialRepository.findById(dto.getMaterialId())
				.orElseThrow(() -> new RuntimeException("Material not found"));

		if (inventoryRepository.existsByMaterialMaterialId(dto.getMaterialId())) {
			throw new RuntimeException("Inventory already exists for this material");
		}

		Inventory inventory = new Inventory();

		BeanUtils.copyProperties(dto, inventory);
		inventory.setMaterial(material);
		inventory.setStatus(
				calculateStockStatus(dto.getAvailableQuantity(), dto.getReorderLevel(), dto.getMaxStockLevel()));

		Inventory savedInventory = inventoryRepository.save(inventory);
		InventoryAddRequestDto response = new InventoryAddRequestDto();
		BeanUtils.copyProperties(savedInventory, response);
		response.setMaterialId(savedInventory.getMaterial().getMaterialId());

		return response;
	}

	public InventoryAddRequestDto updateInventory(Long inventoryId, InventoryAddRequestDto dto) {
		Inventory inventory = inventoryRepository.findById(inventoryId)
				.orElseThrow(() -> new RuntimeException("Inventory not found"));
		BeanUtils.copyProperties(inventory, dto);
		inventory.setStatus(
				calculateStockStatus(dto.getAvailableQuantity(), dto.getReorderLevel(), dto.getMaxStockLevel()));

		Inventory savedInventory = inventoryRepository.save(inventory);

		InventoryAddRequestDto response = new InventoryAddRequestDto();
		BeanUtils.copyProperties(savedInventory, response);
		response.setMaterialId(savedInventory.getMaterial().getMaterialId());
		return response;
	}

}
