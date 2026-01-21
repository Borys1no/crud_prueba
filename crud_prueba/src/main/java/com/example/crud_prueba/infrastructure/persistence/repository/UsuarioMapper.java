package com.example.crud_prueba.infrastructure.persistence.repository;

import com.example.crud_prueba.domain.model.entities.Usuario;
import com.example.crud_prueba.domain.model.valueobjects.Email;
import com.example.crud_prueba.domain.model.valueobjects.Password;
import com.example.crud_prueba.domain.model.valueobjects.Username;
import com.example.crud_prueba.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioEntity entity) {
        if (entity == null) return null;

        try {
            // Crear Value Objects
            Username username = new Username(entity.getUsername());
            Password password = new Password(entity.getPassword());
            Email email = new Email(entity.getMail());

            // Crear usuario
            Usuario usuario = new Usuario();
            usuario.setIdUsuario(entity.getIdUsuario());
            usuario.setPersonaIdPersona(entity.getPersonaIdPersona());
            usuario.setUsername(username);           // Ahora es Username, no String
            usuario.setPassword(password);           // Ahora es Password, no String
            usuario.setMail(email);                  // Ahora es Email, no String
            usuario.setStatus(entity.getStatus() != null ? entity.getStatus() : "ACTIVE");
            usuario.setFailedAttempts(entity.getFailedAttempts() != null ? entity.getFailedAttempts() : 0);
            usuario.setSessionActive(entity.getSessionActive() != null ? entity.getSessionActive() : "N");
            usuario.setDeleted(entity.getDeleted() != null ? entity.getDeleted() : false);
            usuario.setFechaCreacion(entity.getFechaCreacion());

            return usuario;

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Error creando Value Objects desde entidad: " + e.getMessage(), e);
        }
    }

    public UsuarioEntity toEntity(Usuario domain) {
        if (domain == null) return null;

        UsuarioEntity entity = new UsuarioEntity();
        entity.setIdUsuario(domain.getIdUsuario());
        entity.setPersonaIdPersona(domain.getPersonaIdPersona());

        // Extraer valores de los Value Objects
        if (domain.getUsername() != null) {
            entity.setUsername(domain.getUsername().getValor());  // Extraer String de Username
        }

        if (domain.getPassword() != null) {
            entity.setPassword(domain.getPassword().getValor());  // Extraer String de Password
        }

        if (domain.getMail() != null) {
            entity.setMail(domain.getMail().getValor());          // Extraer String de Email
        }

        entity.setStatus(domain.getStatus());
        entity.setFailedAttempts(domain.getFailedAttempts());
        entity.setSessionActive(domain.getSessionActive());
        entity.setDeleted(domain.isDeleted());                     // Cambiado de getDeleted() a isDeleted()
        entity.setFechaCreacion(domain.getFechaCreacion());

        return entity;
    }
}