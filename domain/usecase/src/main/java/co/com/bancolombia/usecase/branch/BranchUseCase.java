package co.com.bancolombia.usecase.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
public class BranchUseCase {

    private static final int MAX_BRANCH_NAME_LENGTH = 150;

    private final BranchRepository branchRepository;
    private final FranchiseRepository franchiseRepository;

    public Mono<Branch> create(Branch branch) {
        if (branch.getIdFranchise() == null || branch.getIdFranchise() <= 0) {
            return Mono.error(new IllegalArgumentException("Franchise id is required"));
        }

        String branchName = sanitizeName(branch.getName());

        return franchiseRepository.existsById(branch.getIdFranchise())
                .flatMap(franchiseExists -> {
                    if (!franchiseExists) {
                        return Mono.error(new NoSuchElementException("Franchise not found"));
                    }
                    return branchRepository.existsByIdFranchiseAndName(branch.getIdFranchise(), branchName);
                })
                .flatMap(branchExists -> {
                    if (branchExists) {
                        return Mono.error(new IllegalStateException("Branch name already exists for this franchise"));
                    }
                    return branchRepository.save(branch.toBuilder().name(branchName).build());
                });
    }

    private String sanitizeName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Branch name is required");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > MAX_BRANCH_NAME_LENGTH) {
            throw new IllegalArgumentException("Branch name must be at most 150 characters");
        }

        return trimmedName;
    }

}


