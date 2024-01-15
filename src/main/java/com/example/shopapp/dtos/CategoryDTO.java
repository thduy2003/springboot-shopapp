package com.example.shopapp.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data //ToString
@Setter
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
    @NotEmpty(message = "Category's name cannot be empty")
    public String name;
}
