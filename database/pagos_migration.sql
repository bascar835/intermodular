-- ============================================================
-- Migración: tabla de pagos simulados
-- Ejecutar una sola vez sobre la base de datos existente.
-- ============================================================

CREATE TABLE IF NOT EXISTS public.pagos (
    id            BIGSERIAL       PRIMARY KEY,
    usuario_id    BIGINT          NOT NULL REFERENCES public.usuarios(id) ON DELETE CASCADE,
    referencia    VARCHAR(30)     NOT NULL UNIQUE,          -- ej. PAY-20260520-AB3F
    metodo_pago   VARCHAR(20)     NOT NULL                  -- tarjeta | transferencia | paypal
                  CHECK (metodo_pago IN ('tarjeta', 'transferencia', 'paypal')),
    estado_pago   VARCHAR(20)     NOT NULL DEFAULT 'completado',
    importe_total NUMERIC(10, 2)  NOT NULL CHECK (importe_total >= 0),
    fecha_pago    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índice para consultas por usuario
CREATE INDEX IF NOT EXISTS idx_pagos_usuario ON public.pagos(usuario_id);

-- Comentarios descriptivos
COMMENT ON TABLE  public.pagos               IS 'Registro de pagos simulados asociados a reservas de experiencias';
COMMENT ON COLUMN public.pagos.referencia    IS 'Código único generado internamente con formato PAY-YYYYMMDD-XXXX';
COMMENT ON COLUMN public.pagos.metodo_pago   IS 'Método elegido por el usuario: tarjeta, transferencia o paypal';
COMMENT ON COLUMN public.pagos.estado_pago   IS 'Estado de la transacción simulada (siempre completado en demo)';
