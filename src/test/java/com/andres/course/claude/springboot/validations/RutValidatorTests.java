package com.andres.course.claude.springboot.validations;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

// Test unitario: prueba el algoritmo del RUT directamente, sin levantar Spring.
// Todos los RUT son ficticios, generados solo para probar el dígito verificador
class RutValidatorTests {

    private final RutValidator validator = new RutValidator();

    @ParameterizedTest
    @ValueSource(strings = {
            "12.345.678-5", "12345678-5", "123456785",  // mismo RUT en distintos formatos
            "11.111.111-1",
            "9.876.543-3",                               // 7 dígitos
            "15.000.000-9",
            "10.000.013-K", "10.000.013-k",              // dígito verificador K, en mayúscula o minúscula
            "23.000.006-9"
    })
    void validRutIsAccepted(String rut) {
        assertTrue(validator.isValid(rut, null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "12.345.678-9",   // dígito verificador incorrecto
            "10.000.013-0",   // debía ser K
            "12.345.678",     // sin dígito verificador
            "123.456.789-0",  // demasiados dígitos
            "123.456-7",      // muy pocos dígitos
            "12-345-678-5",   // guiones en vez de puntos
            "12.345.678-X",   // dígito verificador que no es número ni K
            "abc"
    })
    void invalidRutIsRejected(String rut) {
        assertFalse(validator.isValid(rut, null));
    }

    // null y vacío se consideran válidos aquí: los rechaza @NotBlank, no @Rut
    @ParameterizedTest
    @NullAndEmptySource
    void nullOrEmptyIsLeftToNotBlank(String rut) {
        assertTrue(validator.isValid(rut, null));
    }
}
