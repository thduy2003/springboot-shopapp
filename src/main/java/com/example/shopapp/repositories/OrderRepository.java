package com.example.shopapp.repositories;

import com.example.shopapp.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
//    Tìm các đơn hàng của 1 user nào đó
    List<Order> findByUserId(Long userId);

    @Query("Select o from Order o where " + "o.active = true and (:keyword is null or :keyword = '' OR o.fullname LIKE %:keyword% OR o.address LIKE %:keyword% " +
            "OR o.note LIKE %:keyword% " + "Or o.email LIKE %:keyword%)")
    Page<Order> findByKeyword(String keyword, Pageable pageable);
}
