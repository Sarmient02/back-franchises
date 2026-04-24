package co.com.bancolombia.usecase.franchise;

import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.franchise.Franchise;
import co.com.bancolombia.model.franchise.gateways.FranchiseRepository;
import co.com.bancolombia.model.product.TopStockProductByBranch;
import co.com.bancolombia.model.product.gateways.ProductRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FranchiseUseCaseTest {

    @Mock
    private FranchiseRepository franchiseRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private FranchiseUseCase franchiseUseCase;

    /* create */

    @Test
    void shouldCreateFranchiseSuccessfully() {
        Franchise franchise = Franchise.builder().name("McDonald's").build();
        Franchise saved = Franchise.builder().id(1L).name("McDonald's").build();

        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(franchiseUseCase.create(franchise))
                .expectNextMatches(f -> f.getId().equals(1L) && f.getName().equals("McDonald's"))
                .verifyComplete();
    }

    @Test
    void shouldFailCreateWhenNameIsNull() {
        Franchise franchise = Franchise.builder().name(null).build();

        StepVerifier.create(franchiseUseCase.create(franchise))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

    @Test
    void shouldFailCreateWhenNameIsBlank() {
        Franchise franchise = Franchise.builder().name("   ").build();

        StepVerifier.create(franchiseUseCase.create(franchise))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

    /* updateName */

    @Test
    void shouldUpdateNameSuccessfully() {
        Franchise existing = Franchise.builder().id(1L).name("Old Name").build();
        Franchise updated = Franchise.builder().id(1L).name("New Name").build();

        when(franchiseRepository.findById(1L)).thenReturn(Mono.just(existing));
        when(franchiseRepository.save(any(Franchise.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(franchiseUseCase.updateName(1L, "New Name"))
                .expectNextMatches(f -> f.getName().equals("New Name"))
                .verifyComplete();
    }

    @Test
    void shouldFailUpdateNameWhenFranchiseNotFound() {
        when(franchiseRepository.findById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(franchiseUseCase.updateName(99L, "New Name"))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.FRANCHISE_NOT_FOUND);
                })
                .verify();
    }

    @Test
    void shouldFailUpdateNameWhenIdIsInvalid() {
        StepVerifier.create(franchiseUseCase.updateName(0L, "New Name"))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();

        verifyNoInteractions(franchiseRepository);
    }

    @Test
    void shouldFailUpdateNameWhenNameIsBlank() {
        StepVerifier.create(franchiseUseCase.updateName(1L, "   "))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

    /* getTopStockProductsByBranch */

    @Test
    void shouldGetTopStockProductsByBranchWhenFranchiseExists() {
        TopStockProductByBranch firstProduct = TopStockProductByBranch.builder()
                .id(10L).idBranch(1L).branchName("Centro").name("Laptop").stockQuantity(25).build();
        TopStockProductByBranch secondProduct = TopStockProductByBranch.builder()
                .id(20L).idBranch(2L).branchName("Norte").name("Mouse").stockQuantity(15).build();

        when(franchiseRepository.existsById(7L)).thenReturn(Mono.just(true));
        when(productRepository.findTopStockProductsByFranchiseId(7L))
                .thenReturn(Flux.just(firstProduct, secondProduct));

        StepVerifier.create(franchiseUseCase.getTopStockProductsByBranch(7L))
                .expectNext(firstProduct)
                .expectNext(secondProduct)
                .verifyComplete();

        verify(franchiseRepository).existsById(7L);
        verify(productRepository).findTopStockProductsByFranchiseId(7L);
    }

    @Test
    void shouldFailGetTopStockWhenFranchiseDoesNotExist() {
        when(franchiseRepository.existsById(99L)).thenReturn(Mono.just(false));

        StepVerifier.create(franchiseUseCase.getTopStockProductsByBranch(99L))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.FRANCHISE_NOT_FOUND);
                })
                .verify();

        verify(franchiseRepository).existsById(99L);
        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldFailGetTopStockWhenFranchiseIdIsInvalid() {
        StepVerifier.create(franchiseUseCase.getTopStockProductsByBranch(0L))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();

        verifyNoInteractions(franchiseRepository, productRepository);
    }

    /* getAll */

    @Test
    void shouldGetAllFranchises() {
        Franchise franchise = Franchise.builder().id(1L).name("McDonald's").build();

        when(franchiseRepository.findAll()).thenReturn(Flux.just(franchise));

        StepVerifier.create(franchiseUseCase.getAll())
                .expectNext(franchise)
                .verifyComplete();
    }

}
