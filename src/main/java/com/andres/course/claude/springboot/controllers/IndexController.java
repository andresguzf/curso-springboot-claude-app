package com.andres.course.claude.springboot.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import com.andres.course.claude.springboot.models.MessageDto;
import com.andres.course.claude.springboot.models.MessageHeadersDto;
import com.andres.course.claude.springboot.models.UserDto;

@RestController
@RequestMapping("/api/v1")
public class IndexController {

    @GetMapping("/greeting")
    public Map<String, Object> handleGet() {
        // LinkedHashMap mantiene los campos del JSON en el orden en que se agregan
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "¡Hola Mundo desde Spring Boot! desde un Map. Cualquier cambio que haga se va a actualizar en caliente.");
        body.put("date", LocalDateTime.now());
        return body;
    }

    @GetMapping(value = "/user", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> handleGetUser() {
        // Map anidado e inmutable: en el JSON queda como un objeto dentro del campo "user"

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "¡Hola Mundo desde Spring Boot! con los datos de un usuario.");
        body.put("date", LocalDateTime.now());
        body.put("user", Map.of(
                "name", "Juan",
                "lastname", "Pérez",
                "email", "juan.perez@ejemplo.com",
                "age", 30));
        return body;
    }

    @GetMapping(value = "/user-dto", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public MessageDto handleGetUserDto() {
        return new MessageDto(
                "¡Hola Mundo desde Spring Boot! con los datos de un usuario.",
                LocalDateTime.now(),
                new UserDto("Andrés", "Guzmán", "andres.guzman@example.com", 30));
    }

    @GetMapping("/user-response-entity")
    public ResponseEntity<MessageHeadersDto> handleGetUserResponseEntity(
            @RequestHeader(name = "X-UserName", required = true, defaultValue = "Anonymous") String username,
            @RequestHeader(name = HttpHeaders.AUTHORIZATION, required = true) String authorization) {
        MessageHeadersDto body = new MessageHeadersDto(
                "¡Hola Mundo desde Spring Boot! con los datos de un usuario.",
                LocalDateTime.now(),
                new UserDto("Andrés", "Guzmán", "andres.guzman@example.com", 30),
                username,
                authorization);

        // El status y el content type se definen en el código, no con anotaciones
        return ResponseEntity.status(HttpStatus.OK)
        // .contentType(MediaType.APPLICATION_XML)
        // .header("Content-Type", "application/json;charset=UTF-8")
        .header("X-Color", "#%06X".formatted(ThreadLocalRandom.current().nextInt(0x1000000)))
        // Token de ejemplo público de jwt.io, no es una credencial real
        .header("X-Token", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c")
        .body(body);
    }

    // Parámetros del query string: /api/v1/hello?name=Andrés&limit=5&page=2
    // Los tres son obligatorios. Si falta uno, no es número o no cumple la validación, responde 400
    @GetMapping("/hello")
    public Map<String, Object> handleGetHello(
            @RequestParam @NotBlank String name,
            @RequestParam @Min(5) @Max(50) Integer limit,
            @RequestParam @Min(0) Integer page) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "¡Hola, %s! Bienvenido a Spring Boot.".formatted(name));
        body.put("name", name);
        body.put("limit", limit);
        body.put("page", page);
        body.put("date", LocalDateTime.now());
        return body;
    }

}
