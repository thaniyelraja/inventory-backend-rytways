package com.project.irs_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummary {

	private Long totalMaterials;
	private Long pendingRequests;
	private Long approvedRequests;
	private Long rejectedRequests;

}
