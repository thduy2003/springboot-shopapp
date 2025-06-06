package com.example.shopapp.responses.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPageResponse {
    List<UserResponse> users;
    Integer pageNumber;
    Integer pageSize;
    long totalElements;
    int totalPages;
    boolean isLast;
}
