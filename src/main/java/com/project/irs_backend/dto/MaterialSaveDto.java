package com.project.irs_backend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialSaveDto {

	private String materialCode;

	private String materialName;

	private String materialDesc;

	private BigDecimal materialPrice;

	private Integer maxStockLevel;

	private Integer reorderLevel;

	private String unit;

	private Integer availableQuantity;

	private Long categoryId;

}
