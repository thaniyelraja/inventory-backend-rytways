package com.project.irs_backend.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.project.irs_backend.dto.CategoryDto;
import com.project.irs_backend.dto.MaterialSaveDto;
import com.project.irs_backend.dto.MaterialDto;
import com.project.irs_backend.entity.Category;
import com.project.irs_backend.entity.Material;
import com.project.irs_backend.entity.MaterialOrder;
import com.project.irs_backend.repository.MaterialRepository;
import com.project.irs_backend.service.MaterialService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/material")
@RequiredArgsConstructor
public class MaterialController {

	private final MaterialService materialService;

	private final MaterialRepository materialRepository;

	@GetMapping("/materials")
	public ResponseEntity<Page<Material>> getAllMaterials(
			@RequestParam(defaultValue = "") String search,
			@RequestParam(defaultValue = "ALL") String status, 
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String availability,
			Pageable pageable) {
		return ResponseEntity.ok(materialService.getAllMaterials(search, status, categoryId, pageable));
	}

	@GetMapping("/export-excel")
	public ResponseEntity<StreamingResponseBody> exportExcel() {

		StreamingResponseBody stream = outputStream -> {
			materialService.exportMaterialToExcel(outputStream);
		};
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=materials.xlsx")
				.contentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
				.body(stream);
	}

	@PostMapping("/create")
	public ResponseEntity<Material> createMaterial(@RequestBody MaterialSaveDto dto) {
		return ResponseEntity.ok(materialService.createMaterial(dto));
	}

	@PutMapping("/update/{materialId}")
	public ResponseEntity<Material> updateMaterial(@PathVariable Long materialId, @RequestBody MaterialSaveDto dto) {
		return ResponseEntity.ok(materialService.updateMaterial(materialId, dto));
	}

	@DeleteMapping("/delete/{materialId}")
	public ResponseEntity<Void> deleteMaterial(@PathVariable Long materialId){
		materialService.deleteMaterial(materialId);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/categories")
	public ResponseEntity<List<CategoryDto>> getAllCategories() {
		return ResponseEntity.ok(materialService.getAllCategories());
	}

	@GetMapping("/categories/{supplierId}")
	public ResponseEntity<List<CategoryDto>> getCategories(@PathVariable Long supplierId) {
		return ResponseEntity.ok(materialService.getCategoriesBySupplierId(supplierId));
	}

	@GetMapping("/{supplierId}/category/{categoryId}")
	public ResponseEntity<List<MaterialDto>> getMaterialsByCategory(@PathVariable Long supplierId,
			@PathVariable Long categoryId) {
		return ResponseEntity.ok(materialService.getMaterialsByCategory(supplierId, categoryId));
	}
	
	@GetMapping("/category/{categoryId}")
	public ResponseEntity<List<MaterialDto>> getMaterialsByCategories(
			@PathVariable Long categoryId) {
		return ResponseEntity.ok(materialService.getMaterialsByCategories(categoryId));
	}

}
