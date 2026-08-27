package com.project.irs_backend.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrderRequest {

	private Long supplierId;
	private Long createdBy;
	private BigDecimal totalAmount;
	private List<MaterialOrderItemRequest> items;

}
