package co.com.bancolombia.usecase.branch;

import co.com.bancolombia.model.branch.Branch;
import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BranchUseCaseTest {

    @Mock
    private BranchRepository branchRepository;

    @Mock
    private FranchiseRepository franchiseRepository;

    @InjectMocks
    private BranchUseCase branchUseCase;

    /* create */

    @Test
    void shouldCreateBranchSuccessfully() {
        Branch branch = Branch.builder().idFranchise(1L).name("Sucursal Centro").build();
        Branch saved = Branch.builder().id(1L).idFranchise(1L).name("Sucursal Centro").build();

        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(branchRepository.existsByIdFranchiseAndName(1L, "Sucursal Centro")).thenReturn(Mono.just(false));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(branchUseCase.create(branch))
                .expectNextMatches(b -> b.getId().equals(1L) && b.getName().equals("Sucursal Centro"))
                .verifyComplete();
    }

    @Test
    void shouldFailCreateWhenFranchiseNotFound() {
        Branch branch = Branch.builder().idFranchise(99L).name("Sucursal").build();

        when(franchiseRepository.existsById(99L)).thenReturn(Mono.just(false));

        StepVerifier.create(branchUseCase.create(branch))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.FRANCHISE_NOT_FOUND);
                })
                .verify();
    }

    @Test
    void shouldFailCreateWhenBranchNameAlreadyExists() {
        Branch branch = Branch.builder().idFranchise(1L).name("Duplicada").build();

        when(franchiseRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(branchRepository.existsByIdFranchiseAndName(1L, "Duplicada")).thenReturn(Mono.just(true));

        StepVerifier.create(branchUseCase.create(branch))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.BRANCH_ALREADY_EXISTS);
                })
                .verify();
    }

    @Test
    void shouldFailCreateWhenFranchiseIdIsInvalid() {
        Branch branch = Branch.builder().idFranchise(0L).name("Sucursal").build();

        StepVerifier.create(branchUseCase.create(branch))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();

        verifyNoInteractions(franchiseRepository, branchRepository);
    }

    @Test
    void shouldFailCreateWhenNameIsBlank() {
        Branch branch = Branch.builder().idFranchise(1L).name("  ").build();

        StepVerifier.create(branchUseCase.create(branch))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

    /* updateName */

    @Test
    void shouldUpdateNameSuccessfully() {
        Branch existing = Branch.builder().id(1L).idFranchise(1L).name("Old Name").build();
        Branch updated = Branch.builder().id(1L).idFranchise(1L).name("New Name").build();

        when(branchRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(branchRepository.save(any(Branch.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(branchUseCase.updateName(1L, "New Name"))
                .expectNextMatches(b -> b.getName().equals("New Name"))
                .verifyComplete();
    }

    @Test
    void shouldFailUpdateNameWhenBranchNotFound() {
        when(branchRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(branchUseCase.updateName(99L, "New Name"))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.BRANCH_NOT_FOUND);
                })
                .verify();
    }

    @Test
    void shouldFailUpdateNameWhenIdIsInvalid() {
        StepVerifier.create(branchUseCase.updateName(0L, "New Name"))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();

        verifyNoInteractions(branchRepository);
    }

    @Test
    void shouldFailUpdateNameWhenNameIsBlank() {
        StepVerifier.create(branchUseCase.updateName(1L, "   "))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

    /* getAll */

    @Test
    void shouldGetAllBranchesWhenFranchiseIdIsNull() {
        Branch branch = Branch.builder().id(1L).idFranchise(1L).name("Branch").build();

        when(branchRepository.findAll()).thenReturn(Flux.just(branch));

        StepVerifier.create(branchUseCase.getAll(null))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void shouldGetAllBranchesByFranchiseId() {
        Branch branch = Branch.builder().id(1L).idFranchise(1L).name("Branch").build();

        when(branchRepository.findAllByIdFranchise(1L)).thenReturn(Flux.just(branch));

        StepVerifier.create(branchUseCase.getAll(1L))
                .expectNext(branch)
                .verifyComplete();
    }

    @Test
    void shouldFailGetAllWhenFranchiseIdIsInvalid() {
        StepVerifier.create(branchUseCase.getAll(0L))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

}
