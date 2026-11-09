package com.andres.course.claude.springboot.controllers;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
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
        // LinkedHashMap mantiene los campos del JSON en el orden en que se agregan
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "¡Hola Mundo desde Spring Boot! desde un Map. Cualquier cambio que haga se va a actualizar en caliente.");
        body.put("date", LocalDateTime.now());
        return body;
    }

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> handleGetUser() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "¡Hola Mundo desde Spring Boot! con los datos de un usuario.");
        body.put("date", LocalDateTime.now());
        body.put("name", "Juan");
        body.put("lastname", "Pérez");
        body.put("email", "juan.perez@ejemplo.com");
        return body;
    }

}
