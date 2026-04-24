package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;
    private final ProductRepository productRepository;

    public Mono<Franchise> create(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().isBlank())
            return Mono.error(new IllegalArgumentException("Franchise name is required"));

        return franchiseRepository.save(franchise);
    }

    public Flux<TopStockProductByBranch> getTopStockProductsByBranch(Long idFranchise) {
        if (idFranchise == null || idFranchise <= 0) {
            return Flux.error(new IllegalArgumentException("Franchise id is required"));
        }

        return franchiseRepository.existsById(idFranchise)
                .flatMapMany(franchiseExists -> {
                    if (!franchiseExists) {
                        return Flux.error(new NoSuchElementException("Franchise not found"));
                    }

                    return productRepository.findTopStockProductsByFranchiseId(idFranchise);
                });
    }

}
