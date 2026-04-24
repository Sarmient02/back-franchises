package co.com.bancolombia.r2dbc.product.repository;

import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import co.com.bancolombia.model.product.gateways.ProductRepository;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import co.com.bancolombia.r2dbc.product.entity.ProductEntity;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
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
                        ex -> new BusinessException(BusinessErrorType.PRODUCT_ALREADY_EXISTS));
    }

    @Override
    public Mono<Product> findProductById(Long idProduct) {
        return repository.findById(idProduct)
                .map(this::toEntity);
    }

    @Override
    public Mono<Boolean> existsByIdBranchAndName(Long idBranch, String name) {
        return repository.existsByIdBranchAndName(idBranch, name);
    }

    @Override
    public Mono<Boolean> existsProductById(Long idProduct) {
        return repository.existsById(idProduct);
    }

    @Override
    public Mono<Void> deleteProductById(Long idProduct) {
        return repository.deleteById(idProduct);
    }

    @Override
    public Flux<TopStockProductByBranch> findTopStockProductsByFranchiseId(Long idFranchise) {
        return repository.findTopStockProductsByFranchiseId(idFranchise)
                .map(projection -> TopStockProductByBranch.builder()
                        .id(projection.getId())
                        .idBranch(projection.getIdBranch())
                        .branchName(projection.getBranchName())
                        .name(projection.getName())
                        .stockQuantity(projection.getStockQuantity())
                        .build());
    }

}
