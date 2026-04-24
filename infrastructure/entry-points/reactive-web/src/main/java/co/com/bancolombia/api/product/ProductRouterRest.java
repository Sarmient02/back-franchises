package co.com.bancolombia.api.product;

import co.com.bancolombia.api.product.dto.CreateProductRequestDTO;
import co.com.bancolombia.api.product.dto.PatchProductRequestDTO;
import co.com.bancolombia.api.product.dto.ProductResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/products",
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class,
                    beanMethod = "getAllProducts",
                    operation = @Operation(
                            operationId = "getAllProducts",
                            summary = "Get all products",
                            tags = {"Product"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "idBranch",
                                            description = "Optional ID of the branch to filter products",
                                            required = false,
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation",
                                            content = @Content(array = @ArraySchema(
                                                    schema = @Schema(implementation = ProductResponseDTO.class))))
                            }
                    )),
            @RouterOperation(
                    path = "/api/branches/{idBranch}/products",
                    method = RequestMethod.POST,
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
                    )),
            @RouterOperation(
                    path = "/api/products/{idProduct}",
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProduct",
                            summary = "Delete a product",
                            tags = {"Product"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "idProduct",
                                            description = "ID of the product to delete",
                                            required = true,
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
                                    @ApiResponse(responseCode = "404", description = "Product not found")
                            }
                    )),
            @RouterOperation(
                    path = "/api/products/{idProduct}",
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "patchProduct",
                    operation = @Operation(
                            operationId = "patchProduct",
                            summary = "Patch product",
                            tags = {"Product"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "idProduct",
                                            description = "ID of the product to patch",
                                            required = true,
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Fields to patch for the product",
                                    content = @Content(schema = @Schema(implementation = PatchProductRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product updated",
                                            content = @Content(schema = @Schema(implementation = ProductResponseDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Product not found")
                            }
                    ))
    })
    public RouterFunction<ServerResponse> productRoutes(ProductHandler handler) {
        return route()
                .path("/api", builder -> builder
                        .GET("/products", handler::getAllProducts)
                        .POST("/branches/{idBranch}/products", handler::createProduct)
                        .DELETE("/products/{idProduct}", handler::deleteProduct)
                        .PATCH("/products/{idProduct}", handler::patchProduct)
                )
                .build();
    }

}

