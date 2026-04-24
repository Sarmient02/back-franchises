package co.com.bancolombia.r2dbc.branch.repository;

import co.com.bancolombia.r2dbc.branch.entity.BranchEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IBranchReactiveRepository extends ReactiveCrudRepository<BranchEntity, Long>, ReactiveQueryByExampleExecutor<BranchEntity> {

    Mono<Boolean> existsByIdFranchiseAndName(Long idFranchise, String name);

}

