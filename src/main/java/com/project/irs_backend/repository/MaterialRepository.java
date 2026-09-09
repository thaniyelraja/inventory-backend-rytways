package com.project.irs_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.project.irs_backend.dto.MaterialDto;
import com.project.irs_backend.entity.Category;
import com.project.irs_backend.entity.Inventory;
import com.project.irs_backend.entity.Material;

public interface MaterialRepository extends JpaRepository<Material, Long> {

	@Query(value = """
			select i.* from inventory i join material m on i.material_id = m.material_id where m.category_id = :categoryId
									""", nativeQuery = true)
	List<Inventory> findMaterialsByCategories(@Param("categoryId") Long categoryId);

	Optional<Material> findByMaterialCode(String materialCode);

	@Query(value = """
			SELECT distinct c.* from category c join material m on c.category_id = m.category_id join inventory i on m.material_id = i.material_id
			order by c.category_name;
						""", nativeQuery = true)
	List<Category> findCategoriesByStock();

	@Query(value = """
			select m.* from material m where m.category_id = :categoryId and
			not exists (select 1 from inventory i where i.material_id = m.material_id)
			order by m.material_name
			""", nativeQuery = true)
	List<Material> findMaterialsForAddStock(@Param("categoryId") Long categoryId);

//
//	@Query(value = """
//						select m.id, m.material_code, m.material_name, sm.price from material m join supplier_material sm on
//			sm.material_id = m.id where sm.supplier_id = :supplierId and m.category_id = :categoryId;
//						""", nativeQuery = true)
//	List<MaterialDto> findMaterialsByCategory(@Param("supplierId") Long supplierId,
//			@Param("categoryId") Long categoryId);
//
//	@Query(value = """
//			SELECT COUNT(*) from material
//			""", nativeQuery = true)
//	Long countTotalProducts();
//
//	@Query(value = """
//			SELECT COUNT(*) FROM material WHERE available_quantity > 0 AND available_quantity <= reorder_level
//			""", nativeQuery = true)
//	Long countLowStockProducts();
//
//	@Query(value = """
//			SELECT COUNT(*) FROM material WHERE available_quantity = 0
//			""", nativeQuery = true)
//	Long countOutOfStockProducts();
//
//	@Query(value = """
//			SELECT COUNT(*) FROM material WHERE available_quantity > max_stock_level
//			""", nativeQuery = true)
//	Long countOverStockedProducts();

	@Query(value = """
				SELECT * FROM material WHERE (
				:search = ''
					OR LOWER(material_code) LIKE LOWER(CONCAT('%', :search, '%'))
					OR LOWER(material_name) LIKE LOWER(CONCAT('%', :search, '%'))
					)
					AND
				 	(
					:categoryId IS NULL
					OR category_id = :categoryId
			)

			""", countQuery = """
			SELECT COUNT(*) FROM material WHERE
				(
				:search = ''
				OR LOWER(material_code) LIKE LOWER(CONCAT('%', :search, '%'))
				OR LOWER(material_name) LIKE LOWER(CONCAT('%', :search, '%'))			)
			AND
				 (
				:categoryId IS NULL
				OR category_id = :categoryId
				)

			""", nativeQuery = true)
	Page<Material> searchAndFilter(@Param("search") String search, @Param("categoryId") Long categoryId,
			Pageable pageable);
}
