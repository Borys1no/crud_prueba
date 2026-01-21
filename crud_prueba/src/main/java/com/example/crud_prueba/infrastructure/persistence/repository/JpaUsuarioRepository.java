package com.example.crud_prueba.infrastructure.persistence.repository;

import com.example.crud_prueba.infrastructure.persistence.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface JpaUsuarioRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByUsername(String username);
    Optional<UsuarioEntity> findByMail(String mail);

    @Query("SELECT u FROM UsuarioEntity u WHERE u.username = :identifier OR u.mail = :identifier")
    Optional<UsuarioEntity> findByUsernameOrEmail(@Param("identifier") String identifier);

    boolean existsByUsername(String username);
    boolean existsByMail(String mail);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END " +
            "FROM UsuarioEntity u WHERE u.personaIdPersona = :personaId AND u.deleted = false")
    boolean existsByPersonaId(@Param("personaId") Long personaId);

    @Procedure(name = "UsuarioEntity.registerLogin")
    void registerLogin(@Param("p_user_id") Integer userId);

    @Procedure(name = "UsuarioEntity.registerLogout")
    void registerLogout(@Param("p_user_id") Integer userId);
}