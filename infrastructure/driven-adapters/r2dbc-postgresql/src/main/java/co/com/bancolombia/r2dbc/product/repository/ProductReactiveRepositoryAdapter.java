package co.com.bancolombia.r2dbc.product.repository;

import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.product.entity.ProductEntity;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ProductReactiveRepositoryAdapter extends ReactiveAdapterOperations<Product, ProductEntity, Long, IProductReactiveRepository>
        implements ProductRepository {

    public ProductReactiveRepositoryAdapter(IProductReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Product.class));
    }

    @Override
    public Mono<Product> save(Product product) {
        return Mono.just(product)
                .map(this::toData)
                .flatMap(repository::save)
                .map(this::toEntity)
                .onErrorMap(DuplicateKeyException.class,
                        ex -> new IllegalStateException("Product name already exists for this branch"));
    }

    @Override
    public Mono<Boolean> existsByIdBranchAndName(Long idBranch, String name) {
        return repository.existsByIdBranchAndName(idBranch, name);
    }

}

