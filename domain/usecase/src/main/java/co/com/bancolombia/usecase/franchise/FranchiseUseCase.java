package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private static final int MAX_FRANCHISE_NAME_LENGTH = 150;

    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;

    public Mono<Franchise> create(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().isBlank())
            return Mono.error(new BusinessException(BusinessErrorType.INVALID_INPUT, "Franchise name is required"));

        return franchiseRepository.save(franchise);
    }

    public Mono<Franchise> updateName(Long idFranchise, String name) {
        if (idFranchise == null || idFranchise <= 0) {
            return Mono.error(new BusinessException(BusinessErrorType.INVALID_INPUT, "Franchise id is required"));
        }

        return Mono.fromCallable(() -> sanitizeName(name))
                .flatMap(sanitizedName -> franchiseRepository.findById(idFranchise)
                        .switchIfEmpty(Mono.error(new BusinessException(BusinessErrorType.FRANCHISE_NOT_FOUND)))
                        .flatMap(franchise -> franchiseRepository.save(franchise.toBuilder()
                                .name(sanitizedName)
                                .build())));
    }

    public Flux<TopStockProductByBranch> getTopStockProductsByBranch(Long idFranchise) {
        if (idFranchise == null || idFranchise <= 0) {
            return Flux.error(new BusinessException(BusinessErrorType.INVALID_INPUT, "Franchise id is required"));
        }

        return franchiseRepository.existsById(idFranchise)
                .flatMapMany(franchiseExists -> {
                    if (!franchiseExists) {
                        return Flux.error(new BusinessException(BusinessErrorType.FRANCHISE_NOT_FOUND));
                    }

                    return productRepository.findTopStockProductsByFranchiseId(idFranchise);
                });
    }

    private String sanitizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new BusinessException(BusinessErrorType.INVALID_INPUT, "Franchise name is required");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > MAX_FRANCHISE_NAME_LENGTH) {
            throw new BusinessException(BusinessErrorType.INVALID_INPUT, "Franchise name must be at most 150 characters");
        }

        return trimmedName;
    }

}
