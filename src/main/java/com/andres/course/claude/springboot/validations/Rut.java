package com.andres.course.claude.springboot.validations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

// Validación personalizada: el valor debe ser un RUT chileno válido (formato y dígito verificador).
// Se usa igual que @NotBlank o @Email: @Rut String rut
@Documented
@Constraint(validatedBy = RutValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.RECORD_COMPONENT})
@Retention(RetentionPolicy.RUNTIME)
public @interface Rut {

    // Mensaje de error que aparece en la respuesta 400
    String message() default "no es un RUT chileno válido";

    // groups y payload son obligatorios en toda anotación de validación
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
