package com.project.irs_backend.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryRequestHistoryItemDto {

	private Long historyId;
	private Long userId;
	private String userName;
	private String action;
	private LocalDateTime createdAt;

}
