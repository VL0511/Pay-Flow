package io.payflow.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(boolean success,
                       LocalDateTime timestamp,
                       int status,
                       String error,
                       String message,
                       String path,
                       List<String> fieldErrors) {
}
