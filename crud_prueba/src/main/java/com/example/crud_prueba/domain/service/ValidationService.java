package com.example.crud_prueba.domain.service;

public class ValidationService {
    public boolean validateIdentification(String identificacion) {
        if (identificacion == null || identificacion.length() != 10) {
            return false;
        }

        if (!identificacion.matches("\\d{10}")) {
            return false;
        }

        for (int i = 0; i <= identificacion.length() - 4; i++) {
            String segment = identificacion.substring(i, i + 4);
            if (segment.chars().distinct().count() == 1) {
                return false;
            }
        }

        return true;
    }
}
