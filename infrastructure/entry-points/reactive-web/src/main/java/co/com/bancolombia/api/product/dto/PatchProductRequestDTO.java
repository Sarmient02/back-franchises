package co.com.bancolombia.api.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public record PatchProductRequestDTO(
        @Size(min = 1, max = 150)
        String name,
        @Min(0)
        Integer stockQuantity
) {}
