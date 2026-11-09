package com.andres.course.claude.springboot.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
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

    @GetMapping(value = "/user-dto", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Map<String, Object> handleGetUserDto() {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "¡Hola Mundo desde Spring Boot! con los datos de un usuario.");
        body.put("date", LocalDateTime.now());
        body.put("user", new UserDto("Juan", "Pérez", "juan.perez@ejemplo.com", 30));
        return body;
    }

}
