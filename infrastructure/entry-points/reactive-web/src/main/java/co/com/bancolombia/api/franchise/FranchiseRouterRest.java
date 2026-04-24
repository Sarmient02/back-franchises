package co.com.bancolombia.api.franchise;

import co.com.bancolombia.api.franchise.dto.CreateFranchiseRequestDTO;
import co.com.bancolombia.api.franchise.dto.FranchiseResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
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
                                    @ApiResponse(responseCode = "200", description = "Successful operation",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponseDTO.class)))}
                    ))}
    )
    public RouterFunction<ServerResponse> franchiseRoutes(FranchiseHandler handler) {
        return route()
                .POST("/franchises", handler::createFranchise)
                .build();
    }
}
