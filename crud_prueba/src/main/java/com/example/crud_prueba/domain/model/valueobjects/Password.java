package com.example.crud_prueba.domain.model.valueobjects;

import java.util.regex.Pattern;

public class Password {
    private final String valor;

    public Password(String valor) {
        if (!esValida(valor)) {
            throw new IllegalArgumentException("Contraseña inválida. Debe tener: al menos 8 caracteres, una mayúscula, sin espacios, al menos un signo (!@#$%^&* etc.)");
        }
        this.valor = valor;
    }

    private boolean esValida(String valor) {
        // Al menos 8 dígitos
        if (valor.length() < 8) {
            return false;
        }

        // Al menos una letra mayúscula
        if (!Pattern.compile("[A-Z]").matcher(valor).find()) {
            return false;
        }

        // No debe contener espacios
        if (valor.contains(" ")) {
            return false;
        }

        // Debe tener al menos un signo
        if (!Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(valor).find()) {
            return false;
        }

        return true;
    }

    public boolean verificar(String passwordPlain) {
        // En producción usar BCrypt
        return this.valor.equals(passwordPlain);
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Password password = (Password) o;
        return valor.equals(password.valor);
    }

    @Override
    public int hashCode() {
        return valor.hashCode();
    }
}