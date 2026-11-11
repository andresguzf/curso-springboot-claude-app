package com.andres.course.claude.springboot.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// Lógica de @Rut: revisa el formato y calcula el dígito verificador con el algoritmo módulo 11
public class RutValidator implements ConstraintValidator<Rut, String> {

    // 7 u 8 dígitos (con o sin puntos de miles), guion opcional y dígito verificador 0-9 o K.
    // Acepta 12.345.678-5, 12345678-5 y 123456785
    private static final String RUT_FORMAT = "\\d{1,2}\\.?\\d{3}\\.?\\d{3}-?[\\dkK]";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        // null o vacío lo valida @NotBlank: así cada anotación tiene una sola responsabilidad
        if (value == null || value.isBlank()) {
            return true;
        }
        if (!value.matches(RUT_FORMAT)) {
            return false;
        }

        // Se quitan puntos y guion: 12.345.678-5 -> 123456785
        String clean = value.replace(".", "").replace("-", "").toUpperCase();
        String number = clean.substring(0, clean.length() - 1);
        char verifier = clean.charAt(clean.length() - 1);

        return calculateVerifier(number) == verifier;
    }

    // Módulo 11: cada dígito, de derecha a izquierda, se multiplica por 2, 3, 4, 5, 6, 7, 2, 3...
    // Se suman los productos y el dígito es 11 - (suma % 11). Si da 11 es '0' y si da 10 es 'K'
    private char calculateVerifier(String number) {
        int sum = 0;
        int multiplier = 2;
        for (int i = number.length() - 1; i >= 0; i--) {
            sum += Character.getNumericValue(number.charAt(i)) * multiplier;
            multiplier = multiplier == 7 ? 2 : multiplier + 1;
        }

        int result = 11 - (sum % 11);
        if (result == 11) {
            return '0';
        }
        if (result == 10) {
            return 'K';
        }
        return Character.forDigit(result, 10);
    }
}
