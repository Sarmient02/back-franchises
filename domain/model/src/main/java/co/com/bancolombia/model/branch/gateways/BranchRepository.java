package co.com.bancolombia.model.branch.gateways;

import co.com.bancolombia.model.branch.Branch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BranchRepository {

    Mono<Branch> save(Branch branch);

    Mono<Branch> findById(Long idBranch);

    Mono<Boolean> existsByIdFranchiseAndName(Long idFranchise, String name);

    Mono<Boolean> existsById(Long idBranch);

    Flux<Branch> findAll();

    Flux<Branch> findAllByIdFranchise(Long idFranchise);

}


