package com.project.irs_backend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrderItemResponse {
	
	private Long materialOrderItemId;
	private Long materialId;
	private Long materialCode;
	private String materialName;
	private Long quantity;
	private BigDecimal price;
	private BigDecimal totalPrice;

}
