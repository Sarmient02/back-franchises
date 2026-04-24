package co.com.bancolombia.api.branch;

import co.com.bancolombia.api.branch.dto.CreateBranchRequestDTO;
import co.com.bancolombia.api.branch.mappers.BranchMapper;
import co.com.bancolombia.api.common.RequestValidator;
import co.com.bancolombia.api.common.ResponseUtil;
import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.usecase.branch.BranchUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class BranchHandler {

    private final BranchUseCase branchUseCase;
    private final BranchMapper branchMapper;
    private final RequestValidator requestValidator;

    public Mono<ServerResponse> createBranch(ServerRequest request) {
        Long idFranchise = parsePathVariable(request, "idFranchise");

        return request.bodyToMono(CreateBranchRequestDTO.class)
                .doOnNext(requestValidator::validate)
                .map(body -> branchMapper.toEntity(body, idFranchise))
                .flatMap(branchUseCase::create)
                .map(branchMapper::toResponse)
                .flatMap(ResponseUtil::created);
    }

    private Long parsePathVariable(ServerRequest request, String name) {
        try {
            return Long.parseLong(request.pathVariable(name));
        } catch (NumberFormatException exception) {
            throw new BusinessException(BusinessErrorType.INVALID_INPUT, name + " must be a number");
        }
    }

}
