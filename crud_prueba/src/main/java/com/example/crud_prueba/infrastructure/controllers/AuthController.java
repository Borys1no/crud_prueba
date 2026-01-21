package com.example.crud_prueba.infrastructure.controllers;

import com.example.crud_prueba.application.dto.LoginRequest;
import com.example.crud_prueba.application.dto.RegisterRequest;
import com.example.crud_prueba.domain.repository.UsuarioRepository;
import com.example.crud_prueba.infrastructure.persistence.entity.UsuarioEntity;
import com.example.crud_prueba.infrastructure.persistence.repository.JpaUsuarioRepository;
import com.example.crud_prueba.infrastructure.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioRepository usuarioRepository;
    private final JpaUsuarioRepository jpaUsuarioRepository;

    public AuthController(JwtTokenProvider jwtTokenProvider,
                          PasswordEncoder passwordEncoder,
                          UsuarioRepository usuarioRepository,
                          JpaUsuarioRepository jpaUsuarioRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.usuarioRepository = usuarioRepository;
        this.jpaUsuarioRepository = jpaUsuarioRepository;


        crearUsuarioAdminSiNoExiste();
    }

    private void crearUsuarioAdminSiNoExiste() {
        if (!jpaUsuarioRepository.existsByUsername("admin")) {

            Long maxPersonaId = jpaUsuarioRepository.findAll().stream()
                    .map(UsuarioEntity::getPersonaIdPersona)
                    .max(Long::compare)
                    .orElse(0L);

            UsuarioEntity admin = new UsuarioEntity();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("Admin123!"));
            admin.setMail("admin@mail.com");
            admin.setStatus("ACTIVE");
            admin.setFailedAttempts(0);
            admin.setSessionActive("N");
            admin.setDeleted(false);
            admin.setPersonaIdPersona(maxPersonaId + 1);

            jpaUsuarioRepository.save(admin);
            System.out.println("✅ Usuario admin creado con persona_id: " + admin.getPersonaIdPersona());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("\n=== INTENTO DE LOGIN ===");
            System.out.println("Usuario/Email: " + request.getUsernameOrEmail());


            Optional<UsuarioEntity> usuarioOpt = jpaUsuarioRepository
                    .findByUsernameOrEmail(request.getUsernameOrEmail());

            if (usuarioOpt.isEmpty()) {
                System.out.println("USUARIO NO ENCONTRADO EN POSTGRESQL");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Usuario no encontrado"));
            }

            UsuarioEntity usuario = usuarioOpt.get();
            System.out.println("✅ Usuario encontrado en PostgreSQL: " + usuario.getUsername());
            System.out.println("Role: " + usuario.getRole()); // <-- Añade este log
            System.out.println("Status: " + usuario.getStatus());
            System.out.println("Intentos fallidos: " + usuario.getFailedAttempts());


            if ("BLOCKED".equals(usuario.getStatus())) {
                System.out.println("❌ USUARIO BLOQUEADO");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Usuario bloqueado"));
            }


            boolean passwordMatch = passwordEncoder.matches(
                    request.getPassword(),
                    usuario.getPassword()
            );

            System.out.println("Password match: " + passwordMatch);

            if (!passwordMatch) {
                System.out.println("PASSWORD INCORRECTA");

                usuario.setFailedAttempts(usuario.getFailedAttempts() + 1);

                if (usuario.getFailedAttempts() >= 3) {
                    usuario.setStatus("BLOCKED");
                    System.out.println("USUARIO BLOQUEADO POR 3 INTENTOS FALLIDOS");
                }


                jpaUsuarioRepository.save(usuario);

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse(
                                String.format("Contraseña incorrecta. Intentos restantes: %d",
                                        3 - usuario.getFailedAttempts())));
            }

            System.out.println("✅ PASSWORD CORRECTA");


            usuario.setFailedAttempts(0);
            usuario.setSessionActive("Y");


            jpaUsuarioRepository.save(usuario);


            String token = jwtTokenProvider.generateToken(
                    usuario.getIdUsuario(),
                    usuario.getUsername(),
                    usuario.getMail(),
                    usuario.getRole()
            );


            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("userId", usuario.getIdUsuario());
            response.put("username", usuario.getUsername());
            response.put("email", usuario.getMail());
            response.put("role", usuario.getRole());
            response.put("message", "Login exitoso");

            System.out.println("✅ LOGIN EXITOSO");
            System.out.println("Role enviado al frontend: " + usuario.getRole());
            System.out.println("================\n");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("❌ ERROR EN LOGIN: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error en el servidor: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            System.out.println("\n=== INTENTO DE REGISTRO ===");
            System.out.println("Username: " + request.getUsername());
            System.out.println("Nombres: " + request.getNombres());
            System.out.println("Apellidos: " + request.getApellidos());


            if (request.getNombres() == null || request.getNombres().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Nombres son requeridos"));
            }

            if (request.getApellidos() == null || request.getApellidos().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Apellidos son requeridos"));
            }

            if (request.getIdentificacion() == null || !request.getIdentificacion().matches("\\d{10}")) {
                return ResponseEntity.badRequest().body(createErrorResponse("Identificación debe tener 10 dígitos"));
            }

            if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Username es requerido"));
            }


            if (!isValidUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                        "Username inválido. Debe tener: 8-20 caracteres, al menos un número, " +
                                "al menos una mayúscula, sin signos"));
            }

            if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("Password es requerido"));
            }


            if (!isValidPassword(request.getPassword())) {
                return ResponseEntity.badRequest().body(createErrorResponse(
                        "Password inválido. Debe tener: al menos 8 caracteres, " +
                                "al menos una mayúscula, sin espacios, al menos un signo"));
            }

            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest().body(createErrorResponse("Las contraseñas no coinciden"));
            }


            if (jpaUsuarioRepository.existsByUsername(request.getUsername())) {
                return ResponseEntity.badRequest().body(createErrorResponse("El username ya está en uso"));
            }


            String email = generateEmail(request.getNombres(), request.getApellidos(), request.getIdentificacion());


            int counter = 1;
            String originalEmail = email;
            while (jpaUsuarioRepository.existsByMail(email)) {
                email = originalEmail.replace("@mail.com", counter + "@mail.com");
                counter++;
            }

            System.out.println("✅ Email generado: " + email);


            UsuarioEntity nuevoUsuario = new UsuarioEntity();
            nuevoUsuario.setUsername(request.getUsername());
            nuevoUsuario.setPassword(passwordEncoder.encode(request.getPassword()));
            nuevoUsuario.setMail(email);
            nuevoUsuario.setStatus("ACTIVE");
            nuevoUsuario.setFailedAttempts(0);
            nuevoUsuario.setSessionActive("N");
            nuevoUsuario.setDeleted(false);
            nuevoUsuario.setPersonaIdPersona(1L);

            System.out.println("Guardando usuario en PostgreSQL...");


            UsuarioEntity usuarioGuardado = jpaUsuarioRepository.save(nuevoUsuario);

            System.out.println("Usuario guardado con ID: " + usuarioGuardado.getIdUsuario());


            String token = jwtTokenProvider.generateToken(
                    usuarioGuardado.getIdUsuario(),
                    usuarioGuardado.getUsername(),
                    usuarioGuardado.getMail()
            );


            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("token", token);
            response.put("userId", usuarioGuardado.getIdUsuario());
            response.put("username", usuarioGuardado.getUsername());
            response.put("email", usuarioGuardado.getMail());
            response.put("generatedEmail", usuarioGuardado.getMail());
            response.put("message", "Usuario registrado exitosamente en PostgreSQL");

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            System.out.println("ERROR EN REGISTRO: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error en el servidor: " + e.getMessage()));
        }
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Missing or invalid Authorization header"));
            }

            String token = authHeader.substring(7);

            if (!jwtTokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("Invalid or expired token"));
            }


            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            String username = jwtTokenProvider.getUsernameFromToken(token);
            String email = jwtTokenProvider.getEmailFromToken(token);


            Optional<UsuarioEntity> usuarioOpt = jpaUsuarioRepository.findById(userId);
            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("User no longer exists"));
            }

            UsuarioEntity usuario = usuarioOpt.get();


            if ("BLOCKED".equals(usuario.getStatus())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(createErrorResponse("User is blocked"));
            }


            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("valid", true);
            response.put("userId", userId);
            response.put("username", username);
            response.put("email", email);
            response.put("userStatus", usuario.getStatus());
            response.put("sessionActive", usuario.getSessionActive());
            response.put("message", "Token is valid");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error validating token: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(createErrorResponse("Token validation failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtTokenProvider.validateToken(token)) {
                    Long userId = jwtTokenProvider.getUserIdFromToken(token);


                    Optional<UsuarioEntity> usuarioOpt = jpaUsuarioRepository.findById(userId);
                    if (usuarioOpt.isPresent()) {
                        UsuarioEntity usuario = usuarioOpt.get();
                        usuario.setSessionActive("N");
                        jpaUsuarioRepository.save(usuario);
                        System.out.println("✅ Sesión cerrada para usuario ID: " + userId);
                    }
                }
            }


            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
                SecurityContextHolder.clearContext();
                System.out.println("✅ SecurityContext cleared");
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logged out successfully");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error during logout: " + e.getMessage()));
        }
    }


    @GetMapping("/test")
    public ResponseEntity<?> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Auth controller is working");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }



    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }

    private boolean isValidUsername(String username) {
        if (username.length() < 8 || username.length() > 20) return false;
        if (username.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        if (!username.matches(".*\\d.*")) return false;
        if (!username.matches(".*[A-Z].*")) return false;
        return true;
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8) return false;
        if (!password.matches(".*[A-Z].*")) return false;
        if (password.contains(" ")) return false;
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) return false;
        return true;
    }

    private String generateEmail(String nombres, String apellidos, String identificacion) {
        String primeraLetraNombre = nombres.trim().split("\\s+")[0].substring(0, 1).toLowerCase();
        String primerApellido = apellidos.trim().split("\\s+")[0].toLowerCase();
        String base = (primeraLetraNombre + primerApellido).replaceAll("[^a-z]", "");
        return base + "@mail.com";
    }


}