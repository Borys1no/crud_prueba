package com.example.crud_prueba.infrastructure.persistence.entity;


import com.example.crud_prueba.infrastructure.persistence.repository.JpaUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")

@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private JpaUsuarioRepository usuarioRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<UsuarioEntity> usuarios = usuarioRepository.findAll();

            List<Map<String, Object>> usersDTO = usuarios.stream()
                    .map(user -> {
                        Map<String, Object> dto = new HashMap<>();
                        dto.put("id", user.getIdUsuario());
                        dto.put("username", user.getUsername());
                        dto.put("email", user.getMail());
                        dto.put("role", user.getRole());
                        dto.put("status", user.getStatus());
                        dto.put("failedAttempts", user.getFailedAttempts());
                        dto.put("sessionActive", user.getSessionActive());
                        dto.put("fechaCreacion", user.getFechaCreacion());
                        dto.put("personaIdPersona", user.getPersonaIdPersona());
                        return dto;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("users", usersDTO);
            response.put("count", usersDTO.size());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error obteniendo usuarios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener usuarios: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(id);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Usuario no encontrado"));
            }

            UsuarioEntity user = usuarioOpt.get();

            Map<String, Object> userDTO = new HashMap<>();
            userDTO.put("id", user.getIdUsuario());
            userDTO.put("username", user.getUsername());
            userDTO.put("email", user.getMail());
            userDTO.put("role", user.getRole());
            userDTO.put("status", user.getStatus());
            userDTO.put("failedAttempts", user.getFailedAttempts());
            userDTO.put("sessionActive", user.getSessionActive());
            userDTO.put("fechaCreacion", user.getFechaCreacion());
            userDTO.put("personaIdPersona", user.getPersonaIdPersona());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("user", userDTO);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error obteniendo usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener usuario: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        try {
            Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(id);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Usuario no encontrado"));
            }

            UsuarioEntity user = usuarioOpt.get();

            if (updates.containsKey("username")) {
                user.setUsername((String) updates.get("username"));
            }

            if (updates.containsKey("email")) {
                user.setMail((String) updates.get("email"));
            }

            if (updates.containsKey("role")) {
                user.setRole((String) updates.get("role"));
            }

            if (updates.containsKey("status")) {
                user.setStatus((String) updates.get("status"));
            }

            if (updates.containsKey("failedAttempts")) {
                user.setFailedAttempts((Integer) updates.get("failedAttempts"));
            }

            if (updates.containsKey("sessionActive")) {
                user.setSessionActive((String) updates.get("sessionActive"));
            }

            usuarioRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario actualizado correctamente");
            response.put("userId", user.getIdUsuario());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error actualizando usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar usuario: " + e.getMessage()));
        }
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String newStatus = request.get("status");

            if (newStatus == null || newStatus.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(createErrorResponse("El status es requerido"));
            }

            Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(id);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Usuario no encontrado"));
            }

            UsuarioEntity user = usuarioOpt.get();
            user.setStatus(newStatus);

            if ("BLOCKED".equals(newStatus)) {
                user.setFailedAttempts(0);
            }

            usuarioRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Status actualizado correctamente");
            response.put("userId", user.getIdUsuario());
            response.put("newStatus", newStatus);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error actualizando status: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al actualizar status: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            Optional<UsuarioEntity> usuarioOpt = usuarioRepository.findById(id);

            if (usuarioOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Usuario no encontrado"));
            }

            UsuarioEntity user = usuarioOpt.get();
            user.setDeleted(true);
            user.setStatus("INACTIVE");

            usuarioRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Usuario marcado como eliminado");
            response.put("userId", user.getIdUsuario());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error eliminando usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al eliminar usuario: " + e.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Implementar autenticación JWT para obtener usuario actual");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error obteniendo usuario actual: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener usuario actual: " + e.getMessage()));
        }
    }


    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserStats() {
        try {
            List<UsuarioEntity> allUsers = usuarioRepository.findAll();

            long totalUsers = allUsers.size();
            long activeUsers = allUsers.stream().filter(u -> "ACTIVE".equals(u.getStatus())).count();
            long blockedUsers = allUsers.stream().filter(u -> "BLOCKED".equals(u.getStatus())).count();
            long inactiveUsers = allUsers.stream().filter(u -> "INACTIVE".equals(u.getStatus())).count();
            long totalFailedAttempts = allUsers.stream().mapToInt(UsuarioEntity::getFailedAttempts).sum();
            long activeSessions = allUsers.stream().filter(u -> "Y".equals(u.getSessionActive())).count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalUsers", totalUsers);
            stats.put("activeUsers", activeUsers);
            stats.put("blockedUsers", blockedUsers);
            stats.put("inactiveUsers", inactiveUsers);
            stats.put("totalFailedAttempts", totalFailedAttempts);
            stats.put("activeSessions", activeSessions);
            stats.put("admins", allUsers.stream().filter(u -> "ADMIN".equals(u.getRole())).count());
            stats.put("regularUsers", allUsers.stream().filter(u -> "USER".equals(u.getRole())).count());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("stats", stats);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.err.println("Error obteniendo estadísticas: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al obtener estadísticas: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }
}
