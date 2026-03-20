-- =========================
-- INSERTS: CATEGORIAS
-- =========================
INSERT INTO categorias (nombre, descripcion) VALUES
('Senderismo', 'Excursiones y caminatas al aire libre'),
('Gastronomía', 'Tours y experiencias culinarias'),
('Cultural', 'Visitas a museos, monumentos y eventos históricos'),
('Aventura', 'Actividades extremas y deportes de riesgo'),
('Relax', 'Spas, playas y experiencias de bienestar');

-- =========================
-- INSERTS: USUARIOS
-- =========================
INSERT INTO usuarios (nombre, email, password, rol) VALUES
('Ana Pérez', 'ana.perez@example.com', 'hashed_password1', 'ROLE_USER'),
('Luis García', 'luis.garcia@example.com', 'hashed_password2', 'ROLE_USER'),
('Carlos López', 'carlos.lopez@example.com', 'hashed_password3', 'ROLE_ADMIN');

-- =========================
-- INSERTS: EXPERIENCIAS
-- =========================
INSERT INTO experiencias (titulo, descripcion, precio, ubicacion, duracion_horas, categoria_id) VALUES
('Caminata por la Sierra', 'Ruta guiada de 5 km por montañas locales', 25.00, 'Sierra Norte', 3.5, 1),
('Tour gastronómico en el casco histórico', 'Degustación de platos típicos y visita a mercados', 50.00, 'Ciudad Antigua', 4.0, 2),
('Visita al Museo de Arte Moderno', 'Recorrido guiado por exposiciones contemporáneas', 15.00, 'Museo Central', 2.0, 3),
('Rafting en el río Bravo', 'Aventura extrema en rápidos clase III y IV', 70.00, 'Río Bravo', 3.0, 4),
('Día de spa y relajación', 'Acceso a piscinas, sauna y masajes relajantes', 60.00, 'Spa Resort', 6.0, 5);

-- =========================
-- INSERTS: RESERVAS
-- =========================
INSERT INTO reservas (usuario_id, experiencia_id, fecha_reserva, numero_personas, precio_total, estado) VALUES
(1, 1, '2026-04-05 10:00', 2, 50.00, 'confirmada'),
(1, 2, '2026-04-10 12:00', 1, 50.00, 'pendiente'),
(2, 4, '2026-04-08 09:00', 3, 210.00, 'confirmada'),
(3, 5, '2026-04-15 15:00', 1, 60.00, 'pendiente'),
(2, 3, '2026-04-12 11:00', 2, 30.00, 'cancelada');