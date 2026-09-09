package com.project.irs_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestItemDto {

	private Long inventoryId;
	private Integer quantity;

}
