package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ProductUseCase {

    private static final int MAX_PRODUCT_NAME_LENGTH = 150;

    private final ProductRepository productRepository;
    private final BranchRepository branchRepository;

    public Mono<Product> create(Product product) {
        if (product.getIdBranch() == null || product.getIdBranch() <= 0) {
            return Mono.error(new BusinessException(BusinessErrorType.INVALID_INPUT, "Branch id is required"));
        }

        return Mono.fromCallable(() -> {
                    String productName = sanitizeName(product.getName());
                    Integer stockQuantity = sanitizeStockQuantity(product.getStockQuantity());
                    return product.toBuilder()
                            .name(productName)
                            .stockQuantity(stockQuantity)
                            .build();
                })
                .flatMap(sanitizedProduct -> branchRepository.existsById(sanitizedProduct.getIdBranch())
                        .flatMap(branchExists -> {
                            if (!branchExists) {
                                return Mono.error(new BusinessException(BusinessErrorType.BRANCH_NOT_FOUND));
                            }
                            return productRepository.existsByIdBranchAndName(sanitizedProduct.getIdBranch(), sanitizedProduct.getName());
                        })
                        .flatMap(productExists -> {
                            if (productExists) {
                                return Mono.error(new BusinessException(BusinessErrorType.PRODUCT_ALREADY_EXISTS));
                            }
                            return productRepository.save(sanitizedProduct);
                        }));
    }

    public Flux<Product> getAll(Long idBranch) {
        if (idBranch == null) {
            return productRepository.findAll();
        }
        if (idBranch <= 0) {
            return Flux.error(new BusinessException(BusinessErrorType.INVALID_INPUT, "Branch id must be greater than 0"));
        }
        return productRepository.findAllByIdBranch(idBranch);
    }

    public Mono<Void> deleteById(Long idProduct) {
        if (idProduct == null || idProduct <= 0) {
            return Mono.error(new BusinessException(BusinessErrorType.INVALID_INPUT, "Product id is required"));
        }

        return productRepository.existsProductById(idProduct)
                .flatMap(productExists -> {
                    if (!productExists) {
                        return Mono.error(new BusinessException(BusinessErrorType.PRODUCT_NOT_FOUND));
                    }
                    return productRepository.deleteProductById(idProduct);
                });
    }

    public Mono<Product> patch(Long idProduct, String name, Integer stockQuantity) {
        if (idProduct == null || idProduct <= 0) {
            return Mono.error(new BusinessException(BusinessErrorType.INVALID_INPUT, "Product id is required"));
        }

        if (name == null && stockQuantity == null) {
            return Mono.error(new BusinessException(BusinessErrorType.INVALID_INPUT, "At least one field to update is required"));
        }

        return productRepository.findProductById(idProduct)
                .switchIfEmpty(Mono.error(new BusinessException(BusinessErrorType.PRODUCT_NOT_FOUND)))
                .flatMap(product -> {
                    Product.ProductBuilder builder = product.toBuilder();

                    if (name != null) {
                        builder.name(sanitizeName(name));
                    }
                    if (stockQuantity != null) {
                        builder.stockQuantity(sanitizeStockQuantity(stockQuantity));
                    }

                    return productRepository.save(builder.build());
                });
    }

    private String sanitizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(BusinessErrorType.INVALID_INPUT, "Product name is required");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > MAX_PRODUCT_NAME_LENGTH) {
            throw new BusinessException(BusinessErrorType.INVALID_INPUT, "Product name must be at most 150 characters");
        }

        return trimmedName;
    }

    private Integer sanitizeStockQuantity(Integer stockQuantity) {
        if (stockQuantity == null) {
            throw new BusinessException(BusinessErrorType.INVALID_INPUT, "stockQuantity is required");
        }

        if (stockQuantity < 0) {
            throw new BusinessException(BusinessErrorType.INVALID_INPUT, "stockQuantity must be greater than or equal to 0");
        }

        return stockQuantity;
    }

}
