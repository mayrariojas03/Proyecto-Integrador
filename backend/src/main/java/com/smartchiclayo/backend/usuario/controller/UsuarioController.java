package com.smartchiclayo.backend.usuario.controller;

import com.smartchiclayo.backend.usuario.entity.Usuario;
import com.smartchiclayo.backend.usuario.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private final UsuarioRepository usuarioRepository;

    public UsuarioController(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // REGISTRO DE USUARIO: Guarda físicamente en MySQL
    @PostMapping("/registro")
    public ResponseEntity<?> registrarUsuario(@RequestBody Usuario nuevoUsuario) {
        Map<String, Object> response = new HashMap<>();

        // Validar si el DNI ya existe
        Optional<Usuario> existenteDni = usuarioRepository.findAll().stream()
                .filter(u -> nuevoUsuario.getNumeroDocumento() != null && nuevoUsuario.getNumeroDocumento().equals(u.getNumeroDocumento()))
                .findFirst();
        if (existenteDni.isPresent()) {
            response.put("success", false);
            response.put("message", "El número de documento (DNI) ya se encuentra registrado.");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }

        // Valores por defecto
        if (nuevoUsuario.getTipoDocumento() == null) nuevoUsuario.setTipoDocumento("DNI");
        if (nuevoUsuario.getTipoUsuario() == null) nuevoUsuario.setTipoUsuario("PASAJERO");
        if (nuevoUsuario.getEstado() == null) nuevoUsuario.setEstado("ACTIVO");
        nuevoUsuario.setTelefonoVerificado(true);
        nuevoUsuario.setCorreoVerificado(true);

        // Si viene fecha de nacimiento, se preserva; si no, queda en null
        if (nuevoUsuario.getFechaNacimiento() != null && nuevoUsuario.getFechaNacimiento().isBlank()) {
            nuevoUsuario.setFechaNacimiento(null);
        }

        // Guardar en MySQL
        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        response.put("success", true);
        response.put("message", "Usuario registrado exitosamente en MySQL");
        response.put("usuario", usuarioGuardado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // LOGIN DE USUARIO
    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(@RequestBody Map<String, String> credenciales) {
        String identificador = credenciales.get("identificador"); // correo o teléfono
        String password = credenciales.get("password");

        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.findAll().stream()
                .filter(u -> (u.getCorreo() != null && u.getCorreo().equalsIgnoreCase(identificador)) ||
                        (u.getTelefono() != null && u.getTelefono().contains(identificador)) ||
                        (u.getNumeroDocumento() != null && u.getNumeroDocumento().equals(identificador)))
                .findFirst();

        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "No se encontró una cuenta con esos datos.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        Usuario usuario = usuarioOpt.get();
        if (usuario.getPasswordHash() != null && !usuario.getPasswordHash().equals(password)) {
            response.put("success", false);
            response.put("message", "Contraseña incorrecta.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        response.put("success", true);
        response.put("message", "Inicio de sesión exitoso");
        response.put("usuario", usuario);
        return ResponseEntity.ok(response);
    }

    // ACTUALIZAR PERFIL EN MYSQL: Se invoca al editar desde la app
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> campos) {
        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Usuario no encontrado.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Usuario usuario = usuarioOpt.get();

        if (campos.containsKey("telefono")) {
            usuario.setTelefono((String) campos.get("telefono"));
        }
        if (campos.containsKey("correo")) {
            usuario.setCorreo((String) campos.get("correo"));
        }
        if (campos.containsKey("numeroDocumento")) {
            usuario.setNumeroDocumento((String) campos.get("numeroDocumento"));
        }
        if (campos.containsKey("nombres")) {
            usuario.setNombres((String) campos.get("nombres"));
        }
        if (campos.containsKey("apellidos")) {
            usuario.setApellidos((String) campos.get("apellidos"));
        }

        if (campos.containsKey("fechaNacimiento")) {
            usuario.setFechaNacimiento((String) campos.get("fechaNacimiento"));
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);

        response.put("success", true);
        response.put("message", "Usuario actualizado exitosamente en MySQL");
        response.put("usuario", usuarioActualizado);
        return ResponseEntity.ok(response);
    }
}