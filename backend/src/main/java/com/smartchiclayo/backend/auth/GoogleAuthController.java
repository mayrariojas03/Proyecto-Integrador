package com.smartchiclayo.backend.auth;

import com.smartchiclayo.backend.usuario.entity.Usuario;
import com.smartchiclayo.backend.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class GoogleAuthController {

    private final GoogleAuthService googleAuthService;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final long expirationMinutes;

    public GoogleAuthController(
            GoogleAuthService googleAuthService,
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            @Value("${app.jwt.expiration-minutes}") long expirationMinutes
    ) {
        this.googleAuthService = googleAuthService;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.expirationMinutes = expirationMinutes;
    }

    @PostMapping("/google")
    public ResponseEntity<?> autenticarConGoogle(@RequestBody Map<String, String> body) {
        String idToken = body == null ? null : body.get("idToken");

        if (idToken == null || idToken.isBlank()) {
            return ResponseEntity.badRequest().body(
                    respuesta(false, "Falta el ID token de Google")
            );
        }

        GoogleAuthService.GoogleUserInfo googleUser;
        try {
            googleUser = googleAuthService.verificarToken(idToken);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    respuesta(false, "Credencial de Google invalida")
            );
        } catch (GeneralSecurityException | IOException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(
                    respuesta(false, "No se pudo verificar Google en este momento")
            );
        }

        // Identificar cuentas Google exclusivamente por su identificador unico.
        Optional<Usuario> existenteGoogle =
                usuarioRepository.findByGoogleSub(googleUser.googleSub());

        if (existenteGoogle.isPresent()) {
            Usuario usuario = existenteGoogle.get();
            if (!"ACTIVO".equalsIgnoreCase(usuario.getEstado())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        respuesta(false, "La cuenta no esta activa")
                );
            }
            return ResponseEntity.ok(
                    respuestaUsuario("Cuenta de Google reconocida", usuario, false)
            );
        }

        // No asociar cuentas tradicionales solo porque coincida el correo.
        if (usuarioRepository.existsByCorreoIgnoreCase(googleUser.correo())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    respuesta(false,
                            "Este correo ya esta registrado. Inicia sesion con tu metodo habitual.")
            );
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setGoogleSub(googleUser.googleSub());
        nuevoUsuario.setCorreo(googleUser.correo());
        nuevoUsuario.setNombres(textoObligatorio(googleUser.nombres()));
        nuevoUsuario.setApellidos(textoObligatorio(googleUser.apellidos()));
        nuevoUsuario.setFotoPerfil(googleUser.fotoPerfil());
        nuevoUsuario.setTipoUsuario("PASAJERO");
        nuevoUsuario.setEstado("ACTIVO");
        nuevoUsuario.setCorreoVerificado(true);
        nuevoUsuario.setTelefonoVerificado(false);
        nuevoUsuario.setTipoDocumento(null);
        nuevoUsuario.setNumeroDocumento(null);
        nuevoUsuario.setTelefono(null);
        nuevoUsuario.setFechaNacimiento(null);
        nuevoUsuario.setPasswordHash(null);

        try {
            Usuario guardado = usuarioRepository.save(nuevoUsuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    respuestaUsuario("Cuenta de Google registrada correctamente", guardado, true)
            );
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    respuesta(false, "No se pudo registrar la cuenta. Comprueba si ya existe un usuario asociado.")
            );
        }
    }

    private String textoObligatorio(String valor) {
        return valor == null || valor.isBlank() ? "Pendiente" : valor.trim();
    }

    private Map<String, Object> respuesta(boolean success, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> respuestaUsuario(
            String message, Usuario usuario, boolean nuevoRegistro
    ) {
        Map<String, Object> response = respuesta(true, message);
        response.put("nuevoRegistro", nuevoRegistro);

        Map<String, Object> datosUsuario = new HashMap<>();
        datosUsuario.put("idUsuario", usuario.getIdUsuario());
        datosUsuario.put("nombres", usuario.getNombres());
        datosUsuario.put("apellidos", usuario.getApellidos());
        datosUsuario.put("correo", usuario.getCorreo());
        datosUsuario.put("tipoUsuario", usuario.getTipoUsuario());
        response.put("usuario", datosUsuario);

        // JWT propio del backend: no devolver el ID token de Google.
        response.put("accessToken", jwtService.generarToken(usuario));
        response.put("tokenType", "Bearer");
        response.put("expiresIn", Math.multiplyExact(expirationMinutes, 60L));
        return response;
    }
}
