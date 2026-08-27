package com.project.irs_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.irs_backend.entity.MaterialOrder;

public interface MaterialOrderRepository extends JpaRepository<MaterialOrder, Long> {

	@Query(value = """
			select mo.material_order_id, mo.order_date_time, mo.order_status, mo.total_amount, mo.created_by,
			s.supplier_id, s.supplier_code, s.supplier_name, moi.material_order_item_id, moi.material_id,
			m.material_code, m.material_name, moi.quantity, moi.price, moi.total_price
			from material_order mo join supplier s on s.supplier_id = mo.supplier_id
			join material_order_item moi on moi.order_id = mo.material_order_id
			join material m on m.material_id = moi.material_id
			order by mo.order_date_time desc;
						""", nativeQuery = true)
	List<Object[]> getAllOrderDetails();
}
