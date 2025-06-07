package com.example.shopapp.responses;

import lombok.*;
import org.springframework.http.HttpStatus;

@Setter
@Data
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseObject {
    private String message;
    private HttpStatus status;
    private Object data;
}
