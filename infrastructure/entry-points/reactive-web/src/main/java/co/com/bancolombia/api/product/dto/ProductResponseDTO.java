package co.com.bancolombia.api.product.dto;

public record ProductResponseDTO(
        Long id,
        Long idBranch,
        String name,
        Integer stockQuantity
) {
}
