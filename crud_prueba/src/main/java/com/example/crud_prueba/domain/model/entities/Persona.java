package com.example.crud_prueba.domain.model.entities;

import com.example.crud_prueba.domain.model.valueobjects.Identificacion;

import java.time.LocalDate;

public class Persona {
    private Long idPersona;
    private LocalDate fechaNacimiento;
    private String nombres;
    private String apellidos;
    private Identificacion identificacion;
    private boolean deleted;

    public Persona(LocalDate fechaNacimiento, String nombres, String apellidos, Identificacion identificacion) {
        this.fechaNacimiento = fechaNacimiento;
        this.nombres = nombres.trim();
        this.apellidos = apellidos.trim();
        this.identificacion = identificacion;
        this.deleted = false;
    }

    // Getters y Setters
    public Long getIdPersona() { return idPersona; }
    public void setIdPersona(Long idPersona) { this.idPersona = idPersona; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) {
        this.nombres = nombres.trim();
    }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos.trim();
    }

    public Identificacion getIdentificacion() { return identificacion; }
    public void setIdentificacion(Identificacion identificacion) {
        this.identificacion = identificacion;
    }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}
