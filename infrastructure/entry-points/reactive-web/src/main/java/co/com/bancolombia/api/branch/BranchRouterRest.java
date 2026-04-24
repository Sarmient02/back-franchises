package co.com.bancolombia.api.branch;

import co.com.bancolombia.api.branch.dto.BranchResponseDTO;
import co.com.bancolombia.api.branch.dto.CreateBranchRequestDTO;
import co.com.bancolombia.api.branch.dto.UpdateBranchNameRequestDTO;
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
public class BranchRouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/franchises/{idFranchise}/branches",
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class,
                    beanMethod = "createBranch",
                    operation = @Operation(
                            operationId = "createBranch",
                            summary = "Create a new branch for a franchise",
                            tags = {"Branch"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "idFranchise",
                                            description = "ID of the franchise to which the branch belongs",
                                            required = true,
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Branch to create",
                                    content = @Content(schema = @Schema(implementation = CreateBranchRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch created",
                                            content = @Content(schema = @Schema(implementation = BranchResponseDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found")
                            }
                    )),
            @RouterOperation(
                    path = "/branches/{idBranch}",
                    method = RequestMethod.PATCH,
                    beanClass = BranchHandler.class,
                    beanMethod = "patchBranch",
                    operation = @Operation(
                            operationId = "patchBranch",
                            summary = "Patch branch",
                            tags = {"Branch"},
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "idBranch",
                                            description = "ID of the branch to patch",
                                            required = true,
                                            in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH
                                    )
                            },
                            requestBody = @RequestBody(
                                    required = true,
                                    description = "Fields to patch for the branch",
                                    content = @Content(schema = @Schema(implementation = UpdateBranchNameRequestDTO.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch updated",
                                            content = @Content(schema = @Schema(implementation = BranchResponseDTO.class))),
                                    @ApiResponse(responseCode = "404", description = "Branch not found"),
                                    @ApiResponse(responseCode = "409", description = "Branch name already exists")
                            }
                    ))
    })
    public RouterFunction<ServerResponse> branchRoutes(BranchHandler handler) {
        return route()
                .POST("/franchises/{idFranchise}/branches", handler::createBranch)
                .PATCH("/branches/{idBranch}", handler::patchBranch)
                .build();
    }

}

