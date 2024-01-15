package com.example.shopapp.repositories;

import com.example.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    boolean existsByName(String name);
    Page<Product> findAll(Pageable pageable);
    @Query("SELECT p from Product p where"
    + "(:categoryId is null or :categoryId = 0 or p.category.id = :categoryId)"
    + "And (:keyword is null or :keyword = '' or p.name LIKE %:keyword% or p.description LIKE %:keyword% )"
    )
    Page<Product> searchProducts(@Param("categoryId") Long categoryId, @Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT p from Product p LEFT JOIN FETCH p.productImages where p.id = :productId")
    Optional<Product> getDetailProduct(@Param("productId") Long productId);

    @Query("Select p from Product p WHERE p.id IN :productIds")
    List<Product> findProductsByIds(@Param("productIds") List<Long> productIds);
}
