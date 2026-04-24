package co.com.bancolombia.api.franchise;

import co.com.bancolombia.api.common.RequestValidator;
import co.com.bancolombia.api.common.ResponseUtil;
import co.com.bancolombia.api.franchise.dto.CreateFranchiseRequestDTO;
import co.com.bancolombia.api.franchise.mappers.FranchiseMapper;
import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class FranchiseHandler {

    private final FranchiseUseCase franchiseUseCase;

    private final FranchiseMapper franchiseMapper;

    private final RequestValidator requestValidator;

    public Mono<ServerResponse> createFranchise(ServerRequest request) {
        return request.bodyToMono(CreateFranchiseRequestDTO.class)
                .doOnNext(requestValidator::validate)
                .map(franchiseMapper::toEntity)
                .flatMap(franchiseUseCase::create)
                .flatMap(ResponseUtil::created);
    }

    public Mono<ServerResponse> getTopStockProductsByBranch(ServerRequest request) {
        Long idFranchise = parsePathVariable(request, "idFranchise");

        return franchiseUseCase.getTopStockProductsByBranch(idFranchise)
                .map(franchiseMapper::toTopStockProductByBranchResponse)
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
