-- ══════════════════════════════════════════════════════════════
-- IMÁGENES DE PRUEBA — Xperiabox
-- URLs de Unsplash (acceso libre, sin API key necesaria)
-- Ejecutar DESPUÉS de datos1.sql si las imágenes no existen ya
-- ══════════════════════════════════════════════════════════════

-- ── Imágenes de Categorías (imagen_url en tabla categorias) ──────────────────
UPDATE public.categorias SET imagen_url = 'https://images.unsplash.com/photo-1551632811-561732d1e306?w=800&q=80'
WHERE id = 1;  -- Aventura: senderismo montaña

UPDATE public.categorias SET imagen_url = 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=800&q=80'
WHERE id = 2;  -- Gastronomía: platos coloridos

UPDATE public.categorias SET imagen_url = 'https://images.unsplash.com/photo-1554907984-15263bfd63bd?w=800&q=80'
WHERE id = 3;  -- Cultura: museo/arte

UPDATE public.categorias SET imagen_url = 'https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=800&q=80'
WHERE id = 4;  -- Relax: spa/bienestar


-- ── Imágenes de Experiencias ──────────────────────────────────────────────────
-- Borrar las existentes (rutas locales que no cargan) y poner URLs reales
DELETE FROM public.experiencia_imagenes WHERE experiencia_id IN (1,2,3,4,5);

-- Experiencia 1 — Senderismo en la Sierra Nevada
INSERT INTO public.experiencia_imagenes (experiencia_id, url) VALUES
(1, 'https://images.unsplash.com/photo-1551632811-561732d1e306?w=1200&q=80'),
(1, 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=1200&q=80'),
(1, 'https://images.unsplash.com/photo-1530866495561-507c9faab2ed?w=1200&q=80');

-- Experiencia 2 — Taller de cocina mediterránea
INSERT INTO public.experiencia_imagenes (experiencia_id, url) VALUES
(2, 'https://images.unsplash.com/photo-1556910103-1c02745aae4d?w=1200&q=80'),
(2, 'https://images.unsplash.com/photo-1466637574441-749b8f19452f?w=1200&q=80'),
(2, 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?w=1200&q=80');

-- Experiencia 3 — Visita guiada al Museo del Prado
INSERT INTO public.experiencia_imagenes (experiencia_id, url) VALUES
(3, 'https://images.unsplash.com/photo-1554907984-15263bfd63bd?w=1200&q=80'),
(3, 'https://images.unsplash.com/photo-1518998053901-5348d3961a04?w=1200&q=80'),
(3, 'https://images.unsplash.com/photo-1574182245530-967d9b3831af?w=1200&q=80');

-- Experiencia 4 — Sesión de yoga en la playa
INSERT INTO public.experiencia_imagenes (experiencia_id, url) VALUES
(4, 'https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=1200&q=80'),
(4, 'https://images.unsplash.com/photo-1544161515-4ab6ce6db874?w=1200&q=80'),
(4, 'https://images.unsplash.com/photo-1545389336-cf090694435e?w=1200&q=80');

-- Experiencia 5 — Kayak en el Mediterráneo
INSERT INTO public.experiencia_imagenes (experiencia_id, url) VALUES
(5, 'https://images.unsplash.com/photo-1530870110042-98b2cb110834?w=1200&q=80'),
(5, 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?w=1200&q=80'),
(5, 'https://images.unsplash.com/photo-1559827260-dc66d52bef19?w=1200&q=80');

-- Actualizar secuencia
SELECT pg_catalog.setval('public.experiencia_imagenes_id_seq',
    (SELECT MAX(id) FROM public.experiencia_imagenes), true);

