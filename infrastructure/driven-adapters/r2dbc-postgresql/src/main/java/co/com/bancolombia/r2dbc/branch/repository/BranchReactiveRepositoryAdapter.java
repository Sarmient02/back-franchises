package co.com.bancolombia.r2dbc.branch.repository;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.r2dbc.branch.entity.BranchEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BranchReactiveRepositoryAdapter extends ReactiveAdapterOperations<Branch, BranchEntity, Long, IBranchReactiveRepository>
        implements BranchRepository {

    public BranchReactiveRepositoryAdapter(IBranchReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Branch.class));
    }

    @Override
    public Mono<Branch> save(Branch branch) {
        return Mono.just(branch)
                .map(this::toData)
                .flatMap(repository::save)
                .map(this::toEntity)
                .onErrorMap(DuplicateKeyException.class,
                        ex -> new BusinessException(BusinessErrorType.BRANCH_ALREADY_EXISTS));
    }

    @Override
    public Mono<Boolean> existsByIdFranchiseAndName(Long idFranchise, String name) {
        return repository.existsByIdFranchiseAndName(idFranchise, name);
    }

    @Override
    public Mono<Boolean> existsById(Long idBranch) {
        return repository.existsById(idBranch);
    }

    @Override
    public Mono<Branch> findById(Long idBranch) {
        return repository.findById(idBranch)
                .map(this::toEntity);
    }

}


