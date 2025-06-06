package com.example.shopapp.responses.product;

import lombok.*;

import java.util.List;
@Setter
@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponse {
    private List<ProductResponse> products;
    private int totalPages;
}
