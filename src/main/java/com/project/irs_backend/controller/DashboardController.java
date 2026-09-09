//package com.project.irs_backend.controller;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.project.irs_backend.dto.AdminDashboardSummary;
//import com.project.irs_backend.dto.DashboardSummary;
//import com.project.irs_backend.dto.RequestStatusCount;
//import com.project.irs_backend.dto.SpendingOverviewDto;
//import com.project.irs_backend.dto.TopMonthlyDto;
//import com.project.irs_backend.entity.InventoryRequest;
//import com.project.irs_backend.dto.ProductTrend;
//import com.project.irs_backend.service.DashboardService;
//
//import lombok.RequiredArgsConstructor;
//
//@RestController
//@RequestMapping("/dashboard")
//@RequiredArgsConstructor
//public class DashboardController {
//
//	private final DashboardService dashboardService;
//
//	@GetMapping("/summary")
//	public DashboardSummary getDashboardSummary(@RequestParam Long userId) {
//		return dashboardService.getDashboardSummary(userId);
//	}
//
//	@GetMapping("/recent/{userId}")
//	public ResponseEntity<List<InventoryRequest>> getRecentRequests(@PathVariable Long userId,
//			@RequestParam(required = false) LocalDateTime startDateTime) {
//		return ResponseEntity.ok(dashboardService.getRecentRequests(userId, startDateTime));
//	}
//
//	@GetMapping("/spending/{userId}")
//	public ResponseEntity<List<SpendingOverviewDto>> getSpendingOverview(@PathVariable Long userId,
//			@RequestParam LocalDate fromMonth, @RequestParam LocalDate toMonth) {
//		return ResponseEntity
//				.ok(dashboardService.getSpendingOverview(userId, fromMonth.atStartOfDay(), toMonth.atStartOfDay()));
//	}
//
//	@GetMapping("/trend")
//	public List<ProductTrend> getRequestTrend(@RequestParam String period,
//			@RequestParam(required = false) LocalDate startDate, @RequestParam(required = false) Integer month,
//			@RequestParam(required = false) Integer year) {
//
//		return dashboardService.getProductTrend(period, startDate, month, year);
//	}
//
//	@GetMapping("/status")
//	public List<RequestStatusCount> getRequestStatus(@RequestParam Long userId) {
//		return dashboardService.getRequestStatus(userId);
//	}
//
//	@GetMapping("/admin/top-products")
//	public ResponseEntity<List<TopMonthlyDto>> getTopProducts(@RequestParam Integer month) {
//		return ResponseEntity.ok(dashboardService.getTopProductByMonth(month));
//	}
//
//	@GetMapping("/admin/summary")
//	public AdminDashboardSummary getAdminDashboardSummary() {
//		return dashboardService.getAdminDashboardSummary();
//	}
//
//}
