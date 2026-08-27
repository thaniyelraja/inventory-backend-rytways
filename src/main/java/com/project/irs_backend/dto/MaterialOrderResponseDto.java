package com.project.irs_backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.project.irs_backend.entity.Supplier;
import com.project.irs_backend.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialOrderResponseDto {

	private Long id;
	private LocalDateTime orderDateTime;
	private OrderStatus orderStatus;
	private BigDecimal totalAmount;
	private Long createdby;
	private Supplier supplier;
	private List<MaterialOrderItemResponse> items;

}
