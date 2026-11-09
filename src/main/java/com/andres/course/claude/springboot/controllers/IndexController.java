package com.andres.course.claude.springboot.controllers;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class IndexController {

    @GetMapping("/greeting")
    public Map<String, Object> handleGet() {
        return Map.of(
                "message",
                "¡Hola Mundo desde Spring Boot! desde un Map. Cualquier cambio que haga se va a actualizar en caliente.",
                "date", LocalDateTime.now());
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> handleGetUser() {
        return Map.of(
                "message", "¡Hola Mundo desde Spring Boot! con los datos de un usuario.",
                "date", LocalDateTime.now(),
                "name", "Juan",
                "lastname", "Pérez",
                "email", "juan.perez@ejemplo.com");
    }

}
