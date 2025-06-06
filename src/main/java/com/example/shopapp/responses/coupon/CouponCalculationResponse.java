package com.example.shopapp.responses.coupon;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CouponCalculationResponse {
    @JsonProperty("result")
    private Double result;

    @JsonProperty("errorMessage")
    private String errorMessage;
}
