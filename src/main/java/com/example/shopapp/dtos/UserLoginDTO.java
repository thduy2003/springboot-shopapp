package com.example.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data //ToString
@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserLoginDTO {
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message ="Password cannot be blank")
    private String password;
    @JsonProperty("role_id")
    private Long roleId;
}

