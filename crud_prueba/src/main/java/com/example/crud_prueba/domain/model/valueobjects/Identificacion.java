package com.example.crud_prueba.domain.model.valueobjects;

import com.example.crud_prueba.domain.exceptions.DomainException;

import java.util.regex.Pattern;

public class Identificacion {
    private final String valor;

    public Identificacion(String valor) {
        if (!esValido(valor)) {
            throw new DomainException("Identificación inválida");
        }
        this.valor = valor;
    }

    private boolean esValido(String valor) {

        if (!Pattern.matches("^\\d{10}$", valor)) {
            return false;
        }


        for (int i = 0; i <= valor.length() - 4; i++) {
            String substring = valor.substring(i, i + 4);
            if (substring.chars().distinct().count() == 1) {
                return false;
            }
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
        Identificacion that = (Identificacion) o;
        return valor.equals(that.valor);
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
