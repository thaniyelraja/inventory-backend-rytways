package com.project.irs_backend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrderItemRequest {

	private Long materialId;
	private Long quantity;
	private BigDecimal price;
	private BigDecimal totalPrice;

}
