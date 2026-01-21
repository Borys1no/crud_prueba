package com.example.crud_prueba.application.usecases;

import com.example.crud_prueba.domain.model.entities.Usuario;
import com.example.crud_prueba.domain.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginUseCase {
    private final UsuarioRepository usuarioRepository;

    public LoginUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Usuario execute(String usernameOrEmail, String password) {
        // Buscar usuario por username o email
        Usuario usuario = usuarioRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));


        if (usuario.isBlocked()) {
            throw new RuntimeException("Usuario bloqueado por múltiples intentos fallidos");
        }


        if (usuario.hasActiveSession()) {
            throw new RuntimeException("El usuario ya tiene una sesión activa");
        }


        if (!usuario.getPassword().verificar(password)) {
            usuario.incrementFailedAttempts();
            usuarioRepository.update(usuario);

            if (usuario.isBlocked()) {
                throw new RuntimeException("Usuario bloqueado después de 3 intentos fallidos");
            }

            throw new RuntimeException(
                    String.format("Contraseña incorrecta. Intentos restantes: %d",
                            3 - usuario.getFailedAttempts())
            );
        }


        usuario.resetFailedAttempts();
        usuario.activateSession();
        usuarioRepository.update(usuario);

        return usuario;
    }
}