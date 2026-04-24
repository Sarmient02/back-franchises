package co.com.bancolombia.api.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProductRequestDTO(
        @NotBlank
        @Size(max = 150)
        String name,
        @NotNull
        @Min(0)
        Integer stockQuantity
) {
}

