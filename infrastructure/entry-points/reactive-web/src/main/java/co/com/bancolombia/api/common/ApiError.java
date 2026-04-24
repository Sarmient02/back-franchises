package co.com.bancolombia.api.common;

public record ApiError(
        String code,
        String message
) {}