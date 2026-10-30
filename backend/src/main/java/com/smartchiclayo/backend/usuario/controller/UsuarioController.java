
package com.smartchiclayo.backend.usuario.controller;

import com.smartchiclayo.backend.usuario.entity.Usuario;
import com.smartchiclayo.backend.usuario.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    private static final String ESTADO_PENDIENTE =
            "PENDIENTE_VERIFICACION";

    private static final String ESTADO_ACTIVO = "ACTIVO";

    private static final String CODIGO_VERIFICACION_REQUERIDA =
            "EMAIL_VERIFICATION_REQUIRED";

    private static final Duration VIGENCIA_CODIGO =
            Duration.ofMinutes(5);

    private static final Duration ESPERA_REENVIO =
            Duration.ofSeconds(60);

    private static final int MAX_INTENTOS = 5;

    private static final Pattern PATRON_CORREO = Pattern.compile(
            "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$"
    );

    private final UsuarioRepository usuarioRepository;
    private final JavaMailSender mailSender;

    private final PasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder(12);

    private final SecureRandom secureRandom =
            new SecureRandom();

    private final Map<String, CodigoTemporal> codigosVerificacion =
            new ConcurrentHashMap<>();

    private static class CodigoTemporal {

        private final String codigo;
        private final Instant fechaEnvio;
        private final Instant fechaExpiracion;

        private int intentosFallidos;

        private CodigoTemporal(
                String codigo,
                Instant ahora
        ) {
            this.codigo = codigo;
            this.fechaEnvio = ahora;
            this.fechaExpiracion = ahora.plus(VIGENCIA_CODIGO);
            this.intentosFallidos = 0;
        }

        private boolean estaExpirado() {
            return !Instant.now().isBefore(fechaExpiracion);
        }

        private boolean puedeReenviar() {
            return !Instant.now().isBefore(
                    fechaEnvio.plus(ESPERA_REENVIO)
            );
        }
    }

    public UsuarioController(
            UsuarioRepository usuarioRepository,
            JavaMailSender mailSender
    ) {
        this.usuarioRepository = usuarioRepository;
        this.mailSender = mailSender;
    }

    // ============================================================
    // FUNCIONES AUXILIARES
    // ============================================================

    private String normalizarCorreo(String correo) {
        if (correo == null) {
            return "";
        }

        return correo.trim().toLowerCase(Locale.ROOT);
    }

    private boolean correoValido(String correo) {
        return correo != null
                && correo.length() <= 254
                && PATRON_CORREO.matcher(correo).matches();
    }

    private Map<String, Object> respuestaUsuario(
            Usuario usuario
    ) {
        Map<String, Object> datos = new HashMap<>();

        datos.put(
                "idUsuario",
                usuario.getIdUsuario()
        );

        datos.put(
                "nombres",
                usuario.getNombres() == null
                        ? ""
                        : usuario.getNombres()
        );

        datos.put(
                "apellidos",
                usuario.getApellidos() == null
                        ? ""
                        : usuario.getApellidos()
        );

        datos.put(
                "tipoDocumento",
                usuario.getTipoDocumento() == null
                        ? "DNI"
                        : usuario.getTipoDocumento()
        );

        datos.put(
                "numeroDocumento",
                usuario.getNumeroDocumento() == null
                        ? ""
                        : usuario.getNumeroDocumento()
        );

        datos.put(
                "telefono",
                usuario.getTelefono() == null
                        ? ""
                        : usuario.getTelefono()
        );

        datos.put(
                "correo",
                usuario.getCorreo() == null
                        ? ""
                        : usuario.getCorreo()
        );

        datos.put(
                "fotoPerfil",
                usuario.getFotoPerfil()
        );

        datos.put(
                "tipoUsuario",
                usuario.getTipoUsuario() == null
                        ? ""
                        : usuario.getTipoUsuario()
        );

        datos.put(
                "estado",
                usuario.getEstado() == null
                        ? ""
                        : usuario.getEstado()
        );

        datos.put(
                "correoVerificado",
                Boolean.TRUE.equals(
                        usuario.getCorreoVerificado()
                )
        );

        datos.put(
                "telefonoVerificado",
                Boolean.TRUE.equals(
                        usuario.getTelefonoVerificado()
                )
        );

        datos.put(
                "fechaNacimiento",
                usuario.getFechaNacimiento()
        );

        // Nunca devolver contraseñas ni googleSub.
        return datos;
    }

    private ResponseEntity<?> error(
            HttpStatus estado,
            String mensaje
    ) {
        return ResponseEntity.status(estado).body(
                Map.of(
                        "success", false,
                        "message", mensaje
                )
        );
    }

    private ResponseEntity<?> errorVerificacionPendiente(
            Usuario usuario
    ) {
        Map<String, Object> respuesta = new HashMap<>();

        respuesta.put("success", false);

        respuesta.put(
                "code",
                CODIGO_VERIFICACION_REQUERIDA
        );

        respuesta.put(
                "message",
                "Tu cuenta está pendiente de verificación. "
                        + "Solicita un nuevo código para activar tu correo."
        );

        respuesta.put(
                "correo",
                normalizarCorreo(usuario.getCorreo())
        );

        respuesta.put(
                "requiresEmailVerification",
                true
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(respuesta);
    }

    private boolean contrasenaCorrecta(
            Usuario usuario,
            String password
    ) {
        if (usuario == null
                || password == null
                || password.isBlank()) {
            return false;
        }

        String hashGuardado = usuario.getPasswordHash();

        if (hashGuardado == null
                || !hashGuardado.startsWith("$2")) {
            return false;
        }

        return passwordEncoder.matches(
                password,
                hashGuardado
        );
    }

    private Optional<Usuario> buscarPorIdentificador(
            String identificador
    ) {
        if (identificador == null
                || identificador.isBlank()) {
            return Optional.empty();
        }

        String valor = identificador.trim();

        return usuarioRepository.findAll()
                .stream()
                .filter(usuario ->
                        (
                                usuario.getCorreo() != null
                                        && usuario.getCorreo()
                                        .equalsIgnoreCase(valor)
                        )
                                || (
                                usuario.getTelefono() != null
                                        && usuario.getTelefono().equals(valor)
                        )
                                || (
                                usuario.getNumeroDocumento() != null
                                        && usuario.getNumeroDocumento().equals(valor)
                        )
                )
                .findFirst();
    }

    // ============================================================
    // LISTADO DE USUARIOS
    // ============================================================
    // IMPORTANTE:
    // SecurityConfig debe restringir esta ruta.
    // No debe quedar accesible públicamente.

    @GetMapping
    public List<Map<String, Object>> listarUsuarios() {
        return usuarioRepository.findAll()
                .stream()
                .map(this::respuestaUsuario)
                .toList();
    }

    // ============================================================
    // REGISTRO TRADICIONAL
    // ============================================================
    // La cuenta se crea pendiente.
    // Spring Boot envía el primer código automáticamente.

    @PostMapping("/registro")
    public synchronized ResponseEntity<?> registrarUsuario(
            @RequestBody Usuario nuevoUsuario
    ) {

        if (nuevoUsuario == null) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "Datos de registro requeridos."
            );
        }

        String correo = normalizarCorreo(
                nuevoUsuario.getCorreo()
        );

        if (!correoValido(correo)) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "Ingresa un correo electrónico válido."
            );
        }

        if (nuevoUsuario.getNombres() == null
                || nuevoUsuario.getNombres().isBlank()
                || nuevoUsuario.getApellidos() == null
                || nuevoUsuario.getApellidos().isBlank()) {

            return error(
                    HttpStatus.BAD_REQUEST,
                    "Los nombres y apellidos son obligatorios."
            );
        }

        String password = nuevoUsuario.getPasswordHash();

        if (password == null || password.length() < 8) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña debe tener al menos 8 caracteres."
            );
        }

        if (password.length() > 72) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "La contraseña no debe superar los 72 caracteres."
            );
        }

        if (usuarioRepository.existsByCorreoIgnoreCase(correo)) {
            return error(
                    HttpStatus.CONFLICT,
                    "Ya existe una cuenta registrada con ese correo. "
                            + "Si está pendiente, inicia sesión "
                            + "para solicitar otro código."
            );
        }

        String dni = nuevoUsuario.getNumeroDocumento();

        if (dni == null || !dni.matches("\\d{8}")) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "El DNI debe contener exactamente 8 dígitos."
            );
        }

        boolean dniRegistrado = usuarioRepository.findAll()
                .stream()
                .anyMatch(usuario ->
                        dni.equals(usuario.getNumeroDocumento())
                );

        if (dniRegistrado) {
            return error(
                    HttpStatus.CONFLICT,
                    "El número de documento (DNI) "
                            + "ya se encuentra registrado."
            );
        }

        String telefono = nuevoUsuario.getTelefono();

        if (telefono == null
                || !telefono.matches("9\\d{8}")) {

            return error(
                    HttpStatus.BAD_REQUEST,
                    "El teléfono debe tener 9 dígitos "
                            + "y comenzar con 9."
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNombres(
                nuevoUsuario.getNombres().trim()
        );

        usuario.setApellidos(
                nuevoUsuario.getApellidos().trim()
        );

        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuario.setNumeroDocumento(dni);
        usuario.setTipoDocumento("DNI");

        String fechaNacimiento =
                nuevoUsuario.getFechaNacimiento();

        usuario.setFechaNacimiento(
                fechaNacimiento == null
                        || fechaNacimiento.isBlank()
                        ? null
                        : fechaNacimiento
        );

        usuario.setFotoPerfil(null);
        usuario.setTipoUsuario("PASAJERO");

        usuario.setPasswordHash(
                passwordEncoder.encode(password)
        );

        usuario.setEstado(ESTADO_PENDIENTE);
        usuario.setCorreoVerificado(false);
        usuario.setTelefonoVerificado(false);
        usuario.setGoogleSub(null);

        Usuario guardado = usuarioRepository.save(usuario);

        ResponseEntity<?> resultadoEnvio =
                enviarCodigoInterno(correo);

        if (!resultadoEnvio.getStatusCode()
                .is2xxSuccessful()) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of(
                            "success", false,
                            "message",
                            "La cuenta quedó pendiente, "
                                    + "pero no se pudo enviar el código. "
                                    + "Inicia sesión para solicitar otro.",
                            "usuario",
                            respuestaUsuario(guardado)
                    ));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "success", true,
                        "message",
                        "Registro pendiente. Revisa tu correo "
                                + "e ingresa el código de verificación.",
                        "usuario",
                        respuestaUsuario(guardado)
                ));
    }

    // ============================================================
    // LOGIN TRADICIONAL
    // ============================================================
    // Valida la contraseña antes de revelar que una cuenta
    // necesita verificación.
    //
    // Android reconocerá:
    // code = EMAIL_VERIFICATION_REQUIRED

    @PostMapping("/login")
    public ResponseEntity<?> loginUsuario(
            @RequestBody Map<String, String> credenciales
    ) {

        if (credenciales == null) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "Credenciales requeridas."
            );
        }

        String identificador =
                credenciales.get("identificador");

        String password =
                credenciales.get("password");

        if (identificador == null
                || identificador.isBlank()
                || password == null
                || password.isBlank()) {

            return error(
                    HttpStatus.BAD_REQUEST,
                    "Identificador y contraseña requeridos."
            );
        }

        Optional<Usuario> usuarioOpt =
                buscarPorIdentificador(identificador);

        if (usuarioOpt.isEmpty()) {
            return error(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas."
            );
        }

        Usuario usuario = usuarioOpt.get();

        if (!contrasenaCorrecta(usuario, password)) {
            return error(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas."
            );
        }

        // Cuenta registrada pero todavía no verificada.
        if (ESTADO_PENDIENTE.equalsIgnoreCase(
                usuario.getEstado()
        ) && !Boolean.TRUE.equals(
                usuario.getCorreoVerificado()
        )) {

            return errorVerificacionPendiente(usuario);
        }

        // También impedir acceso si el correo quedó sin verificar.
        if (!Boolean.TRUE.equals(
                usuario.getCorreoVerificado()
        )) {
            return error(
                    HttpStatus.FORBIDDEN,
                    "Debes verificar tu correo electrónico "
                            + "antes de iniciar sesión."
            );
        }

        if (!ESTADO_ACTIVO.equalsIgnoreCase(
                usuario.getEstado()
        )) {
            return error(
                    HttpStatus.FORBIDDEN,
                    "Tu cuenta todavía no está activa."
            );
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Inicio de sesión exitoso",
                "usuario", respuestaUsuario(usuario)
        ));
    }

    // ============================================================
    // ACTUALIZACIÓN DEL PERFIL PROTEGIDA CON JWT
    // ============================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarUsuario(
            @PathVariable Long id,
            @RequestBody Map<String, Object> campos,
            @AuthenticationPrincipal Jwt jwt
    ) {

        if (jwt == null
                || !id.toString().equals(jwt.getSubject())) {

            return error(
                    HttpStatus.FORBIDDEN,
                    "No tienes permiso para modificar este perfil."
            );
        }

        Optional<Usuario> usuarioOpt =
                usuarioRepository.findById(id);

        if (usuarioOpt.isEmpty()) {
            return error(
                    HttpStatus.NOT_FOUND,
                    "Usuario no encontrado."
            );
        }

        Usuario usuario = usuarioOpt.get();

        if (campos.containsKey("telefono")) {
            usuario.setTelefono(
                    (String) campos.get("telefono")
            );
        }

        if (campos.containsKey("correo")) {
            String nuevoCorreo = normalizarCorreo(
                    (String) campos.get("correo")
            );

            if (!correoValido(nuevoCorreo)) {
                return error(
                        HttpStatus.BAD_REQUEST,
                        "Ingresa un correo electrónico válido."
                );
            }

            if (!nuevoCorreo.equalsIgnoreCase(
                    usuario.getCorreo()
            )) {

                Optional<Usuario> existente =
                        usuarioRepository
                                .findByCorreoIgnoreCase(nuevoCorreo);

                if (existente.isPresent()
                        && !existente.get()
                        .getIdUsuario().equals(id)) {

                    return error(
                            HttpStatus.CONFLICT,
                            "Ese correo ya está registrado."
                    );
                }

                usuario.setCorreoVerificado(false);
                usuario.setEstado(ESTADO_PENDIENTE);
            }

            usuario.setCorreo(nuevoCorreo);
        }

        if (campos.containsKey("numeroDocumento")) {
            usuario.setNumeroDocumento(
                    (String) campos.get("numeroDocumento")
            );
        }

        if (campos.containsKey("nombres")) {
            usuario.setNombres(
                    (String) campos.get("nombres")
            );
        }

        if (campos.containsKey("apellidos")) {
            usuario.setApellidos(
                    (String) campos.get("apellidos")
            );
        }

        if (campos.containsKey("fechaNacimiento")) {
            usuario.setFechaNacimiento(
                    (String) campos.get("fechaNacimiento")
            );
        }

        Usuario actualizado =
                usuarioRepository.save(usuario);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message",
                "Usuario actualizado exitosamente en MySQL",
                "usuario",
                respuestaUsuario(actualizado)
        ));
    }

    // ============================================================
    // REENVÍO DEL CÓDIGO POR GMAIL
    // ============================================================
    // Para reenviar un código sin iniciar sesión,
    // se requiere la contraseña correcta de la cuenta.
    //
    // Solicitud:
    // {
    //   "correo": "usuario@gmail.com",
    //   "password": "contraseña"
    // }

    @PostMapping("/enviar-codigo-correo")
    public synchronized ResponseEntity<?> enviarCodigoCorreo(
            @RequestBody Map<String, String> body
    ) {

        if (body == null) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "Correo y contraseña requeridos."
            );
        }

        String correo =
                normalizarCorreo(body.get("correo"));

        String password =
                body.get("password");

        if (!correoValido(correo)) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "Ingresa un correo electrónico válido."
            );
        }

        if (password == null || password.isBlank()) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "Ingresa tu contraseña para reenviar el código."
            );
        }

        Optional<Usuario> usuarioOpt =
                usuarioRepository.findByCorreoIgnoreCase(correo);

        if (usuarioOpt.isEmpty()) {
            return error(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas."
            );
        }

        Usuario usuario = usuarioOpt.get();

        if (!contrasenaCorrecta(usuario, password)) {
            return error(
                    HttpStatus.UNAUTHORIZED,
                    "Credenciales incorrectas."
            );
        }

        if (Boolean.TRUE.equals(
                usuario.getCorreoVerificado()
        )) {
            return error(
                    HttpStatus.CONFLICT,
                    "Este correo ya está verificado."
            );
        }

        if (!ESTADO_PENDIENTE.equalsIgnoreCase(
                usuario.getEstado()
        )) {
            return error(
                    HttpStatus.FORBIDDEN,
                    "La cuenta no está pendiente de verificación."
            );
        }

        return enviarCodigoInterno(correo);
    }

    // ============================================================
    // GENERACIÓN Y ENVÍO DEL CÓDIGO
    // ============================================================

    private ResponseEntity<?> enviarCodigoInterno(
            String correo
    ) {

        CodigoTemporal anterior =
                codigosVerificacion.get(correo);

        if (anterior != null
                && !anterior.puedeReenviar()) {

            return error(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Espera 60 segundos antes de solicitar otro código."
            );
        }

        String codigo = String.format(
                Locale.ROOT,
                "%06d",
                secureRandom.nextInt(1_000_000)
        );

        CodigoTemporal temporal =
                new CodigoTemporal(
                        codigo,
                        Instant.now()
                );

        try {
            SimpleMailMessage mensaje =
                    new SimpleMailMessage();

            mensaje.setTo(correo);

            mensaje.setSubject(
                    "Código de verificación - Smart Chiclayo"
            );

            mensaje.setText(
                    "Hola,\n\n"
                            + "Tu código de verificación "
                            + "para Smart Chiclayo es:\n\n"
                            + codigo
                            + "\n\nEste código vence en 5 minutos."
                            + "\nTienes un máximo de 5 intentos."
                            + "\n\nSi no solicitaste este código, "
                            + "ignora el mensaje."
            );

            mailSender.send(mensaje);

            // El código anterior queda invalidado
            // únicamente cuando el nuevo correo se envía.
            codigosVerificacion.put(
                    correo,
                    temporal
            );

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message",
                    "Código de verificación enviado al correo.",
                    "expiresInSeconds", 300,
                    "resendAfterSeconds", 60
            ));

        } catch (Exception ex) {
            System.err.println(
                    "Error SMTP al enviar código: "
                            + ex.getClass().getSimpleName()
            );

            return error(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible enviar el correo. "
                            + "Inténtalo nuevamente."
            );
        }
    }

    // ============================================================
    // VERIFICACIÓN DEL CÓDIGO Y ACTIVACIÓN DE CUENTA
    // ============================================================

    @PostMapping("/verificar-codigo-correo")
    public synchronized ResponseEntity<?> verificarCodigoCorreo(
            @RequestBody Map<String, String> body
    ) {

        if (body == null) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "Correo y código requeridos."
            );
        }

        String correo =
                normalizarCorreo(body.get("correo"));

        String codigo =
                body.get("codigo");

        if (!correoValido(correo)
                || codigo == null
                || !codigo.trim().matches("\\d{6}")) {

            return error(
                    HttpStatus.BAD_REQUEST,
                    "Correo y código de seis dígitos requeridos."
            );
        }

        Optional<Usuario> usuarioOpt =
                usuarioRepository.findByCorreoIgnoreCase(correo);

        if (usuarioOpt.isEmpty()) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "No se pudo verificar el código."
            );
        }

        Usuario usuario = usuarioOpt.get();

        if (Boolean.TRUE.equals(
                usuario.getCorreoVerificado()
        )) {
            return error(
                    HttpStatus.CONFLICT,
                    "El correo ya fue verificado."
            );
        }

        if (!ESTADO_PENDIENTE.equalsIgnoreCase(
                usuario.getEstado()
        )) {
            return error(
                    HttpStatus.FORBIDDEN,
                    "La cuenta no está pendiente de verificación."
            );
        }

        CodigoTemporal temporal =
                codigosVerificacion.get(correo);

        if (temporal == null) {
            return error(
                    HttpStatus.BAD_REQUEST,
                    "No existe un código vigente. "
                            + "Solicita uno nuevo."
            );
        }

        if (temporal.estaExpirado()) {
            codigosVerificacion.remove(correo);

            return error(
                    HttpStatus.BAD_REQUEST,
                    "El código expiró. Solicita uno nuevo."
            );
        }

        if (temporal.intentosFallidos >= MAX_INTENTOS) {
            codigosVerificacion.remove(correo);

            return error(
                    HttpStatus.TOO_MANY_REQUESTS,
                    "Superaste el máximo de intentos. "
                            + "Solicita otro código."
            );
        }

        if (!temporal.codigo.equals(
                codigo.trim()
        )) {

            temporal.intentosFallidos++;

            if (temporal.intentosFallidos >= MAX_INTENTOS) {
                codigosVerificacion.remove(correo);

                return error(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Superaste el máximo de intentos. "
                                + "Solicita otro código."
                );
            }

            return error(
                    HttpStatus.BAD_REQUEST,
                    "Código incorrecto. Intentos restantes: "
                            + (
                            MAX_INTENTOS
                                    - temporal.intentosFallidos
                    )
            );
        }

        // El código es correcto.
        // Activar la cuenta y marcar el correo verificado.

        usuario.setCorreoVerificado(true);
        usuario.setEstado(ESTADO_ACTIVO);

        Usuario actualizado =
                usuarioRepository.save(usuario);

        codigosVerificacion.remove(correo);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message",
                "Correo verificado. Tu cuenta ya está activa.",
                "usuario",
                respuestaUsuario(actualizado)
        ));
    }
}
