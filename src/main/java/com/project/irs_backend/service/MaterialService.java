package com.project.irs_backend.service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.project.irs_backend.dto.MaterialSaveDto;
import com.project.irs_backend.dto.MaterialDto;
import com.project.irs_backend.entity.Category;
import com.project.irs_backend.entity.Inventory;
import com.project.irs_backend.entity.Material;
import com.project.irs_backend.entity.Unit;
import com.project.irs_backend.repository.CategoryRepository;
import com.project.irs_backend.repository.InventoryRepository;
import com.project.irs_backend.repository.MaterialRepository;
import com.project.irs_backend.repository.UnitRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MaterialService {

	private final MaterialRepository materialRepository;

	private final CategoryRepository categoryRepository;

	private final UnitRepository unitRepository;

	private final InventoryRepository inventoryRepository;

	public List<Unit> getUnits() {
		return unitRepository.findAll();
	}

	public List<Category> getCategories() {
		return categoryRepository.findAll();
	}

	public List<Material> getMaterialsForAddStock(Long categoryId, Long materialId) {
		return materialRepository.findMaterialsForAddStock(categoryId, materialId);
	}

	public Material createMaterial(MaterialSaveDto dto) {
		if (materialRepository.findByMaterialCode(dto.getMaterialCode()).isPresent()) {
			throw new RuntimeException("Material code already exists: " + dto.getMaterialCode());
		}
		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found"));
		Unit unit = unitRepository.findById(dto.getUnitId()).orElseThrow(() -> new RuntimeException("Unit not found"));
		Material material = new Material();
		BeanUtils.copyProperties(dto, material);
		material.setCategory(category);
		material.setUnit(unit);
		return materialRepository.save(material);
	}

	public Page<Material> getAllMaterials(String search, Long categoryId, Pageable pageable) {

		return materialRepository.searchAndFilter(search, categoryId, pageable);

	}

	public Material updateMaterial(Long materialId, MaterialSaveDto dto) {
		Material existingMaterial = materialRepository.findById(materialId)
				.orElseThrow(() -> new RuntimeException("Material not found"));
		Long unitId = dto.getUnitId();
		BeanUtils.copyProperties(dto, existingMaterial);
		Category category = categoryRepository.findById(dto.getCategoryId())
				.orElseThrow(() -> new RuntimeException("Category not found"));
		Unit unit = unitRepository.findById(unitId).orElseThrow(() -> new RuntimeException("Unit not found"));
		existingMaterial.setUnit(unit);
		existingMaterial.setCategory(category);
		return materialRepository.save(existingMaterial);
	}

	public void deleteMaterial(Long materialId) {
		Material material = materialRepository.findById(materialId)
				.orElseThrow(() -> new RuntimeException("Material not found"));
		if (inventoryRepository.existsByMaterialMaterialId(materialId)) {
			throw new RuntimeException("Cannot delete material because it is already added to inventory");
		}
		materialRepository.delete(material);
	}

	public List<Inventory> getMaterialsByCategories(Long categoryId) {
		return materialRepository.findMaterialsByCategories(categoryId);
	}

	public List<Category> getCategoriesByStock() {
		return materialRepository.findCategoriesByStock();
	}

//	
//	public List<MaterialDto> getMaterialsByCategory(Long supplierId, Long categoryId) {
//		return materialRepository.findMaterialsByCategory(supplierId, categoryId);
//	}

//	public void exportMaterialToExcel(OutputStream outputStream) throws IOException {
//		List<Material> materials = materialRepository.findAll();
//		SXSSFWorkbook workbook = new SXSSFWorkbook(100);
//		SXSSFSheet sheet = workbook.createSheet("Materials");
//		sheet.trackAllColumnsForAutoSizing();
//
//		Row header = sheet.createRow(0);
//
//		header.createCell(0).setCellValue("S.No");
//		header.createCell(1).setCellValue("Material Code");
//		header.createCell(2).setCellValue("Material Name");
//		header.createCell(3).setCellValue("Available Quantity");
//		header.createCell(4).setCellValue("Unit");
//		header.createCell(5).setCellValue("Status");
//
//		for (int i = 0; i < materials.size(); i++) {
//			Material material = materials.get(i);
//			Row row = sheet.createRow(i + 1);
//
//			row.createCell(0).setCellValue(i + 1);
//			row.createCell(1).setCellValue(material.getMaterialCode());
//			row.createCell(2).setCellValue(material.getMaterialName());
//			row.createCell(3).setCellValue(material.getAvailableQuantity());
//			row.createCell(4).setCellValue(material.getUnit());
//
//			String status = material.getAvailableQuantity() > 0 ? "Available" : "Out of Stock";
//
//			row.createCell(5).setCellValue(status);
//
//		}
//
//		for (int i = 0; i < 6; i++) {
//			sheet.autoSizeColumn(i);
//		}
//
//		workbook.write(outputStream);
//		workbook.close();
//
//	}
//
}
