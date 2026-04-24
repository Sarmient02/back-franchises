package co.com.bancolombia.api.franchise.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateFranchiseRequestDTO(
        @NotBlank
        String name
) {}