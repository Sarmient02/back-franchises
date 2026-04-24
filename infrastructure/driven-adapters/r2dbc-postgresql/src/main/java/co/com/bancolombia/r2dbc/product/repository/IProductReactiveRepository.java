package co.com.bancolombia.r2dbc.product.repository;

import co.com.bancolombia.r2dbc.product.entity.ProductEntity;
import co.com.bancolombia.r2dbc.product.projection.TopStockProductProjection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductReactiveRepository extends ReactiveCrudRepository<ProductEntity, Long>, ReactiveQueryByExampleExecutor<ProductEntity> {

    Mono<Boolean> existsByIdBranchAndName(Long idBranch, String name);

    Flux<ProductEntity> findAllByIdBranch(Long idBranch);

    @Query("""
            SELECT ranked.id,
                   ranked.id_branch,
                   ranked.branch_name,
                   ranked.name,
                   ranked.stock_quantity
            FROM (
                SELECT p.id,
                       p.id_branch,
                       b.name AS branch_name,
                       p.name,
                       p.stock_quantity,
                       ROW_NUMBER() OVER (
                           PARTITION BY p.id_branch
                           ORDER BY p.stock_quantity DESC, p.id
                       ) AS rn
                FROM inventory.product p
                JOIN inventory.branch b
                    ON b.id = p.id_branch
                WHERE b.id_franchise = :idFranchise
            ) ranked
            WHERE ranked.rn = 1
            ORDER BY ranked.stock_quantity DESC, ranked.branch_name
            """)
    Flux<TopStockProductProjection> findTopStockProductsByFranchiseId(@Param("idFranchise") Long idFranchise);

}
