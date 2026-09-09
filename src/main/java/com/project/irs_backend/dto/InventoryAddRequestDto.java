package com.project.irs_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryAddRequestDto {

	private Long materialId;

	private Long maxStockLevel;

	private Long reorderLevel;

	private Long availableQuantity;

}
