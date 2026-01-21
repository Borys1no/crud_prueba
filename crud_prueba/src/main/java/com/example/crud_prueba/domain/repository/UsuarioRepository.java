package com.example.crud_prueba.domain.repository;

import com.example.crud_prueba.domain.model.entities.Usuario;
import java.util.Optional;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);
    Optional<Usuario> findById(Long id);
    Optional<Usuario> findByUsername(String username); // Acepta String
    Optional<Usuario> findByEmail(String email); // Acepta String
    Optional<Usuario> findByUsernameOrEmail(String usernameOrEmail);
    boolean existsByUsername(String username); // Acepta String
    boolean existsByEmail(String email); // Acepta String
    boolean existsByPersonaId(Long personaId);
    void update(Usuario usuario);
}