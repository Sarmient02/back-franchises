package co.com.bancolombia.r2dbc.franchise.repository;

import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.r2dbc.franchise.entity.FranchiseEntity;
import co.com.bancolombia.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public class FranchiseReactiveRepositoryAdapter extends ReactiveAdapterOperations<Franchise, FranchiseEntity, Long, IFranchiseReactiveRepository> implements FranchiseRepository {

    public FranchiseReactiveRepositoryAdapter(IFranchiseReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Franchise.class));
    }

    @Override
    public Mono<Franchise> save(Franchise franchise) {
        return Mono.just(franchise)
                .map(this::toData)
                .flatMap(repository::save)
                .map(this::toEntity)
                .onErrorMap(DuplicateKeyException.class,
                        ex -> new BusinessException(BusinessErrorType.FRANCHISE_ALREADY_EXISTS));
    }

    @Override
    public Mono<Boolean> existsById(Long idFranchise) {
        return repository.existsById(idFranchise);
    }

    @Override
    public Mono<Franchise> findById(Long idFranchise) {
        return repository.findById(idFranchise)
                .map(this::toEntity);
    }

    @Override
    public Flux<Franchise> findAll() {
        return repository.findAll().map(this::toEntity);
    }

}
