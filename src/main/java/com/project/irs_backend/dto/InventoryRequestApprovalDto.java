package com.project.irs_backend.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestApprovalDto {

	private List<InventoryRequestApprovalItemDto> items;
	private String approvalRemarks;
	private String rejectionRemarks;
	private String clarificationMessage;

}
