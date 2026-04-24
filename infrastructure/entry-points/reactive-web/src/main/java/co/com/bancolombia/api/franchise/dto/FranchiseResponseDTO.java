package co.com.bancolombia.api.franchise.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FranchiseResponseDTO(
        @NotNull
        @Min(1)
        Long id,
        @NotBlank
        String name
) {
}
