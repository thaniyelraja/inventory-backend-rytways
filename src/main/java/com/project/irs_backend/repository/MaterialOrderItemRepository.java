package com.project.irs_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.irs_backend.entity.MaterialOrderItem;

public interface MaterialOrderItemRepository extends JpaRepository<MaterialOrderItem, Long> {

}
