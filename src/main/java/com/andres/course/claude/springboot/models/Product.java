package com.andres.course.claude.springboot.models;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// Producto que llega en el cuerpo (body) de un POST como JSON.
// El id no lo envía el cliente: el servidor genera un UUID al crear el producto.
// Validaciones (se aplican con @Valid en el controlador):
// - name: obligatorio, de 4 a 40 caracteres
// - price: obligatorio, mayor que 2, con hasta 10 dígitos enteros y 6 decimales (BigDecimal no pierde precisión)
// - quantity: obligatoria y mayor que 1
public record Product(
        UUID id,
        @NotBlank @Size(min = 4, max = 40) String name,
        @NotNull @DecimalMin(value = "2", inclusive = false) @Digits(integer = 10, fraction = 6) BigDecimal price,
        @NotNull @Min(2) Integer quantity) {

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
