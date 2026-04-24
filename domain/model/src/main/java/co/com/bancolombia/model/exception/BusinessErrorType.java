package co.com.bancolombia.model.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorType {

    FRANCHISE_NOT_FOUND("Franchise not found", 404),
    FRANCHISE_ALREADY_EXISTS("Franchise name already exists", 409),

    BRANCH_NOT_FOUND("Branch not found", 404),
    BRANCH_ALREADY_EXISTS("Branch name already exists for this franchise", 409),

    PRODUCT_NOT_FOUND("Product not found", 404),
    PRODUCT_ALREADY_EXISTS("Product name already exists for this branch", 409),

    INVALID_INPUT("Invalid input", 400);

    private final String message;
    private final int httpStatus;

}
