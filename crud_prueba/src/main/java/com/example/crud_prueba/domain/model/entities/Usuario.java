package com.example.crud_prueba.domain.model.entities;

import com.example.crud_prueba.domain.model.valueobjects.Username;
import com.example.crud_prueba.domain.model.valueobjects.Password;
import com.example.crud_prueba.domain.model.valueobjects.Email;
import java.time.LocalDateTime;

public class Usuario {
    private Long idUsuario;
    private Long personaIdPersona;
    private Username username;
    private Password password;
    private Email mail;
    private String status;
    private Integer failedAttempts;
    private String sessionActive;
    private boolean deleted;
    private LocalDateTime fechaCreacion;

    // Constructor sin parámetros (necesario para JPA/reflection)
    public Usuario() {
        // Constructor vacío para frameworks
    }

    public Usuario(Long personaIdPersona, Username username, Password password, Email mail) {
        this.personaIdPersona = personaIdPersona;
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.status = "ACTIVE";
        this.failedAttempts = 0;
        this.sessionActive = "N";
        this.deleted = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    public void incrementFailedAttempts() {
        if (this.failedAttempts == null) {
            this.failedAttempts = 0;
        }
        this.failedAttempts++;
        if (this.failedAttempts >= 3) {
            this.status = "BLOCKED";
        }
    }

    public void resetFailedAttempts() {
        this.failedAttempts = 0;
    }

    public void activateSession() {
        this.sessionActive = "Y";
    }

    public void deactivateSession() {
        this.sessionActive = "N";
    }

    public boolean isBlocked() {
        return "BLOCKED".equals(this.status);
    }

    public boolean hasActiveSession() {
        return "Y".equals(this.sessionActive);
    }

    // Getters y Setters
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getPersonaIdPersona() { return personaIdPersona; }
    public void setPersonaIdPersona(Long personaIdPersona) {
        this.personaIdPersona = personaIdPersona;
    }

    public Username getUsername() { return username; }
    public void setUsername(Username username) { this.username = username; }

    public Password getPassword() { return password; }
    public void setPassword(Password password) { this.password = password; }

    public Email getMail() { return mail; }
    public void setMail(Email mail) { this.mail = mail; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getFailedAttempts() {
        return failedAttempts != null ? failedAttempts : 0;
    }

    public void setFailedAttempts(Integer failedAttempts) {
        this.failedAttempts = failedAttempts;
    }

    public String getSessionActive() {
        return sessionActive != null ? sessionActive : "N";
    }

    public void setSessionActive(String sessionActive) {
        this.sessionActive = sessionActive;
    }

    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion != null ? fechaCreacion : LocalDateTime.now();
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}