package com.project.irs_backend.dto;

import java.math.BigDecimal;

import com.project.irs_backend.entity.Unit;

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

	private Long unitId;

	private Long categoryId;

}
