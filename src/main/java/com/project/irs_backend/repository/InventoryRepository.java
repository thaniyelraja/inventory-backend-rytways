package com.project.irs_backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.irs_backend.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	boolean existsByMaterialMaterialId(Long materialId);

	@Query(value = """
			select i.* from inventory i 
			join material m on i.material_id = m.material_id
			join category c on m.category_id = c.category_id join unit u on m.unit_id = u.unit_id
			where
				(
					:search = ''
					OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
					OR LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
			    )
			    AND (
				    :categoryId IS NULL
				    OR c.category_id = :categoryId
				    )
				""", countQuery = """
			select count(*) from inventory i join material m on i.material_id = m.material_id
			join category c on m.category_id = c.category_id join unit u on m.unit_id = u.unit_id
			where
				(
					:search = ''
					OR LOWER(m.material_name) LIKE LOWER(CONCAT('%', :search, '%'))
					OR LOWER(m.material_code) LIKE LOWER(CONCAT('%', :search, '%'))
			    )
			    AND (
				    :categoryId IS NULL
				    OR c.category_id = :categoryId
				    )
						""", nativeQuery = true)
	Page<Inventory> getStocks(@Param("search") String search, @Param("categoryId") Long categoryId, Pageable pageable);

}
