package com.andres.course.claude.springboot.models;

// DTO de respuesta: el mensaje y el producto completo, que en el JSON queda anidado en "product"
public record ProductResponseDto(String message, Product product) {
}
