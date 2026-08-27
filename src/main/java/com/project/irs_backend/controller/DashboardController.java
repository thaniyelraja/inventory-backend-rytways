package com.project.irs_backend.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.irs_backend.dto.AdminDashboardSummary;
import com.project.irs_backend.dto.DashboardSummary;
import com.project.irs_backend.dto.RequestStatusCount;
import com.project.irs_backend.dto.ProductTrend;
import com.project.irs_backend.service.DashboardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

	private final DashboardService dashboardService;

	@GetMapping("/summary")
	public DashboardSummary getDashboardSummary(@RequestParam Long userId) {
		return dashboardService.getDashboardSummary(userId);
	}

	@GetMapping("/trend")
	public List<ProductTrend> getRequestTrend(@RequestParam String period,
			@RequestParam(required = false) LocalDate startDate,
			@RequestParam(required = false) Integer month,
			@RequestParam(required = false) Integer year
			) {
		
		return dashboardService.getProductTrend(period, startDate, month, year);
	}

	@GetMapping("/status")
	public List<RequestStatusCount> getRequestStatus(@RequestParam Long userId) {
		return dashboardService.getRequestStatus(userId);
	}

	@GetMapping("/admin/summary")
	public AdminDashboardSummary getAdminDashboardSummary() {
		return dashboardService.getAdminDashboardSummary();
	}
	
	

}
