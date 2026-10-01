# SMART CHICLAYO

Aplicativo móvil inteligente de movilidad urbana, taxis ejecutivos, mototaxis, colectivos interurbanos, transporte público y gestión integral de líneas en la ciudad de Chiclayo y la región Lambayeque.

## Arquitectura del Proyecto

```
SMART-CHICLAYO/
│
├── app/ (Frontend Android en Kotlin & Jetpack Compose)
│   ├── src/main/java/com/example/
│   │   ├── components/ (TopBar, Drawer, InteractiveMap, ThemeDialog)
│   │   ├── models/ (User, Trip, Driver, Manager, Incident, Notification, Payment)
│   │   ├── modules/
│   │   │   ├── auth/ (Welcome, Login, Register, Otp, Loading)
│   │   │   ├── passenger/ (Home, RequestTrip, TripHistory, Services, Intercity, PublicTransit)
│   │   │   ├── driver/ (DriverPortal, RequestDispatch, TripExecution)
│   │   │   ├── manager/ (Wizard Pasos 1 a 4, Dashboard)
│   │   │   ├── incidents/ (Reporte Viales MPCH)
│   │   │   ├── notifications/
│   │   │   ├── security/ (SOS 24/7, Contactos, Serenazgo, SAMU)
│   │   │   ├── settings/ (5 Temas, Privacidad, Cuenta)
│   │   │   ├── profile/
│   │   │   ├── payments/ (SmartPay, Yape, Plin, Tarjetas)
│   │   │   └── support/ (FAQs, Tickets)
│   │   ├── state/ (AppState & SmartChiclayoViewModel)
│   │   └── ui/theme/ (5 Temas: Claro, Oscuro, Rosa pastel, Verde pastel, Azul pastel)
│   └── res/
│
├── database/ (Scripts SQL para la base de datos central)
│   ├── 01_create_database.sql
│   ├── 02_create_tables.sql
│   ├── 03_initial_data.sql
│   └── 04_test_data.sql
│
└── docs/
    └── README.md
```

## Características Principales

1. **Pantalla de bienvenida:** Identidad visual de Chiclayo, frase principal "MOVERTE SEGURO ES LO JUSTO", fotografía de pasajero en Chiclayo y accesos directos.
2. **Inicio de sesión y registro:** Soporte para inicio sin contraseña previa vía Google / Facebook, autenticación por número de teléfono con código SMS (OTP) y registro con validación de DNI, teléfono y correo único. Rol predeterminado: Pasajero.
3. **Pantalla de carga animada:** Carrito desplazándose linealmente en el centro con verificación secuencial de sesión, perfil, ubicación y notificaciones.
4. **Pantalla principal interactiva:** Mapa interactivo con avenidas principales de Chiclayo (Balta, Salaverry, Bolognesi, Panamericana), selector de 4 modalidades (Taxi Smart, Mototaxi, Colectivo, Combi), buscador de ofertas de tarifa, destinos populares y asignación de conductor.
5. **Menú de navegación completo de 12 secciones:**
   - Perfil del usuario con DNI enmascarado
   - Ciudad (Urban rides)
   - Historial de viajes con filtros (Todas, Ciudad, Envíos Express, Ciudades) y reporte mensual SUNAT en PDF
   - Tipos de servicios
   - Chiclayo a Ciudades (Pimentel, Lambayeque, Ferreñafe, Monsefú, Reque, Santa Rosa)
   - Transporte público (Línea 12 Troncal, Línea 08 Ramal, paraderos y frecuencias)
   - Billetera & SmartPay (Yape, Plin, Efectivo, Tarjeta tokenizada sin guardar CVV)
   - Notificaciones categorizadas con contador
   - Seguridad y SOS 24/7 con botón de emergencia, enlace a Serenazgo 074-202222, SAMU 106, PNP 105 y contactos de confianza
   - Reporte de incidencias viales con auxilio rápido, geolocalización Balta/Bolognesi y alerta de flota en 2 km
   - Configuración con selector de 5 temas (Claro, Oscuro, Rosa pastel, Verde pastel, Azul pastel)
   - Ayuda & Soporte 24/7 con preguntas frecuentes y creación de tickets
   - Modo Gestor de Línea (Wizard de 4 pasos: Acreditación, Asignación de rutas, Padrón de flota con botón único de agregar unidad, Comunicación PTT y activación)
   - Modo Conductor (Gana con nosotros, beneficios, tarjetas con iconos tipo historial, consola de servicio con ciclo completo de viaje)
