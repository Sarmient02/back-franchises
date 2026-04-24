package co.com.bancolombia.api.branch;

import co.com.bancolombia.api.branch.dto.CreateBranchRequestDTO;
import co.com.bancolombia.api.branch.mappers.BranchMapper;
import co.com.bancolombia.api.common.RequestValidator;
import co.com.bancolombia.api.common.ResponseUtil;
import co.com.bancolombia.usecase.branch.BranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final BranchUseCase branchUseCase;
    private final BranchMapper branchMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        Long idFranchise;
        try {
            idFranchise = Long.parseLong(request.pathVariable("idFranchise"));
        } catch (NumberFormatException exception) {
            return ResponseUtil.badRequest("idFranchise must be a number");
        }

        return request.bodyToMono(CreateBranchRequestDTO.class)
                .doOnNext(requestValidator::validate)
                .map(body -> branchMapper.toEntity(body, idFranchise))
                .flatMap(branchUseCase::create)
                .map(branchMapper::toResponse)
                .flatMap(ResponseUtil::created)
                .onErrorResume(IllegalArgumentException.class, ex -> ResponseUtil.badRequest(ex.getMessage()))
                .onErrorResume(NoSuchElementException.class, ex -> ResponseUtil.notFound(ex.getMessage()))
                .onErrorResume(IllegalStateException.class, ex -> ResponseUtil.conflict(ex.getMessage()));
    }

}


