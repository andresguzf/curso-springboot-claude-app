package com.andres.course.claude.springboot.exceptions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

// Manejo de errores para todos los controladores, separado de ellos.
// Responde 400 solo con el campo o parámetro y su mensaje, sin la excepción
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Falla una validación: @NotBlank, @Size, @Min, @Max...
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(HandlerMethodValidationException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getParameterValidationResults().forEach(result -> {
            String field = result.getMethodParameter().getParameterName();
            // Si un mismo campo falla varias validaciones, se juntan sus mensajes
            result.getResolvableErrors().forEach(error ->
                    errors.merge(field, error.getDefaultMessage(), (a, b) -> a + ", " + b));
        });
        return badRequest(errors);
    }

    // Falla una validación de un objeto que llega en el cuerpo con @Valid @RequestBody,
    // por ejemplo @Digits en el price de Product
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBodyValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.merge(error.getField(), error.getDefaultMessage(), (a, b) -> a + ", " + b));
        return badRequest(errors);
    }

    // Falta un parámetro obligatorio del query string
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParameter(MissingServletRequestParameterException ex) {
        return badRequest(Map.of(ex.getParameterName(), "es obligatorio"));
    }

    // Falta una cabecera obligatoria del request
    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> handleMissingHeader(MissingRequestHeaderException ex) {
        return badRequest(Map.of(ex.getHeaderName(), "la cabecera es obligatoria"));
    }

    // El valor no se puede convertir al tipo del parámetro, por ejemplo limit=abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String type = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "válido";
        return badRequest(Map.of(ex.getName(), "debe ser de tipo " + type));
    }

    private ResponseEntity<Map<String, Object>> badRequest(Map<String, String> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.put("errors", errors);
        return ResponseEntity.badRequest().body(body);
    }
}
