package com.example.shopapp.responses.user;

import com.example.shopapp.models.Role;
import com.example.shopapp.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;

    @JsonProperty("fullname")
    private String fullName;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("address")
    private String address;

    @JsonProperty("is_active")
    private Boolean active;

    @JsonProperty("date_of_birth")
    private Date dateOfBirth;

    @JsonProperty("facebook_account_id")
    private int facebookAccountId;

    @JsonProperty("google_account_id")
    private int googleAccountId;

    @JsonProperty("role")
    private Role role;

    public static UserResponse fromUser(User user) {
        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .address(user.getAddress())
                .active(user.isActive())
                .dateOfBirth(user.getDateOfBirth())
                .facebookAccountId(user.getFacebookAccountId())
                .googleAccountId(user.getGoogleAccountId())
                .role(user.getRole())
                .build();
        return userResponse;
    }
}
