package com.andres.course.claude.springboot.models;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.Digits;

// Producto que llega en el cuerpo (body) de un POST como JSON.
// El id no lo envía el cliente: el servidor genera un UUID al crear el producto.
// price es BigDecimal para no perder precisión: hasta 10 dígitos enteros y 6 decimales
public record Product(
        UUID id,
        String name,
        @Digits(integer = 10, fraction = 6) BigDecimal price,
        Integer quantity) {

    // toString() con estructura JSON, para que el log muestre el producto como un JSON.
    // El id va entre comillas; si todavía no se asigna, queda como null (sin comillas)
    @Override
    public String toString() {
        String idJson = id == null ? "null" : "\"%s\"".formatted(id);
        return """
                {"id":%s,"name":"%s","price":%s,"quantity":%s}"""
                .formatted(idJson, name, price, quantity);
    }
}
