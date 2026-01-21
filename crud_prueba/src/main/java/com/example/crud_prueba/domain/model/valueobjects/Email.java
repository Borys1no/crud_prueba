package com.example.crud_prueba.domain.model.valueobjects;

import java.util.regex.Pattern;

public class Email {
    private final String valor;

    public Email(String valor) {
        if (!esValido(valor)) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.valor = valor;
    }

    private boolean esValido(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return false;
        }

        // Patrón básico para validar email
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);

        return pattern.matcher(valor).matches();
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return valor.equalsIgnoreCase(email.valor); // Email es case-insensitive
    }

    @Override
    public int hashCode() {
        return valor.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return valor;
    }
}