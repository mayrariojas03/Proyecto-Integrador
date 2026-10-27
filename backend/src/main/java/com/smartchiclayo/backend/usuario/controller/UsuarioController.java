package com.smartchiclayo.backend.usuario.controller;

import com.smartchiclayo.backend.usuario.entity.Usuario;
import com.smartchiclayo.backend.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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

    @Autowired(required = false)
    private JavaMailSender mailSender;

    // Almacena temporalmente los códigos generados: correo -> codigo
    private static final Map<String, String> codigosVerificacion = new HashMap<>();

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
        if (password == null || usuario.getPasswordHash() == null || usuario.getPasswordHash().isBlank()
                || !usuario.getPasswordHash().equals(password)) {
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
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos,
            @AuthenticationPrincipal Jwt jwt
    ) {
        Map<String, Object> response = new HashMap<>();

        // El subject del JWT es el id del usuario autenticado en MySQL.
        if (jwt == null || !id.toString().equals(jwt.getSubject())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                    "success", false,
                    "message", "No tienes permiso para modificar este perfil."
            ));
        }

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
        // Nunca devolver la entidad completa: contiene passwordHash y googleSub.
        response.put("usuario", Map.of(
                "idUsuario", usuarioActualizado.getIdUsuario(),
                "nombres", usuarioActualizado.getNombres() == null ? "" : usuarioActualizado.getNombres(),
                "apellidos", usuarioActualizado.getApellidos() == null ? "" : usuarioActualizado.getApellidos(),
                "correo", usuarioActualizado.getCorreo() == null ? "" : usuarioActualizado.getCorreo(),
                "tipoUsuario", usuarioActualizado.getTipoUsuario() == null ? "" : usuarioActualizado.getTipoUsuario()
        ));
        return ResponseEntity.ok(response);
    }

    // 1. ENDPOINT PARA ENVIAR EL CÓDIGO AL CORREO
    @PostMapping("/enviar-codigo-correo")
    public ResponseEntity<?> enviarCodigoCorreo(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        if (correo == null || correo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "El correo es requerido"));
        }

        // Genera código aleatorio de 6 dígitos
        String codigo = String.format("%06d", new java.util.Random().nextInt(999999));
        codigosVerificacion.put(correo.trim().toLowerCase(), codigo);

        System.out.println("================================================");
        System.out.println(">>> CÓDIGO DE VERIFICACIÓN PARA [" + correo + "]: " + codigo);
        System.out.println("================================================");

        // Intenta enviar el correo real si mailSender está configurado
        try {
            if (mailSender != null) {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(correo.trim());
                mensaje.setSubject("Código de Verificación - Smart Chiclayo");
                mensaje.setText("Hola,\n\nTu código de verificación para completar tu registro en Smart Chiclayo es: "
                        + codigo + "\n\nEste código es personal e intransferible.");
                mailSender.send(mensaje);
            }
        } catch (Exception e) {
            System.err.println("Aviso al enviar correo: " + e.getMessage());
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Código de verificación enviado al correo."
        ));
    }

    // 2. ENDPOINT PARA VERIFICAR EL CÓDIGO
    @PostMapping("/verificar-codigo-correo")
    public ResponseEntity<?> verificarCodigoCorreo(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String codigo = body.get("codigo");

        if (correo == null || correo.isBlank() || codigo == null || codigo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Correo y código requeridos"));
        }
        String codigoEsperado = codigosVerificacion.get(correo.trim().toLowerCase());

        if (codigoEsperado != null && codigoEsperado.equals(codigo.trim())) {
            codigosVerificacion.remove(correo.trim().toLowerCase());
            return ResponseEntity.ok(Map.of("success", true, "message", "Código verificado correctamente"));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                "success", false,
                "message", "El código ingresado es incorrecto o ha expirado."
        ));
    }
}