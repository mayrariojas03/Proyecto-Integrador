-- SMART CHICLAYO - CATALOGOS Y CONFIGURACION INICIAL
USE smart_chiclayo;

-- Sin cuentas de usuarios registradas inicialmente por solicitud del usuario
-- Líneas de transporte público oficiales en Chiclayo:
INSERT IGNORE INTO lineas_transporte (numero_linea, nombre_oficial, origen, destino, frecuencia_minutos, codigo_mtc)
VALUES 
('Línea 12', 'Troncal Chiclayo Metropolitano', 'Pimentel', 'José Leonardo Ortiz', 4, 'REG-CHIC-L12'),
('Línea 08', 'Ramal Santa Victoria - Mall Aventura', 'Santa Victoria', 'Mall Aventura Panamericana', 6, 'REG-CHIC-L08'),
('Combi 03', 'Combi Urbana Pimentel Express', 'Malecón Pimentel', 'Mercado Modelo Chiclayo', 3, 'REG-CHIC-C03');
