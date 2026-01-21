package com.example.crud_prueba.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
@NamedStoredProcedureQuery(
        name = "UsuarioEntity.registerLogin",
        procedureName = "register_login",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_user_id", type = Integer.class)
        }
)
@NamedStoredProcedureQuery(
        name = "UsuarioEntity.registerLogout",
        procedureName = "register_logout",
        parameters = {
                @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_user_id", type = Integer.class)
        }
)
public class UsuarioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(name = "persona_id_persona", nullable = false)
    private Long personaIdPersona;

    @Column(name = "username", nullable = false, unique = true, length = 20)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "mail", nullable = false, unique = true)
    private String mail;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "failed_attempts")
    private Integer failedAttempts;

    @Column(name = "session_active", length = 1)
    private String sessionActive;

    @Column(name = "deleted")
    private Boolean deleted;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "role", length = 20)
    private String role;


    public UsuarioEntity() {
        this.status = "ACTIVE";
        this.failedAttempts = 0;
        this.sessionActive = "N";
        this.deleted = false;
        this.fechaCreacion = LocalDateTime.now();
        this.role = "USER";
    }


    public UsuarioEntity(String username, String password, String mail, Long personaIdPersona) {
        this();
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.personaIdPersona = personaIdPersona;
    }

    @PrePersist
    protected void onCreate() {

        if (this.status == null) {
            this.status = "ACTIVE";
        }
        if (this.failedAttempts == null) {
            this.failedAttempts = 0;
        }
        if (this.sessionActive == null) {
            this.sessionActive = "N";
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.fechaCreacion == null) {
            this.fechaCreacion = LocalDateTime.now();
        }
        if (this.role == null) {
            this.role = "USER";
        }
        System.out.println("🔧 PrePersist - Valores establecidos:");
        System.out.println("   Role: " + this.role);
        System.out.println("   Status: " + this.status);
        System.out.println("   Fecha creación: " + this.fechaCreacion);
    }

    @PreUpdate
    protected void onUpdate() {

        if (this.role == null) {
            this.role = "USER";
            System.out.println("⚠️ Role establecido a USER en actualización");
        }
    }


    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public Long getPersonaIdPersona() { return personaIdPersona; }
    public void setPersonaIdPersona(Long personaIdPersona) {
        this.personaIdPersona = personaIdPersona;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getStatus() {
        return status != null ? status : "ACTIVE";
    }
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

    public Boolean getDeleted() {
        return deleted != null ? deleted : false;
    }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion != null ? fechaCreacion : LocalDateTime.now();
    }
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getRole() {
        return role != null ? role : "USER";
    }
    public void setRole(String role) {
        this.role = role;
        System.out.println("🔧 Role establecido a: " + this.role);
    }
}