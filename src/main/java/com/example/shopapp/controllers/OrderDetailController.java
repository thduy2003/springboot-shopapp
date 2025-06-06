package com.example.shopapp.controllers;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.exception.DataNotFoundException;
import com.example.shopapp.models.OrderDetail;
import com.example.shopapp.responses.order_detail.OrderDetailResponse;
import com.example.shopapp.services.IOrderDetailService;
import com.example.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {
    final private IOrderDetailService orderDetailService;
    private final LocalizationUtils localizationUtils;
    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(@Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
            return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(newOrderDetail));
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderDetail(@PathVariable("id") Long id) throws DataNotFoundException {
        OrderDetail orderDetail = orderDetailService.getOrderDetail(id);
        return ResponseEntity.ok().body(OrderDetailResponse.fromOrderDetail(orderDetail));
    }

//    Lấy ra danh sách các order_details của 1 order nào đó
    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable("orderId") Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.findByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponseList = orderDetails.stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .toList();
        return ResponseEntity.ok(orderDetailResponseList);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(@PathVariable("id") Long id, @Valid @RequestBody OrderDetailDTO orderDetailDTO) {
        try {
            OrderDetail orderDetail = orderDetailService.updateOrderDetail(id,orderDetailDTO);
            return ResponseEntity.ok(orderDetail);
        }catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable("id") Long id) {
        orderDetailService.deleteById(id);
        return ResponseEntity.ok().body(localizationUtils.getLocalizeMessage(MessageKeys.DELETE_ORDER_DETAIL_SUCCESSFULLY + id));
    }
}
