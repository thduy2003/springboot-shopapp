package com.example.shopapp.responses;

import com.example.shopapp.models.Order;
import com.example.shopapp.models.OrderDetail;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Setter
@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("email")
    private String email;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("note")
    private String note;

    @JsonProperty("order_date")
    private Date orderDate;

    @JsonProperty("payment_method")
    private String paymentMethod;

    @JsonProperty("shipping_date")
    private LocalDate shippingDate;

    @JsonProperty("shipping_method")
    private String shippingMethod;

    @JsonProperty("status")
    private String status;

    @JsonProperty("total_money")
    private Float totalMoney;

    @JsonProperty("order_details")
    private List<OrderDetail> orderDetails;

    public static OrderResponse fromOrder(Order order) {
        OrderResponse orderResponse = OrderResponse
                .builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .fullName(order.getFullname())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .address(order.getAddress())
                .note(order.getNote())
                .orderDate(order.getOrderDate())
                .paymentMethod(order.getPaymentMethod())
                .shippingMethod(order.getShippingMethod())
                .shippingDate(order.getShippingDate())
                .status(order.getStatus())
                .totalMoney(order.getTotalMoney())
                .orderDetails(order.getOrderDetails())
                .build();
        return orderResponse;
    }
}
