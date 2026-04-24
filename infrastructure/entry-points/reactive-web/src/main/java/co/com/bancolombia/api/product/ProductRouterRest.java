package co.com.bancolombia.api.product;

import co.com.bancolombia.api.product.dto.CreateProductRequestDTO;
import co.com.bancolombia.api.product.dto.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    beanClass = ProductHandler.class,
                    beanMethod = "createProduct",
                    operation = @Operation(
                            operationId = "createProduct",
                            summary = "Create a new product for a branch",
                            tags = {"Product"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "idBranch",
                                            description = "ID of the branch to which the product belongs",
                                            required = true,
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Product to create",
                                    content = @Content(schema = @Schema(implementation = CreateProductRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Successful operation",
                                            content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Branch not found")
                            }
                    ))
    })
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return route()
                .POST("/branches/{idBranch}/products", handler::createProduct)
                .build();
    }

}

