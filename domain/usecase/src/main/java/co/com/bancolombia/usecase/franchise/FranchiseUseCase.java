package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class FranchiseUseCase {

    private final FranchiseRepository franchiseRepository;

    public Mono<Franchise> create(Franchise franchise) {
        if (franchise.getName() == null || franchise.getName().isBlank())
            return Mono.error(new IllegalArgumentException("Franchise name is required"));

        return franchiseRepository.save(franchise);
    }

}
