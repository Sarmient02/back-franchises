package co.com.bancolombia.api.product.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateProductStockRequestDTO(
        @NotNull
        @Min(0)
        Integer stockQuantity
) {
}

