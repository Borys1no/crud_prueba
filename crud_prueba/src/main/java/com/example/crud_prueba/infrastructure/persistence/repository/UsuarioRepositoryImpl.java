package com.example.crud_prueba.infrastructure.persistence.repository;

import com.example.crud_prueba.domain.model.entities.Usuario;
import com.example.crud_prueba.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final JpaUsuarioRepository jpaUsuarioRepository;
    private final UsuarioMapper usuarioMapper;

    public UsuarioRepositoryImpl(JpaUsuarioRepository jpaUsuarioRepository,
                                 UsuarioMapper usuarioMapper) {
        this.jpaUsuarioRepository = jpaUsuarioRepository;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    public Usuario save(Usuario usuario) {
        // Convertir dominio a entidad JPA
        var entity = usuarioMapper.toEntity(usuario);
        // Guardar en BD
        var savedEntity = jpaUsuarioRepository.save(entity);
        // Convertir de vuelta a dominio
        return usuarioMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaUsuarioRepository.findById(id)
                .map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return jpaUsuarioRepository.findByUsername(username)
                .map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return jpaUsuarioRepository.findByMail(email)
                .map(usuarioMapper::toDomain);
    }

    @Override
    public Optional<Usuario> findByUsernameOrEmail(String usernameOrEmail) {
        return jpaUsuarioRepository.findByUsernameOrEmail(usernameOrEmail)
                .map(usuarioMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUsuarioRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUsuarioRepository.existsByMail(email);
    }

    @Override
    public boolean existsByPersonaId(Long personaId) {
        return jpaUsuarioRepository.existsByPersonaId(personaId);
    }

    @Override
    public void update(Usuario usuario) {
        // Para update, simplemente guardamos
        // JPA detectará si es insert o update basado en el ID
        save(usuario);
    }
}