package co.com.bancolombia.api.branch.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateBranchRequestDTO(
        @NotBlank
        @Size(max = 150)
        String name
) {
}

