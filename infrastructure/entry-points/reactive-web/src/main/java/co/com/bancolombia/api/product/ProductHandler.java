package co.com.bancolombia.api.product;

import co.com.bancolombia.api.common.RequestValidator;
import co.com.bancolombia.api.common.ResponseUtil;
import co.com.bancolombia.api.product.dto.CreateProductRequestDTO;
import co.com.bancolombia.api.product.dto.PatchProductRequestDTO;
import co.com.bancolombia.api.product.mappers.ProductMapper;
import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.usecase.product.ProductUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ProductHandler {

    private final ProductUseCase productUseCase;
    private final ProductMapper productMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> createProduct(ServerRequest request) {
        Long idBranch = parsePathVariable(request, "idBranch");

        return request.bodyToMono(CreateProductRequestDTO.class)
                .doOnNext(requestValidator::validate)
                .map(body -> productMapper.toEntity(body, idBranch))
                .flatMap(productUseCase::create)
                .map(productMapper::toResponse)
                .flatMap(ResponseUtil::created);
    }

    public Mono<ServerResponse> deleteProduct(ServerRequest request) {
        Long idProduct = parsePathVariable(request, "idProduct");

        return productUseCase.deleteById(idProduct)
                .then(ResponseUtil.ok(Map.of("message", "Product deleted successfully")));
    }

    public Mono<ServerResponse> patchProduct(ServerRequest request) {
        Long idProduct = parsePathVariable(request, "idProduct");

        return request.bodyToMono(PatchProductRequestDTO.class)
                .doOnNext(requestValidator::validate)
                .flatMap(body -> productUseCase.patch(idProduct, body.name(), body.stockQuantity()))
                .map(productMapper::toResponse)
                .flatMap(ResponseUtil::ok);
    }

    public Mono<ServerResponse> getAllProducts(ServerRequest request) {
        Long idBranch = request.queryParam("idBranch")
                .map(id -> {
                    try {
                        return Long.parseLong(id);
                    } catch (NumberFormatException e) {
                        throw new BusinessException(BusinessErrorType.INVALID_INPUT, "idBranch must be a number");
                    }
                }).orElse(null);

        return productUseCase.getAll(idBranch)
                .map(productMapper::toResponse)
                .collectList()
                .flatMap(ResponseUtil::ok);
    }

    private Long parsePathVariable(ServerRequest request, String name) {
        try {
            return Long.parseLong(request.pathVariable(name));
        } catch (NumberFormatException exception) {
            throw new BusinessException(BusinessErrorType.INVALID_INPUT, name + " must be a number");
        }
    }

}
