package com.example.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import lombok.*;

@Data //ToString
@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailDTO {
    @JsonProperty("order_id")
    @Min(value = 1, message = "Order's ID must be > 0")
    private Long orderId;

    @Min(value = 1, message = "Product's ID must be > 0")
    @JsonProperty("product_id")
    private Long productId;

    @Min(value =0 , message="Price must be >= 0")
    private Float price;

    @JsonProperty("number_of_products")
    @Min(value = 1, message = "number_of_products must be >= 1")
    private int numberOfProducts;

    @Min(value =0, message = "total_money must be >= 0")
    @JsonProperty("total_money")
    private Float totalMoney;

    private String color;
}
