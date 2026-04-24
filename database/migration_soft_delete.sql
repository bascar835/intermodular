-- Migración: añadir soft delete a la tabla usuarios
-- Ejecutar una sola vez en la base de datos

-- 1. Añadir columna deleted (soft delete)
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS deleted BOOLEAN DEFAULT FALSE;

-- 2. Asegurarse de que todos los registros existentes tienen deleted = false
UPDATE usuarios SET deleted = FALSE WHERE deleted IS NULL;

-- NOTA: La foreign key reservas.usuario_id -> usuarios.id ya existe en el schema
-- como fk_reserva_usuario. No es necesario volver a crearla.
-- El ALTER TABLE reservas ADD CONSTRAINT fk_usuario solicitado es equivalente
-- a la restricción existente fk_reserva_usuario.
