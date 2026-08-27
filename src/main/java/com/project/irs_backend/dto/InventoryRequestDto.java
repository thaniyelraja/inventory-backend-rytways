package com.project.irs_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestDto {
	
	private Long userId;
	private List<InventoryRequestItemDto> items;

}
