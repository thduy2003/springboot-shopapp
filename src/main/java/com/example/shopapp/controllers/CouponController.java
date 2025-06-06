package com.example.shopapp.controllers;

import com.example.shopapp.responses.CouponCalculationResponse;
import com.example.shopapp.services.ICouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/coupons")
public class CouponController {
    private final ICouponService couponService;

    @GetMapping("/calculate")
    public ResponseEntity<CouponCalculationResponse> calculateCouponValue(
            @RequestParam("couponCode") String couponCode,
            @RequestParam("totalAmount") double totalAmount
    ) {
        try {
            double finalAmount = couponService.calculateCouponValue(couponCode, totalAmount);
            CouponCalculationResponse response = CouponCalculationResponse.builder()
                    .result(finalAmount)
                    .errorMessage("")
                    .build();
            return ResponseEntity.ok(response);
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    CouponCalculationResponse.builder()
                            .result(totalAmount)
                            .errorMessage(e.getMessage())
                            .build()
            );
        }
    }
}
