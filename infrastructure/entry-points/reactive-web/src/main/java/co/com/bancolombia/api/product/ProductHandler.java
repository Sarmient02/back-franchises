package co.com.bancolombia.api.product;

import co.com.bancolombia.api.common.RequestValidator;
import co.com.bancolombia.api.common.ResponseUtil;
import co.com.bancolombia.api.product.dto.CreateProductRequestDTO;
import co.com.bancolombia.api.product.dto.UpdateProductStockRequestDTO;
import co.com.bancolombia.api.product.mappers.ProductMapper;
import co.com.bancolombia.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductUseCase productUseCase;
    private final ProductMapper productMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        Long idBranch;
        try {
            idBranch = Long.parseLong(request.pathVariable("idBranch"));
        } catch (NumberFormatException exception) {
            return ResponseUtil.badRequest("idBranch must be a number");
        }

        return request.bodyToMono(CreateProductRequestDTO.class)
                .doOnNext(requestValidator::validate)
                .map(body -> productMapper.toEntity(body, idBranch))
                .flatMap(productUseCase::create)
                .map(productMapper::toResponse)
                .flatMap(ResponseUtil::created)
                .onErrorResume(IllegalArgumentException.class, ex -> ResponseUtil.badRequest(ex.getMessage()))
                .onErrorResume(NoSuchElementException.class, ex -> ResponseUtil.notFound(ex.getMessage()))
                .onErrorResume(IllegalStateException.class, ex -> ResponseUtil.conflict(ex.getMessage()));
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        Long idProduct;
        try {
            idProduct = Long.parseLong(request.pathVariable("idProduct"));
        } catch (NumberFormatException exception) {
            return ResponseUtil.badRequest("idProduct must be a number");
        }

        return productUseCase.deleteById(idProduct)
                .then(ResponseUtil.ok(Map.of("message", "Product deleted successfully")))
                .onErrorResume(IllegalArgumentException.class, ex -> ResponseUtil.badRequest(ex.getMessage()))
                .onErrorResume(NoSuchElementException.class, ex -> ResponseUtil.notFound(ex.getMessage()));
    }

    public Mono<ServerResponse> patchProduct(ServerRequest request) {
        Long idProduct;
        try {
            idProduct = Long.parseLong(request.pathVariable("idProduct"));
        } catch (NumberFormatException exception) {
            return ResponseUtil.badRequest("idProduct must be a number");
        }

        return request.bodyToMono(UpdateProductStockRequestDTO.class)
                .doOnNext(requestValidator::validate)
                .flatMap(body -> productUseCase.updateStock(idProduct, body.stockQuantity()))
                .map(productMapper::toResponse)
                .flatMap(ResponseUtil::ok)
                .onErrorResume(IllegalArgumentException.class, ex -> ResponseUtil.badRequest(ex.getMessage()))
                .onErrorResume(NoSuchElementException.class, ex -> ResponseUtil.notFound(ex.getMessage()));
    }

}
