package com.example.crud_prueba.domain.service;

import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class EmailGeneratorService {

    public String generateEmail(String nombres, String apellidos, String identificacion) {
        String[] nombresArray = nombres.trim().split("\\s+");
        String[] apellidosArray = apellidos.trim().split("\\s+");

        // Primera letra del primer nombre + primer apellido completo
        String primeraLetraNombre = nombresArray[0].substring(0, 1).toLowerCase();
        String primerApellido = apellidosArray[0].toLowerCase();

        String baseEmail = primeraLetraNombre + primerApellido;

        // Limpiar caracteres especiales
        baseEmail = cleanForEmail(baseEmail);

        return baseEmail + "@mail.com";
    }

    private String cleanForEmail(String text) {
        return text.replaceAll("[^a-zA-Z0-9]", "");
    }

    public String generateUniqueEmail(String baseEmail, int existingCount) {
        String cleanEmail = baseEmail.replace("@mail.com", "");
        if (existingCount > 0) {
            return cleanEmail + existingCount + "@mail.com";
        }
        return baseEmail;
    }
}