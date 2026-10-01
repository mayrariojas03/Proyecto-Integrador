-- =======================================================
-- SMART CHICLAYO - BASE DE DATOS MYSQL OFICIAL
-- =======================================================
DROP DATABASE IF EXISTS smart_chiclayo;

CREATE DATABASE smart_chiclayo
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE smart_chiclayo;

CREATE TABLE usuarios (
    id_usuario BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    tipo_documento ENUM('DNI','CE','PASAPORTE') DEFAULT 'DNI',
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    telefono VARCHAR(20) NOT NULL UNIQUE,
    correo VARCHAR(150) UNIQUE,
    password_hash VARCHAR(255) NULL,
    foto_perfil VARCHAR(255) NULL,

    tipo_usuario ENUM(
        'PASAJERO',
        'CONDUCTOR',
        'GESTOR'
    ) NOT NULL DEFAULT 'PASAJERO',

    estado ENUM(
        'ACTIVO',
        'INACTIVO',
        'SUSPENDIDO',
        'BLOQUEADO'
    ) DEFAULT 'ACTIVO',

    telefono_verificado BOOLEAN DEFAULT FALSE,
    correo_verificado BOOLEAN DEFAULT FALSE,

    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE perfiles_usuario (
    id_perfil BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL UNIQUE,

    fecha_nacimiento DATE NULL,
    direccion VARCHAR(255) NULL,
    ciudad VARCHAR(100) DEFAULT 'Chiclayo',
    departamento VARCHAR(100) DEFAULT 'Lambayeque',

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE configuraciones_usuario (
    id_configuracion BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL UNIQUE,

    tema ENUM(
        'CLARO',
        'OSCURO',
        'ROSA_PASTEL',
        'VERDE_PASTEL',
        'AZUL_PASTEL'
    ) DEFAULT 'OSCURO',

    notificaciones_viajes BOOLEAN DEFAULT TRUE,
    notificaciones_seguridad BOOLEAN DEFAULT TRUE,
    notificaciones_sistema BOOLEAN DEFAULT TRUE,
    compartir_ubicacion BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE sesiones (
    id_sesion BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,

    refresh_token_hash VARCHAR(255) NULL,
    dispositivo VARCHAR(150) NULL,
    direccion_ip VARCHAR(45) NULL,

    fecha_inicio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion DATETIME NULL,
    activa BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE metodos_autenticacion (
    id_metodo BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,

    proveedor ENUM(
        'LOCAL',
        'GOOGLE',
        'FACEBOOK',
        'TELEFONO'
    ) NOT NULL,

    identificador_proveedor VARCHAR(255) NULL,
    fecha_vinculacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    UNIQUE (id_usuario, proveedor)
);

CREATE TABLE dispositivos_usuario (
    id_dispositivo BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,

    token_dispositivo VARCHAR(500) NULL,
    plataforma ENUM(
        'ANDROID',
        'IOS',
        'WEB',
        'OTRO'
    ) DEFAULT 'ANDROID',

    modelo_dispositivo VARCHAR(150) NULL,
    activo BOOLEAN DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE contactos_confianza (
    id_contacto BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL,

    nombres VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    parentesco VARCHAR(80) NULL,
    activo BOOLEAN DEFAULT TRUE,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE pasajeros (
    id_pasajero BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL UNIQUE,

    promedio_calificacion DECIMAL(3,2) DEFAULT 5.00,
    total_viajes INT UNSIGNED DEFAULT 0,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE conductores (
    id_conductor BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL UNIQUE,

    numero_licencia VARCHAR(30) NOT NULL UNIQUE,
    categoria_licencia VARCHAR(20) NULL,
    vencimiento_licencia DATE NULL,

    estado_validacion ENUM(
        'PENDIENTE',
        'VERIFICADO',
        'RECHAZADO',
        'SUSPENDIDO'
    ) DEFAULT 'PENDIENTE',

    estado_servicio ENUM(
        'DESCONECTADO',
        'DISPONIBLE',
        'OCUPADO'
    ) DEFAULT 'DESCONECTADO',

    promedio_calificacion DECIMAL(3,2) DEFAULT 5.00,
    total_viajes INT UNSIGNED DEFAULT 0,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE gestores (
    id_gestor BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT UNSIGNED NOT NULL UNIQUE,

    cargo VARCHAR(100) NULL,

    estado_acreditacion ENUM(
        'BORRADOR',
        'PENDIENTE',
        'APROBADO',
        'RECHAZADO'
    ) DEFAULT 'BORRADOR',

    fecha_acreditacion DATETIME NULL,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE lineas (
    id_linea BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_gestor BIGINT UNSIGNED NOT NULL,

    nombre VARCHAR(150) NOT NULL,
    razon_social VARCHAR(180) NULL,
    ruc VARCHAR(11) NULL,
    telefono VARCHAR(20) NULL,
    direccion VARCHAR(255) NULL,

    estado ENUM(
        'BORRADOR',
        'PENDIENTE',
        'ACTIVA',
        'SUSPENDIDA',
        'INACTIVA'
    ) DEFAULT 'BORRADOR',

    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_gestor)
        REFERENCES gestores(id_gestor)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    UNIQUE (ruc)
);

CREATE TABLE vehiculos (
    id_vehiculo BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_linea BIGINT UNSIGNED NOT NULL,

    placa VARCHAR(10) NOT NULL UNIQUE,
    marca VARCHAR(80) NOT NULL,
    modelo VARCHAR(80) NOT NULL,
    anio SMALLINT UNSIGNED NULL,
    color VARCHAR(50) NULL,

    categoria ENUM(
        'TAXI',
        'MOTOTAXI',
        'COLECTIVO',
        'COMBI'
    ) DEFAULT 'TAXI',

    capacidad TINYINT UNSIGNED NULL,

    estado ENUM(
        'ACTIVO',
        'MANTENIMIENTO',
        'INACTIVO'
    ) DEFAULT 'ACTIVO',

    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_linea)
        REFERENCES lineas(id_linea)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE rutas_linea (
    id_ruta BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_linea BIGINT UNSIGNED NOT NULL,

    nombre VARCHAR(120) NULL,
    origen VARCHAR(255) NOT NULL,
    destino VARCHAR(255) NOT NULL,
    descripcion TEXT NULL,

    hora_inicio TIME NULL,
    hora_fin TIME NULL,

    frecuencia_minutos INT UNSIGNED NULL,

    estado ENUM(
        'ACTIVA',
        'INACTIVA'
    ) DEFAULT 'ACTIVA',

    FOREIGN KEY (id_linea)
        REFERENCES lineas(id_linea)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE linea_conductores (
    id_linea_conductor BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_linea BIGINT UNSIGNED NOT NULL,
    id_conductor BIGINT UNSIGNED NOT NULL,

    estado ENUM(
        'PENDIENTE',
        'ACTIVO',
        'RETIRADO',
        'SUSPENDIDO'
    ) DEFAULT 'PENDIENTE',

    fecha_ingreso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_retiro DATETIME NULL,

    FOREIGN KEY (id_linea)
        REFERENCES lineas(id_linea)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    FOREIGN KEY (id_conductor)
        REFERENCES conductores(id_conductor)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    UNIQUE (id_linea, id_conductor)
);

CREATE TABLE asignaciones_vehiculo (
    id_asignacion BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_linea_conductor BIGINT UNSIGNED NOT NULL,
    id_vehiculo BIGINT UNSIGNED NOT NULL,

    fecha_inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
    fecha_fin DATETIME NULL,

    estado ENUM(
        'ACTIVA',
        'FINALIZADA'
    ) DEFAULT 'ACTIVA',

    FOREIGN KEY (id_linea_conductor)
        REFERENCES linea_conductores(id_linea_conductor)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    FOREIGN KEY (id_vehiculo)
        REFERENCES vehiculos(id_vehiculo)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE solicitudes_viaje (
    id_solicitud BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_pasajero BIGINT UNSIGNED NOT NULL,

    latitud_origen DECIMAL(10,7) NULL,
    longitud_origen DECIMAL(10,7) NULL,
    direccion_origen VARCHAR(255) NOT NULL,

    latitud_destino DECIMAL(10,7) NULL,
    longitud_destino DECIMAL(10,7) NULL,
    direccion_destino VARCHAR(255) NOT NULL,

    tipo_servicio ENUM(
        'TAXI',
        'MOTOTAXI',
        'COLECTIVO',
        'COMBI'
    ) DEFAULT 'TAXI',

    tarifa_ofrecida DECIMAL(10,2) NULL,

    estado ENUM(
        'CREADA',
        'BUSCANDO',
        'ACEPTADA',
        'CANCELADA',
        'EXPIRADA'
    ) DEFAULT 'CREADA',

    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_pasajero)
        REFERENCES pasajeros(id_pasajero)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE viajes (
    id_viaje BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_solicitud BIGINT UNSIGNED NOT NULL UNIQUE,
    id_linea_conductor BIGINT UNSIGNED NOT NULL,
    id_vehiculo BIGINT UNSIGNED NOT NULL,

    estado ENUM(
        'CONDUCTOR_ASIGNADO',
        'CONDUCTOR_EN_CAMINO',
        'CONDUCTOR_LLEGO',
        'EN_CURSO',
        'COMPLETADO',
        'CANCELADO'
    ) NOT NULL DEFAULT 'CONDUCTOR_ASIGNADO',

    fecha_aceptacion DATETIME NULL,
    fecha_inicio DATETIME NULL,
    fecha_fin DATETIME NULL,

    distancia_km DECIMAL(8,2) NULL,
    duracion_minutos INT UNSIGNED NULL,
    tarifa_final DECIMAL(10,2) NULL,

    FOREIGN KEY (id_solicitud)
        REFERENCES solicitudes_viaje(id_solicitud)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    FOREIGN KEY (id_linea_conductor)
        REFERENCES linea_conductores(id_linea_conductor)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    FOREIGN KEY (id_vehiculo)
        REFERENCES vehiculos(id_vehiculo)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE historial_estado_viaje (
    id_historial BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_viaje BIGINT UNSIGNED NOT NULL,

    estado VARCHAR(50) NOT NULL,
    fecha_estado DATETIME DEFAULT CURRENT_TIMESTAMP,
    observacion VARCHAR(255) NULL,

    FOREIGN KEY (id_viaje)
        REFERENCES viajes(id_viaje)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE valoraciones_viaje (
    id_valoracion BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_viaje BIGINT UNSIGNED NOT NULL UNIQUE,

    puntuacion TINYINT UNSIGNED NOT NULL,
    comentario VARCHAR(500) NULL,
    fecha_valoracion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_viaje)
        REFERENCES viajes(id_viaje)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CHECK (puntuacion BETWEEN 1 AND 5)
);

CREATE TABLE tipos_incidencia (
    id_tipo_incidencia INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NULL,
    estado BOOLEAN DEFAULT TRUE
);

CREATE TABLE incidencias (
    id_incidencia BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_usuario BIGINT UNSIGNED NOT NULL,
    id_tipo_incidencia INT UNSIGNED NOT NULL,

    latitud DECIMAL(10,7) NOT NULL,
    longitud DECIMAL(10,7) NOT NULL,
    direccion VARCHAR(255) NULL,

    gravedad ENUM(
        'BAJA',
        'MEDIA',
        'CRITICA'
    ) NOT NULL,

    descripcion VARCHAR(1000) NULL,

    estado ENUM(
        'ACTIVA',
        'EN_REVISION',
        'RESUELTA',
        'EXPIRADA'
    ) DEFAULT 'ACTIVA',

    fecha_reporte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_expiracion DATETIME NULL,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,

    FOREIGN KEY (id_tipo_incidencia)
        REFERENCES tipos_incidencia(id_tipo_incidencia)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE evidencias_incidencia (
    id_evidencia BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_incidencia BIGINT UNSIGNED NOT NULL,

    tipo ENUM(
        'IMAGEN',
        'AUDIO'
    ) NOT NULL,

    archivo_url VARCHAR(500) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_incidencia)
        REFERENCES incidencias(id_incidencia)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE alertas_viaje (
    id_alerta BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_viaje BIGINT UNSIGNED NOT NULL,
    id_incidencia BIGINT UNSIGNED NOT NULL,

    mensaje VARCHAR(255) NOT NULL,
    distancia_metros INT UNSIGNED NULL,

    fecha_alerta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    visualizada BOOLEAN DEFAULT FALSE,

    FOREIGN KEY (id_viaje)
        REFERENCES viajes(id_viaje)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    FOREIGN KEY (id_incidencia)
        REFERENCES incidencias(id_incidencia)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE notificaciones (
    id_notificacion BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_usuario BIGINT UNSIGNED NOT NULL,

    titulo VARCHAR(150) NOT NULL,
    mensaje VARCHAR(500) NOT NULL,

    tipo ENUM(
        'VIAJE',
        'SEGURIDAD',
        'LINEA',
        'SISTEMA',
        'PAGO'
    ) NOT NULL,

    referencia_id BIGINT UNSIGNED NULL,
    leida BOOLEAN DEFAULT FALSE,

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE lugares_favoritos (
    id_favorito BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_usuario BIGINT UNSIGNED NOT NULL,

    nombre VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,

    latitud DECIMAL(10,7) NULL,
    longitud DECIMAL(10,7) NULL,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

CREATE TABLE pagos (
    id_pago BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_viaje BIGINT UNSIGNED NOT NULL UNIQUE,

    metodo ENUM(
        'EFECTIVO',
        'TARJETA',
        'BILLETERA_DIGITAL'
    ) NOT NULL,

    monto DECIMAL(10,2) NOT NULL,

    estado ENUM(
        'PENDIENTE',
        'PAGADO',
        'RECHAZADO',
        'REEMBOLSADO'
    ) DEFAULT 'PENDIENTE',

    referencia_externa VARCHAR(255) NULL,
    fecha_pago DATETIME NULL,

    FOREIGN KEY (id_viaje)
        REFERENCES viajes(id_viaje)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE tickets_soporte (
    id_ticket BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_usuario BIGINT UNSIGNED NOT NULL,

    categoria VARCHAR(100) NOT NULL,
    asunto VARCHAR(200) NOT NULL,
    descripcion TEXT NOT NULL,

    estado ENUM(
        'ABIERTO',
        'EN_PROCESO',
        'RESUELTO',
        'CERRADO'
    ) DEFAULT 'ABIERTO',

    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
        ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

CREATE TABLE auditoria (
    id_auditoria BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,

    id_usuario BIGINT UNSIGNED NOT NULL,

    accion VARCHAR(100) NOT NULL,
    entidad VARCHAR(100) NOT NULL,
    id_entidad BIGINT UNSIGNED NULL,

    descripcion VARCHAR(500) NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- =======================================================
-- DATOS INICIALES Y PRUEBAS
-- =======================================================
INSERT INTO tipos_incidencia (nombre, descripcion) VALUES
('Tráfico severo', 'Congestión vehicular considerable en una vía'),
('Accidente vial', 'Accidente vehicular reportado en la zona'),
('Vía bloqueada', 'Calle o vía temporalmente bloqueada'),
('Falla mecánica', 'Vehículo detenido por falla mecánica'),
('Situación de seguridad', 'Situación que podría representar un riesgo'),
('Otro', 'Incidencia no incluida en las categorías anteriores');

INSERT INTO usuarios (nombres, apellidos, tipo_documento, numero_documento, telefono, correo, tipo_usuario, estado, telefono_verificado, correo_verificado)
VALUES
('Ana', 'Torres', 'DNI', '70000001', '987000001', 'ana@smartchiclayo.pe', 'PASAJERO', 'ACTIVO', TRUE, TRUE),
('Carlos', 'Mendoza', 'DNI', '70000002', '987000002', 'carlos@smartchiclayo.pe', 'CONDUCTOR', 'ACTIVO', TRUE, TRUE),
('Luis', 'Ramirez', 'DNI', '70000003', '987000003', 'luis@smartchiclayo.pe', 'GESTOR', 'ACTIVO', TRUE, TRUE);

INSERT INTO perfiles_usuario (id_usuario, fecha_nacimiento, direccion, ciudad, departamento)
VALUES
(1, '2000-05-10', 'Chiclayo Centro', 'Chiclayo', 'Lambayeque'),
(2, '1995-08-15', 'La Victoria', 'Chiclayo', 'Lambayeque'),
(3, '1988-12-20', 'José Leonardo Ortiz', 'Chiclayo', 'Lambayeque');

INSERT INTO configuraciones_usuario (id_usuario, tema)
VALUES
(1, 'OSCURO'),
(2, 'OSCURO'),
(3, 'OSCURO');

INSERT INTO pasajeros (id_usuario, promedio_calificacion, total_viajes)
VALUES (1, 5.00, 0);

INSERT INTO conductores (id_usuario, numero_licencia, categoria_licencia, vencimiento_licencia, estado_validacion, estado_servicio)
VALUES (2, 'LIC-000001', 'A-IIa', '2030-12-31', 'VERIFICADO', 'DISPONIBLE');

INSERT INTO gestores (id_usuario, cargo, estado_acreditacion, fecha_acreditacion)
VALUES (3, 'Administrador de línea', 'APROBADO', NOW());

INSERT INTO lineas (id_gestor, nombre, razon_social, ruc, telefono, direccion, estado)
VALUES (1, 'Línea Smart 12', 'Transportes Smart Chiclayo SAC', '20123456789', '074555555', 'Chiclayo, Lambayeque', 'ACTIVA');

INSERT INTO vehiculos (id_linea, placa, marca, modelo, anio, color, categoria, capacidad, estado)
VALUES (1, 'M3B120', 'Toyota', 'Yaris', 2024, 'Blanco', 'TAXI', 4, 'ACTIVO');

INSERT INTO rutas_linea (id_linea, nombre, origen, destino, descripcion, hora_inicio, hora_fin, frecuencia_minutos, estado)
VALUES (1, 'Ruta Principal', 'Chiclayo Centro', 'José Leonardo Ortiz', 'Ruta urbana de prueba', '06:00:00', '22:00:00', 10, 'ACTIVA');

INSERT INTO linea_conductores (id_linea, id_conductor, estado) VALUES (1, 1, 'ACTIVO');
INSERT INTO asignaciones_vehiculo (id_linea_conductor, id_vehiculo, estado) VALUES (1, 1, 'ACTIVA');

INSERT INTO lugares_favoritos (id_usuario, nombre, direccion, latitud, longitud)
VALUES (1, 'Plaza de Armas de Chiclayo', 'Centro de Chiclayo', -6.7714000, -79.8409000);
