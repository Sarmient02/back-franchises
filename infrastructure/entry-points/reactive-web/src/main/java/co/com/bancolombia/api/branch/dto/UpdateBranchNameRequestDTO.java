package co.com.bancolombia.api.branch.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBranchNameRequestDTO(
        @NotBlank
        String name
) {}
