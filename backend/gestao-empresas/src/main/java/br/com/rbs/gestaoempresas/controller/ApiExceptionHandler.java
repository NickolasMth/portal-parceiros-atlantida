package br.com.rbs.gestaoempresas.controller;

import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    ResponseEntity<Map<String, String>> negado(AccessDeniedException e) { return erro(HttpStatus.FORBIDDEN, "Acesso negado"); }

    @ExceptionHandler(NoSuchElementException.class)
    ResponseEntity<Map<String, String>> naoEncontrado(NoSuchElementException e) { return erro(HttpStatus.NOT_FOUND, e.getMessage()); }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> invalido(IllegalArgumentException e) { return erro(HttpStatus.BAD_REQUEST, e.getMessage()); }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<Map<String, String>> conflito(IllegalStateException e) { return erro(HttpStatus.CONFLICT, e.getMessage()); }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String, String>> integridade(DataIntegrityViolationException e) {
        return erro(HttpStatus.CONFLICT, "Dados em conflito com um registro existente");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String, String>> validacao(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage()).findFirst().orElse("Dados inválidos");
        return erro(HttpStatus.BAD_REQUEST, msg);
    }

    private ResponseEntity<Map<String, String>> erro(HttpStatus s, String msg) {
        return ResponseEntity.status(s).body(Map.of("erro", msg == null ? s.getReasonPhrase() : msg));
    }
}
