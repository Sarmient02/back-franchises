package co.com.bancolombia.api.common;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@UtilityClass
public final class ResponseUtil {

    public static <T> Mono<ServerResponse> ok(T body) {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ApiResponse.ok(body));
    }

    public static <T> Mono<ServerResponse> created(T body) {
        return ServerResponse.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ApiResponse.ok(body));
    }

    public static Mono<ServerResponse> badRequest(String message) {
        return error(HttpStatus.BAD_REQUEST, message);
    }

    public static Mono<ServerResponse> conflict(String message) {
        return error(HttpStatus.CONFLICT, message);
    }

    public static Mono<ServerResponse> notFound(String message) {
        return error(HttpStatus.NOT_FOUND, message);
    }

    public static Mono<ServerResponse> internalError(String message) {
        return error(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    private static Mono<ServerResponse> error(HttpStatus status, String message) {
        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(ApiResponse.error(status.toString(), message));
    }
}