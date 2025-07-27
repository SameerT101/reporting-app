package com.sam.reporting.repository;

import com.sam.reporting.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select distinct c from Category c left join fetch c.products")
    List<Category> findAllWithProducts();
}