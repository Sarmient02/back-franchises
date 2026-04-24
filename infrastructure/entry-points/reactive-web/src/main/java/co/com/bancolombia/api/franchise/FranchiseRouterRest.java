package co.com.bancolombia.api.franchise;

import co.com.bancolombia.api.franchise.dto.CreateFranchiseRequestDTO;
import co.com.bancolombia.api.franchise.dto.FranchiseResponseDTO;
import co.com.bancolombia.api.franchise.dto.TopStockProductByBranchResponseDTO;
import co.com.bancolombia.api.franchise.dto.UpdateFranchiseNameRequestDTO;
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
public class FranchiseRouterRest {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/franchises",
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a new franchise",
                            tags = {"Franchise"},
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Franchise to create",
                                    content = @Content(schema = @Schema(implementation = CreateFranchiseRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponseDTO.class)))}
                    )),
            @RouterOperation(
                    path = "/franchises/{idFranchise}",
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "patchFranchise",
                    operation = @Operation(
                            operationId = "patchFranchise",
                            summary = "Patch franchise",
                            tags = {"Franchise"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "idFranchise",
                                            description = "ID of the franchise to update",
                                            required = true,
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "New name for the franchise",
                                    content = @Content(schema = @Schema(implementation = UpdateFranchiseNameRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise updated",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponseDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found"),
                                    @ApiResponse(responseCode = "409", description = "Franchise name already exists")
                            }
                    )),
            @RouterOperation(
                    path = "/franchises/{idFranchise}/products/top-stock-by-branch",
                    method = RequestMethod.GET,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "getTopStockProductsByBranch",
                    operation = @Operation(
                            operationId = "getTopStockProductsByBranch",
                            summary = "Get the top stock product for each branch in a franchise",
                            tags = {"Franchise"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "idFranchise",
                                            description = "ID of the franchise",
                                            required = true,
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
                                    )
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation",
                                            content = @Content(array = @ArraySchema(
                                                    schema = @Schema(implementation = TopStockProductByBranchResponseDTO.class)))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found")
                            }
                    ))}
    )
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return route()
                .POST("/franchises", handler::createFranchise)
                .PATCH("/franchises/{idFranchise}", handler::patchFranchise)
                .GET("/franchises/{idFranchise}/products/top-stock-by-branch", handler::getTopStockProductsByBranch)
                .build();
    }
}

