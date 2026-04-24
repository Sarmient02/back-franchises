package co.com.bancolombia.model.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final BusinessErrorType type;

    public BusinessException(BusinessErrorType type) {
        super(type.getMessage());
        this.type = type;
    }

    public BusinessException(BusinessErrorType type, String customMessage) {
        super(customMessage);
        this.type = type;
    }

}
