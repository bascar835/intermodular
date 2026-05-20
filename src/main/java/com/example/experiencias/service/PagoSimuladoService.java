package com.example.experiencias.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import com.example.experiencias.entity.Pago;
import com.example.experiencias.exception.BadRequestException;
import com.example.experiencias.repository.PagoRepository;
import com.example.experiencias.repository.ReservaRepository;

/**
 * PagoSimuladoService — lógica de la pasarela de pago simulada.
 *
 * Simula el cobro sin llamar a ninguna API externa:
 *  1. Valida el método de pago recibido.
 *  2. Genera una referencia única con el formato PAY-YYYYMMDD-XXXX.
 *  3. Persiste el registro en la tabla "pagos".
 *  4. Cambia el estado de todas las reservas indicadas a "confirmada".
 *
 * Métodos de pago aceptados: tarjeta, transferencia, paypal.
 */
public class PagoSimuladoService {

    private static final List<String> METODOS_VALIDOS =
        List.of("tarjeta", "transferencia", "paypal");

    private final PagoRepository    pagoRepo;
    private final ReservaRepository reservaRepo;

    public PagoSimuladoService(PagoRepository pagoRepo, ReservaRepository reservaRepo) {
        this.pagoRepo    = pagoRepo;
        this.reservaRepo = reservaRepo;
    }

    /**
     * Ejecuta la simulación de pago para las reservas dadas.
     *
     * @param usuarioId   ID del usuario en sesión
     * @param metodoPago  Método elegido por el usuario
     * @param reservaIds  IDs de las reservas a confirmar
     * @param importeTotal Importe total del pedido
     * @return el objeto Pago persistido con su id y referencia
     */
    public Pago procesarPago(int usuarioId, String metodoPago,
                             List<Integer> reservaIds, double importeTotal) {

        // 1. Validar método
        if (!METODOS_VALIDOS.contains(metodoPago.toLowerCase())) {
            throw new BadRequestException(
                "Método de pago no válido. Opciones: tarjeta, transferencia, paypal.");
        }

        LocalDateTime ahora = LocalDateTime.now();

        // 2. Generar referencia única: PAY-20260520-AB3F
        String fecha = ahora.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sufijo = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        String referencia = "PAY-" + fecha + "-" + sufijo;

        // 3. Persistir el pago
        Pago pago = new Pago(null, usuarioId, referencia,
                             metodoPago.toLowerCase(), "completado",
                             importeTotal, ahora);
        pagoRepo.insert(pago);

        // 4. Confirmar reservas (cambiar estado pendiente → confirmada)
        for (int reservaId : reservaIds) {
            var reserva = reservaRepo.findOrThrow(reservaId);
            reserva.setEstado("confirmada");
            reservaRepo.update(reserva);
        }

        return pago;
    }
}
