package com.example.crud_prueba.domain.model.valueobjects;

import java.util.regex.Pattern;

public class Username {
    private final String valor;

    public Username(String valor) {
        if (!esValido(valor)) {
            throw new IllegalArgumentException(
                    "Nombre de usuario inválido. Requisitos:\n" +
                            "• Longitud: 8-20 caracteres\n" +
                            "• Al menos un número\n" +
                            "• Al menos una letra mayúscula\n" +
                            "• Sin signos especiales"
            );
        }
        this.valor = valor;
    }

    private boolean esValido(String valor) {
        // Longitud entre 8 y 20 caracteres
        if (valor.length() < 8 || valor.length() > 20) {
            return false;
        }

        // No contener signos
        if (Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(valor).find()) {
            return false;
        }

        // Al menos un número
        if (!Pattern.compile("\\d").matcher(valor).find()) {
            return false;
        }

        // Al menos una letra mayúscula
        if (!Pattern.compile("[A-Z]").matcher(valor).find()) {
            return false;
        }

        return true;
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Username username = (Username) o;
        return valor.equals(username.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }

    @Override
    public String toString() {
        return valor;
    }
}