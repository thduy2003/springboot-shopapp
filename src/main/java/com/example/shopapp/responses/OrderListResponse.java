package com.example.shopapp.responses;

import lombok.*;

import java.util.List;
@Setter
@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderListResponse {
    private List<OrderResponse> orders;
    private int totalPages;
}
