package co.com.bancolombia.api.franchise.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateFranchiseNameRequestDTO(
        @NotBlank
        String name
) {}
