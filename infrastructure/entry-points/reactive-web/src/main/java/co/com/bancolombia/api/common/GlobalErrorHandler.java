package co.com.bancolombia.api.common;

import co.com.bancolombia.model.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@Order(-2)
public class GlobalErrorHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (ex instanceof BusinessException businessException) {
            return writeResponse(exchange, businessException.getType().getHttpStatus(),
                    businessException.getType().name(), businessException.getMessage());
        }

        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return writeResponse(exchange, HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR", "An unexpected error occurred");
    }

    private Mono<Void> writeResponse(ServerWebExchange exchange, int status, String code, String message) {
        exchange.getResponse().setStatusCode(HttpStatus.valueOf(status));
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String json = "{\"success\":false,\"error\":{\"code\":\"%s\",\"message\":\"%s\"}}"
                .formatted(escapeJson(code), escapeJson(message));

        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private String escapeJson(String value) {
        if (value == null) return "";
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
