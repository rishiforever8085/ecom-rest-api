package com.ecommerce.repository;

import com.ecommerce.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository("productImageRepository")
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}

