package com.example.crud_prueba.application.usecases;

import com.example.crud_prueba.domain.model.entities.Persona;
import com.example.crud_prueba.domain.model.entities.Usuario;
import com.example.crud_prueba.domain.model.valueobjects.Email;
import com.example.crud_prueba.domain.model.valueobjects.Password;
import com.example.crud_prueba.domain.model.valueobjects.Username;
import com.example.crud_prueba.domain.repository.UsuarioRepository;
import com.example.crud_prueba.domain.service.EmailGeneratorService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegisterUseCase {
    private final UsuarioRepository usuarioRepository;
    private final EmailGeneratorService emailGeneratorService;

    public RegisterUseCase(UsuarioRepository usuarioRepository,
                           EmailGeneratorService emailGeneratorService) {
        this.usuarioRepository = usuarioRepository;
        this.emailGeneratorService = emailGeneratorService;
    }

    @Transactional
    public Usuario execute(Persona persona, String usernameStr, String passwordStr) {
        // Validar que la persona no tenga ya un usuario
        if (persona.getIdPersona() != null && usuarioRepository.existsByPersonaId(persona.getIdPersona())) {
            throw new RuntimeException("La persona ya tiene una cuenta registrada");
        }

        // Crear objetos de valor
        Username usernameObj = new Username(usernameStr);
        Password passwordObj = new Password(passwordStr);

        // Verificar que el username no exista
        if (usuarioRepository.existsByUsername(usernameObj.getValor())) {
            throw new RuntimeException("El nombre de usuario ya existe");
        }

        // Generar email único
        String baseEmail = emailGeneratorService.generateEmail(
                persona.getNombres(),
                persona.getApellidos(),
                persona.getIdentificacion().getValor()
        );

        // Buscar email único
        String uniqueEmail = generateUniqueEmail(baseEmail);
        Email emailObj = new Email(uniqueEmail);

        // Verificar que el email no exista
        if (usuarioRepository.existsByEmail(emailObj.getValor())) {
            throw new RuntimeException("El email ya existe");
        }

        // Crear usuario
        Usuario usuario = new Usuario(
                persona.getIdPersona(),
                usernameObj,
                passwordObj,
                emailObj
        );

        return usuarioRepository.save(usuario);
    }

    private String generateUniqueEmail(String baseEmail) {
        int counter = 0;
        String emailToTry = baseEmail;

        while (usuarioRepository.existsByEmail(emailToTry)) {
            counter++;
            emailToTry = emailGeneratorService.generateUniqueEmail(baseEmail, counter);
        }

        return emailToTry;
    }
}