package co.com.bancolombia.api.franchise;

import co.com.bancolombia.api.common.RequestValidator;
import co.com.bancolombia.api.common.ResponseUtil;
import co.com.bancolombia.api.franchise.dto.CreateFranchiseRequestDTO;
import co.com.bancolombia.api.franchise.mappers.FranchiseMapper;
import co.com.bancolombia.usecase.franchise.FranchiseUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

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
                .flatMap(ResponseUtil::created)
                .onErrorResume(IllegalArgumentException.class, ex ->
                        ResponseUtil.badRequest(ex.getMessage()))
                .onErrorResume(IllegalStateException.class, ex ->
                        ResponseUtil.conflict(ex.getMessage()));
    }

    public Mono<ServerResponse> getTopStockProductsByBranch(ServerRequest request) {
        Long idFranchise;
        try {
            idFranchise = Long.parseLong(request.pathVariable("idFranchise"));
        } catch (NumberFormatException exception) {
            return ResponseUtil.badRequest("idFranchise must be a number");
        }

        return franchiseUseCase.getTopStockProductsByBranch(idFranchise)
                .map(franchiseMapper::toTopStockProductByBranchResponse)
                .collectList()
                .flatMap(ResponseUtil::ok)
                .onErrorResume(IllegalArgumentException.class, ex -> ResponseUtil.badRequest(ex.getMessage()))
                .onErrorResume(NoSuchElementException.class, ex -> ResponseUtil.notFound(ex.getMessage()));
    }

}
