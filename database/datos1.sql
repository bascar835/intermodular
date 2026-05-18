-- Categorias
INSERT INTO public.categorias VALUES (1, 'Aventura', 'Experiencias de aventura y adrenalina', '/uploads/categorias/aventura.png', true);
INSERT INTO public.categorias VALUES (2, 'Gastronomía', 'Experiencias culinarias y degustación', '/uploads/categorias/gastronomia.png', true);
INSERT INTO public.categorias VALUES (3, 'Cultura', 'Experiencias culturales y turísticas', '/uploads/categorias/cultura.png', true);
INSERT INTO public.categorias VALUES (4, 'Relax', 'Experiencias de bienestar y relajación', '/uploads/categorias/relax.png', true);

SELECT pg_catalog.setval('public.categorias_id_seq', 4, true);

-- Experiencias
INSERT INTO public.experiencias VALUES (1, 'Senderismo en la Sierra Nevada', 'Ruta de senderismo por los picos más altos de la Sierra Nevada con guía experto.', 49.99, 'Granada', 6.00, 1, '2026-01-10 10:00:00');
INSERT INTO public.experiencias VALUES (2, 'Taller de cocina mediterránea', 'Aprende a cocinar platos tradicionales mediterráneos con un chef profesional.', 65.00, 'Valencia', 3.00, 2, '2026-01-15 10:00:00');
INSERT INTO public.experiencias VALUES (3, 'Visita guiada al Museo del Prado', 'Recorrido exclusivo por las obras más importantes del Museo del Prado.', 35.00, 'Madrid', 2.50, 3, '2026-01-20 10:00:00');
INSERT INTO public.experiencias VALUES (4, 'Sesión de yoga en la playa', 'Sesión de yoga al amanecer en la playa con instructor certificado.', 25.00, 'Benidorm', 1.50, 4, '2026-02-01 10:00:00');
INSERT INTO public.experiencias VALUES (5, 'Kayak en el Mediterráneo', 'Excursión en kayak por la costa mediterránea con parada en cala secreta.', 55.00, 'Alicante', 4.00, 1, '2026-02-10 10:00:00');

SELECT pg_catalog.setval('public.experiencias_id_seq', 5, true);

-- Experiencia imagenes
INSERT INTO public.experiencia_imagenes VALUES (1, 1, '/uploads/experiencias/senderismo.png');
INSERT INTO public.experiencia_imagenes VALUES (2, 2, '/uploads/experiencias/cocina.png');
INSERT INTO public.experiencia_imagenes VALUES (3, 3, '/uploads/experiencias/prado.png');
INSERT INTO public.experiencia_imagenes VALUES (4, 4, '/uploads/experiencias/yoga.png');
INSERT INTO public.experiencia_imagenes VALUES (5, 5, '/uploads/experiencias/kayak.png');

SELECT pg_catalog.setval('public.experiencia_imagenes_id_seq', 5, true);

-- Usuario admin
INSERT INTO public.usuarios VALUES (9, 'Valido', 'valido@gmail.com', '$2a$10$.PAtikABot7k0WvUH4FOyuXGBqmFjr6OHTiFnfoBuosLdQY/PG39y', 'ROLE_ADMIN', '2026-04-04 15:00:37.161063', false);

SELECT pg_catalog.setval('public.usuarios_id_seq', 9, true);

-- Reservas
INSERT INTO public.reservas VALUES (1, 9, 1, '2026-06-15 09:00:00', '2026-05-01 10:00:00', 2, 99.98, 'confirmada');
INSERT INTO public.reservas VALUES (2, 9, 3, '2026-06-20 11:00:00', '2026-05-02 10:00:00', 1, 35.00, 'pendiente');

SELECT pg_catalog.setval('public.reservas_id_seq', 2, true);