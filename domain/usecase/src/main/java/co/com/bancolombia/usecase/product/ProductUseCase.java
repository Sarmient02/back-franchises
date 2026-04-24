package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class ProductUseCase {

    private static final int MAX_PRODUCT_NAME_LENGTH = 150;

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Mono<Product> create(Product product) {
        if (product.getIdBranch() == null || product.getIdBranch() <= 0) {
            return Mono.error(new IllegalArgumentException("Branch id is required"));
        }

        String productName = sanitizeName(product.getName());
        Integer stockQuantity = sanitizeStockQuantity(product.getStockQuantity());

        return branchRepository.existsById(product.getIdBranch())
                .flatMap(branchExists -> {
                    if (!branchExists) {
                        return Mono.error(new NoSuchElementException("Branch not found"));
                    }
                    return productRepository.existsByIdBranchAndName(product.getIdBranch(), productName);
                })
                .flatMap(productExists -> {
                    if (productExists) {
                        return Mono.error(new IllegalStateException("Product name already exists for this branch"));
                    }
                    return productRepository.save(product.toBuilder()
                            .name(productName)
                            .stockQuantity(stockQuantity)
                            .build());
                });
    }

    private String sanitizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > MAX_PRODUCT_NAME_LENGTH) {
            throw new IllegalArgumentException("Product name must be at most 150 characters");
        }

        return trimmedName;
    }

    private Integer sanitizeStockQuantity(Integer stockQuantity) {
        if (stockQuantity == null) {
            throw new IllegalArgumentException("stockQuantity is required");
        }

        if (stockQuantity < 0) {
            throw new IllegalArgumentException("stockQuantity must be greater than or equal to 0");
        }

        return stockQuantity;
    }

}

