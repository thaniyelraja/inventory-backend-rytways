package com.project.irs_backend.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaterialDto {

	private Long materialId;
	private String materialCode;
	private String materialName;
	private BigDecimal price;

}
