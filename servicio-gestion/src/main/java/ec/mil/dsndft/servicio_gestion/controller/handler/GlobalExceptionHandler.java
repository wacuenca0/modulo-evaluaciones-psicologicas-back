package ec.mil.dsndft.servicio_gestion.controller.handler;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;

import org.springframework.dao.DataIntegrityViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
        return buildError(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest request) {
        String path = request != null ? request.getRequestURI() : "";
        log.error("Unexpected error on {}", path, ex);
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error inesperado", request);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, HttpServletRequest request) {
        String path = request != null ? request.getRequestURI() : "";
        String message = ex.getRootCause() != null && ex.getRootCause().getMessage() != null && ex.getRootCause().getMessage().contains("ORA-00001")
                ? "Ya existe un registro con un valor único duplicado (por ejemplo: cédula, usuarioId, username o email). Cambia los datos e intenta de nuevo."
                : "Error de integridad de datos: " + ex.getMostSpecificCause().getMessage();
        log.warn("Data integrity violation on {}: {}", path, message);
        return buildError(HttpStatus.CONFLICT, message, request);
    }

    private ResponseEntity<ApiError> buildError(HttpStatus status, String message, HttpServletRequest request) {
        String path = request != null ? request.getRequestURI() : null;
        ApiError body = ApiError.of(status.value(), status.getReasonPhrase(), message, path);
        return ResponseEntity.status(status).body(body);
    }

        @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            sb.append(fieldError.getDefaultMessage()).append(". ");
        }
        String message = sb.length() > 0 ? sb.toString().trim() : "Error de validación en los datos enviados.";
        String path = request != null ? request.getRequestURI() : "";
        log.warn("Validation error on {}: {}", path, message);
        return buildError(HttpStatus.BAD_REQUEST, message, request);
    }
}
