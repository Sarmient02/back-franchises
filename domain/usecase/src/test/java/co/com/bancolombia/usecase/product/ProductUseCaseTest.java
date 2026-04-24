package co.com.bancolombia.usecase.product;

import co.com.bancolombia.model.branch.gateways.BranchRepository;
import co.com.bancolombia.model.exception.BusinessErrorType;
import co.com.bancolombia.model.exception.BusinessException;
import co.com.bancolombia.model.product.Product;
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
class ProductUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BranchRepository branchRepository;

    @InjectMocks
    private ProductUseCase productUseCase;

    /* create */

    @Test
    void shouldCreateProductSuccessfully() {
        Product product = Product.builder().idBranch(1L).name("Big Mac").stockQuantity(10).build();
        Product saved = Product.builder().id(1L).idBranch(1L).name("Big Mac").stockQuantity(10).build();

        when(branchRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(productRepository.existsByIdBranchAndName(1L, "Big Mac")).thenReturn(Mono.just(false));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(saved));

        StepVerifier.create(productUseCase.create(product))
                .expectNextMatches(p -> p.getId().equals(1L) && p.getName().equals("Big Mac"))
                .verifyComplete();
    }

    @Test
    void shouldFailCreateWhenBranchNotFound() {
        Product product = Product.builder().idBranch(99L).name("Big Mac").stockQuantity(10).build();

        when(branchRepository.existsById(99L)).thenReturn(Mono.just(false));

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.BRANCH_NOT_FOUND);
                })
                .verify();
    }

    @Test
    void shouldFailCreateWhenProductNameAlreadyExists() {
        Product product = Product.builder().idBranch(1L).name("Duplicado").stockQuantity(5).build();

        when(branchRepository.existsById(1L)).thenReturn(Mono.just(true));
        when(productRepository.existsByIdBranchAndName(1L, "Duplicado")).thenReturn(Mono.just(true));

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.PRODUCT_ALREADY_EXISTS);
                })
                .verify();
    }

    @Test
    void shouldFailCreateWhenBranchIdIsInvalid() {
        Product product = Product.builder().idBranch(0L).name("Test").stockQuantity(5).build();

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();

        verifyNoInteractions(branchRepository, productRepository);
    }

    @Test
    void shouldFailCreateWhenNameIsBlank() {
        Product product = Product.builder().idBranch(1L).name("  ").stockQuantity(5).build();

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

    @Test
    void shouldFailCreateWhenStockIsNegative() {
        Product product = Product.builder().idBranch(1L).name("Test").stockQuantity(-1).build();

        StepVerifier.create(productUseCase.create(product))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

    /* deleteById */

    @Test
    void shouldDeleteProductSuccessfully() {
        when(productRepository.existsProductById(1L)).thenReturn(Mono.just(true));
        when(productRepository.deleteProductById(1L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.deleteById(1L))
                .verifyComplete();

        verify(productRepository).deleteProductById(1L);
    }

    @Test
    void shouldFailDeleteWhenProductNotFound() {
        when(productRepository.existsProductById(99L)).thenReturn(Mono.just(false));

        StepVerifier.create(productUseCase.deleteById(99L))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.PRODUCT_NOT_FOUND);
                })
                .verify();
    }

    @Test
    void shouldFailDeleteWhenIdIsInvalid() {
        StepVerifier.create(productUseCase.deleteById(0L))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();

        verifyNoInteractions(productRepository);
    }

    /* patch */

    @Test
    void shouldPatchStockSuccessfully() {
        Product existing = Product.builder().id(1L).idBranch(1L).name("Big Mac").stockQuantity(10).build();
        Product updated = Product.builder().id(1L).idBranch(1L).name("Big Mac").stockQuantity(50).build();

        when(productRepository.findProductById(1L)).thenReturn(Mono.just(existing));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productUseCase.patch(1L, null, 50))
                .expectNextMatches(p -> p.getStockQuantity().equals(50) && p.getName().equals("Big Mac"))
                .verifyComplete();
    }

    @Test
    void shouldPatchNameSuccessfully() {
        Product existing = Product.builder().id(1L).idBranch(1L).name("Old Name").stockQuantity(10).build();
        Product updated = Product.builder().id(1L).idBranch(1L).name("New Name").stockQuantity(10).build();

        when(productRepository.findProductById(1L)).thenReturn(Mono.just(existing));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productUseCase.patch(1L, "New Name", null))
                .expectNextMatches(p -> p.getName().equals("New Name") && p.getStockQuantity().equals(10))
                .verifyComplete();
    }

    @Test
    void shouldPatchBothNameAndStock() {
        Product existing = Product.builder().id(1L).idBranch(1L).name("Old").stockQuantity(10).build();
        Product updated = Product.builder().id(1L).idBranch(1L).name("New").stockQuantity(99).build();

        when(productRepository.findProductById(1L)).thenReturn(Mono.just(existing));
        when(productRepository.save(any(Product.class))).thenReturn(Mono.just(updated));

        StepVerifier.create(productUseCase.patch(1L, "New", 99))
                .expectNextMatches(p -> p.getName().equals("New") && p.getStockQuantity().equals(99))
                .verifyComplete();
    }

    @Test
    void shouldFailPatchWhenProductNotFound() {
        when(productRepository.findProductById(99L)).thenReturn(Mono.empty());

        StepVerifier.create(productUseCase.patch(99L, "New Name", null))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.PRODUCT_NOT_FOUND);
                })
                .verify();
    }

    @Test
    void shouldFailPatchWhenNoFieldsProvided() {
        StepVerifier.create(productUseCase.patch(1L, null, null))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                    assertThat(ex).hasMessage("At least one field to update is required");
                })
                .verify();

        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldFailPatchWhenIdIsInvalid() {
        StepVerifier.create(productUseCase.patch(0L, "Name", null))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();

        verifyNoInteractions(productRepository);
    }

    @Test
    void shouldFailPatchWhenStockIsNegative() {
        Product existing = Product.builder().id(1L).idBranch(1L).name("Big Mac").stockQuantity(10).build();

        when(productRepository.findProductById(1L)).thenReturn(Mono.just(existing));

        StepVerifier.create(productUseCase.patch(1L, null, -5))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

    /* getAll */

    @Test
    void shouldGetAllProductsWhenBranchIdIsNull() {
        Product product = Product.builder().id(1L).idBranch(1L).name("Big Mac").stockQuantity(10).build();

        when(productRepository.findAll()).thenReturn(Flux.just(product));

        StepVerifier.create(productUseCase.getAll(null))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void shouldGetAllProductsByBranchId() {
        Product product = Product.builder().id(1L).idBranch(1L).name("Big Mac").stockQuantity(10).build();

        when(productRepository.findAllByIdBranch(1L)).thenReturn(Flux.just(product));

        StepVerifier.create(productUseCase.getAll(1L))
                .expectNext(product)
                .verifyComplete();
    }

    @Test
    void shouldFailGetAllWhenBranchIdIsInvalid() {
        StepVerifier.create(productUseCase.getAll(0L))
                .expectErrorSatisfies(ex -> {
                    assertThat(ex).isInstanceOf(BusinessException.class);
                    assertThat(((BusinessException) ex).getType()).isEqualTo(BusinessErrorType.INVALID_INPUT);
                })
                .verify();
    }

}
