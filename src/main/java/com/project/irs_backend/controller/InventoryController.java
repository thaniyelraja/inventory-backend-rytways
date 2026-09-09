package com.project.irs_backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.irs_backend.dto.InventoryAddRequestDto;
import com.project.irs_backend.entity.Inventory;
import com.project.irs_backend.service.InventoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryController {

	private final InventoryService inventoryService;

	@GetMapping("/stocks")
	public ResponseEntity<Page<Inventory>> getStocks(@RequestParam(defaultValue = "") String search,
			@RequestParam(required = false) Long categoryId, Pageable pageable) {
		return ResponseEntity.ok(inventoryService.getStocks(search, categoryId, pageable));
	}

	@PostMapping("/add-stock")
	public ResponseEntity<InventoryAddRequestDto> addInventory(@RequestBody InventoryAddRequestDto dto) {

		return ResponseEntity.ok(inventoryService.addInventory(dto));

	}

	@PutMapping("/update-stock/{inventoryId}")
	public ResponseEntity<InventoryAddRequestDto> updateInventory(@PathVariable Long inventoryId,
			@RequestBody InventoryAddRequestDto dto) {
		return ResponseEntity.ok(inventoryService.updateInventory(inventoryId, dto));
	}

}
