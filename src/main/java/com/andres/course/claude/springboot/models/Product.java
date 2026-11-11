package com.andres.course.claude.springboot.models;

import java.util.UUID;

// Producto que llega en el cuerpo (body) de un POST como JSON.
// El id no lo envía el cliente: el servidor genera un UUID al crear el producto
public record Product(UUID id, String name, Double price, Integer quantity) {
}
