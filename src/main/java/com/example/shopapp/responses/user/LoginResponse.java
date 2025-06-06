package com.example.shopapp.responses.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    @JsonProperty("message")
    private String message;

    @JsonProperty("token")
    private String token;
}
