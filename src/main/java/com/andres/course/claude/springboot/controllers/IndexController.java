package com.andres.course.claude.springboot.controllers;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.andres.course.claude.springboot.models.MessageDto;
import com.andres.course.claude.springboot.models.MessageHeadersDto;
import com.andres.course.claude.springboot.models.Product;
import com.andres.course.claude.springboot.models.ProductResponseDto;
import com.andres.course.claude.springboot.models.UserDto;

@RestController
@RequestMapping("/api")
public class IndexController {

    // Logger de la clase: los mensajes salen en la consola junto al nombre del controlador
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);

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

    // Parámetros del query string: /api/hello?name=Andrés&limit=5&page=2
    // Los tres son obligatorios. Si falta uno, no es número o no cumple la validación, responde 400
    @GetMapping("/hello")
    public Map<String, Object> handleGetHello(
            @RequestParam @NotBlank @Size(min = 4, max = 14) String name,
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

    // Path variable: el id viene en la ruta, por ejemplo /api/products/7
    @GetMapping("/products/{id}")
    public Map<String, Object> findById(@PathVariable @Min(1) @Max(45) Long id) {
        return Map.of("productId", id);
    }

    // Parte estática "code" + parte variable {code}, por ejemplo /api/code/ABC123
    @GetMapping("/code/{code}")
    public Map<String, Object> findByCode(@PathVariable @Size(min = 3, max = 10) String code) {
        return Map.of("productCode", code);
    }

    // Dos path variables en la misma ruta, por ejemplo /api/hello/Andrés/30
    // name: de 3 a 18 caracteres, sin ser solo espacios. age: entre 1 y 110
    @GetMapping("/hello/{name}/{age}")
    public Map<String, Object> handleGetHelloPath(
            @PathVariable @NotBlank @Size(min = 3, max = 18) String name,
            @PathVariable @Min(1) @Max(110) Integer age) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "¡Hola, %s! Tienes %d años.".formatted(name, age));
        body.put("name", name);
        body.put("age", age);
        return body;
    }

    // POST: el producto llega como JSON en el cuerpo del request y Spring lo convierte a Product
    // Responde con ResponseEntity: el status 201 se define en el código y el cuerpo es un DTO
    // @Valid aplica las validaciones del record Product, como @Digits en price
    @PostMapping("/products")
    public ResponseEntity<ProductResponseDto> create(@Valid @RequestBody Product product) {
        // {} se reemplaza por el toString() de Product, que devuelve el producto en formato JSON
        log.info("Producto recibido: {}", product);

        // El id es un UUID aleatorio generado en el servidor; se ignora el que pudiera enviar el cliente
        Product created = new Product(UUID.randomUUID(), product.name(), product.price(), product.quantity());
        ProductResponseDto body = new ProductResponseDto("Producto creado con éxito", created);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    // POST: el usuario llega como JSON en el cuerpo y @Valid aplica las validaciones de UserDto.
    // La respuesta reutiliza MessageDto: mensaje, fecha y el usuario creado
    @PostMapping("/users")
    public ResponseEntity<MessageDto> createUser(@Valid @RequestBody UserDto user) {
        log.info("Usuario recibido: {}", user);
        MessageDto body = new MessageDto("Usuario creado con éxito", LocalDateTime.now(), user);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

}
