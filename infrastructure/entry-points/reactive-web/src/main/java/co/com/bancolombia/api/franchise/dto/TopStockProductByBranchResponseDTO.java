package co.com.bancolombia.api.franchise.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TopStockProductByBranchResponseDTO(
        @NotNull
        @Min(1)
        Long id,
        @NotNull
        @Min(1)
        Long idBranch,
        @NotBlank
        String branchName,
        @NotBlank
        String name,
        @NotNull
        @Min(0)
        Integer stockQuantity
) {
}
