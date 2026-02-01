package org.ecommerce.project.repositories;

import org.ecommerce.project.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Page<Product> findProductByCategory_CategoryIDOrderByPriceAsc(Long categoryCategoryID, Pageable pageable);

    Page<Product> findProductByProductNameContainingIgnoreCase(String productName, Pageable pageable);

    Product findProductByProductNameIgnoreCase(String productName);
}
