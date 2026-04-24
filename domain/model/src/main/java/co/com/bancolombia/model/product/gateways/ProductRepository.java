package co.com.bancolombia.model.product.gateways;

import co.com.bancolombia.model.product.Product;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductRepository {

    Mono<Product> save(Product product);

    Mono<Product> findProductById(Long idProduct);

    Mono<Boolean> existsByIdBranchAndName(Long idBranch, String name);

    Mono<Boolean> existsProductById(Long idProduct);

    Mono<Void> deleteProductById(Long idProduct);

    Flux<TopStockProductByBranch> findTopStockProductsByFranchiseId(Long idFranchise);

    Flux<Product> findAll();

    Flux<Product> findAllByIdBranch(Long idBranch);

}
