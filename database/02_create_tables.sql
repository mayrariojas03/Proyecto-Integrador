-- SMART CHICLAYO - TABLAS DEL SISTEMA
USE smart_chiclayo;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni VARCHAR(15) UNIQUE NOT NULL,
    telefono VARCHAR(20) UNIQUE NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    fecha_nacimiento DATE NULL,
    password_hash VARCHAR(255) NULL,
    metodo_registro VARCHAR(50) DEFAULT 'STANDARD',
    rol VARCHAR(30) DEFAULT 'PASAJERO',
    calificacion DECIMAL(3,2) DEFAULT 5.00,
    total_viajes INT DEFAULT 0,
    foto_url VARCHAR(500) NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS conductores (
    id_conductor BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    numero_licencia VARCHAR(30) UNIQUE NOT NULL,
    categoria_licencia VARCHAR(15) NOT NULL,
    fecha_vencimiento_licencia DATE NOT NULL,
    estado_verificacion VARCHAR(30) DEFAULT 'PENDIENTE',
    estado_operativo VARCHAR(30) DEFAULT 'DESCONECTADO',
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS lineas_transporte (
    id_linea BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_linea VARCHAR(50) NOT NULL,
    nombre_oficial VARCHAR(150) NOT NULL,
    origen VARCHAR(150) NOT NULL,
    destino VARCHAR(150) NOT NULL,
    frecuencia_minutos INT DEFAULT 5,
    horario_inicio TIME DEFAULT '05:30:00',
    horario_fin TIME DEFAULT '22:30:00',
    codigo_mtc VARCHAR(50) NULL,
    estado VARCHAR(20) DEFAULT 'ACTIVA'
);

CREATE TABLE IF NOT EXISTS vehiculos (
    id_vehiculo BIGINT AUTO_INCREMENT PRIMARY KEY,
    placa VARCHAR(15) UNIQUE NOT NULL,
    marca VARCHAR(50) NOT NULL,
    modelo VARCHAR(50) NOT NULL,
    anio INT NOT NULL,
    color VARCHAR(30) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    capacidad_pasajeros INT DEFAULT 4,
    soat_vigente BOOLEAN DEFAULT TRUE,
    id_linea BIGINT NULL,
    FOREIGN KEY (id_linea) REFERENCES lineas_transporte(id_linea)
);

CREATE TABLE IF NOT EXISTS viajes (
    id_viaje BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_viaje VARCHAR(30) UNIQUE NOT NULL,
    id_pasajero BIGINT NOT NULL,
    id_conductor BIGINT NULL,
    tipo_servicio VARCHAR(50) NOT NULL,
    origen_direccion VARCHAR(255) NOT NULL,
    destino_direccion VARCHAR(255) NOT NULL,
    distancia_km DECIMAL(6,2) NOT NULL,
    duracion_min INT NOT NULL,
    precio_oferta DECIMAL(8,2) NOT NULL,
    precio_final DECIMAL(8,2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    estado VARCHAR(30) DEFAULT 'SOLICITADO',
    calificacion_pasajero INT NULL,
    fecha_viaje TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_pasajero) REFERENCES usuarios(id_usuario)
);

CREATE TABLE IF NOT EXISTS reportes_incidencias (
    id_incidencia BIGINT AUTO_INCREMENT PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    tipo_incidencia VARCHAR(60) NOT NULL,
    nivel_gravedad VARCHAR(30) NOT NULL,
    direccion_referencia VARCHAR(255) NOT NULL,
    latitud DECIMAL(10,8) NULL,
    longitud DECIMAL(11,8) NULL,
    descripcion TEXT NOT NULL,
    foto_adjunta_url VARCHAR(500) NULL,
    alertar_flota BOOLEAN DEFAULT TRUE,
    estado VARCHAR(30) DEFAULT 'ACTIVA',
    fecha_reporte TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_usuario) REFERENCES usuarios(id_usuario)
);
