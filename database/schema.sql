-- =========================
-- TIPOS ENUM
-- =========================
CREATE TYPE rol_usuario AS ENUM ('ROLE_ADMIN', 'ROLE_USER');

CREATE TYPE estado_reserva AS ENUM ('pendiente', 'confirmada', 'cancelada');


-- =========================
-- TABLA: categorias
-- =========================
CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion TEXT
);


-- =========================
-- TABLA: usuarios
-- =========================
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol rol_usuario NOT NULL DEFAULT 'ROLE_USER',
    fecha_registro TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Email único case-insensitive
CREATE UNIQUE INDEX ux_usuarios_email_lower
ON usuarios (LOWER(email));


-- =========================
-- TABLA: experiencias
-- =========================
CREATE TABLE experiencias (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio NUMERIC(10,2) NOT NULL,
    ubicacion VARCHAR(200),
    duracion_horas NUMERIC(4,2),
    categoria_id BIGINT NULL,
    fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_experiencia_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categorias(id)
        ON DELETE SET NULL,  -- Si borras la categoría, la experiencia sigue existiendo

    CONSTRAINT chk_precio_positivo CHECK (precio > 0),
    CONSTRAINT chk_duracion_positiva CHECK (duracion_horas IS NULL OR duracion_horas > 0)
);

CREATE INDEX idx_experiencias_categoria
ON experiencias(categoria_id);


-- =========================
-- TABLA: reservas
-- =========================
CREATE TABLE reservas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    experiencia_id BIGINT NOT NULL,
    fecha_reserva TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    fecha_creacion TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    numero_personas INTEGER NOT NULL,
    precio_total NUMERIC(10,2) NOT NULL,
    estado estado_reserva NOT NULL DEFAULT 'pendiente',

    -- CORREGIDO: RESTRICT en lugar de CASCADE.
    -- Impide borrar un usuario o experiencia si tiene reservas activas,
    -- evitando pérdida accidental de datos de negocio.
    -- Para borrar un usuario/experiencia, primero cancelar o eliminar sus reservas.
    CONSTRAINT fk_reserva_usuario
        FOREIGN KEY (usuario_id)
        REFERENCES usuarios(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_reserva_experiencia
        FOREIGN KEY (experiencia_id)
        REFERENCES experiencias(id)
        ON DELETE RESTRICT,

    CONSTRAINT chk_personas CHECK (numero_personas > 0),
    CONSTRAINT chk_precio_total CHECK (precio_total >= 0)
);

CREATE INDEX idx_reservas_usuario
ON reservas(usuario_id);

CREATE INDEX idx_reservas_experiencia
ON reservas(experiencia_id);


ALTER TABLE categorias
    ADD COLUMN IF NOT EXISTS imagen_url VARCHAR(500);

ALTER TABLE experiencias
    ADD COLUMN IF NOT EXISTS imagen_url VARCHAR(500);