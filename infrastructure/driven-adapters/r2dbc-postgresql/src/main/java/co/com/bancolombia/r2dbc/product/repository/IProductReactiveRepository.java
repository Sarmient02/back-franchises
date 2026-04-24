package co.com.bancolombia.r2dbc.product.repository;

import co.com.bancolombia.r2dbc.product.entity.ProductEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface IProductReactiveRepository extends ReactiveCrudRepository<ProductEntity, Long>, ReactiveQueryByExampleExecutor<ProductEntity> {

    Mono<Boolean> existsByIdBranchAndName(Long idBranch, String name);

}
