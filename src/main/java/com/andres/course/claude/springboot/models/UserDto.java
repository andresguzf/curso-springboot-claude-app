package com.andres.course.claude.springboot.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.andres.course.claude.springboot.validations.Rut;

// Validaciones (se aplican cuando llega con @Valid @RequestBody):
// - name y lastname: obligatorios, de 3 a 20 caracteres
// - email: obligatorio y con formato de correo válido
// - age: obligatoria, entre 1 y 110. Es Integer (no int) para que pueda llegar null y @NotNull lo detecte
// - rut: obligatorio y válido según el dígito verificador (@Rut es una validación personalizada)
public record UserDto(
        @NotBlank @Size(min = 3, max = 20) String name,
        @NotBlank @Size(min = 3, max = 20) String lastname,
        @NotBlank @Email String email,
        @NotNull @Min(1) @Max(110) Integer age,
        @NotBlank @Rut String rut) {

    @Override
    public String toString() {
        return """
                {"name":"%s","lastname":"%s","email":"%s","age":%d,"rut":"%s"}"""
                .formatted(name, lastname, email, age, rut);
    }
}
