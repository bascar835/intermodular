package com.example.experiencias.controller;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.experiencias.db.Tx;
import com.example.experiencias.dto.CarritoItemDetalle;
import com.example.experiencias.dto.CarritoItemResponse;
import com.example.experiencias.dto.CarritoItemStoreRequest;
import com.example.experiencias.dto.CarritoItemUpdateRequest;
import com.example.experiencias.dto.CheckoutConfirmRequest;
import com.example.experiencias.dto.CheckoutItemPreview;
import com.example.experiencias.dto.CheckoutPreviewResponse;
import com.example.experiencias.dto.PagoResponse;
import com.example.experiencias.dto.ReservaResponse;
import com.example.experiencias.entity.CarritoItem;
import com.example.experiencias.entity.CheckoutPreview;
import com.example.experiencias.entity.Experiencia;
import com.example.experiencias.entity.Pago;
import com.example.experiencias.entity.Reserva;
import com.example.experiencias.exception.BadRequestException;
import com.example.experiencias.exception.BusinessException;
import com.example.experiencias.exception.ConflictException;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.CarritoItemRepository;
import com.example.experiencias.repository.CheckoutPreviewRepository;
import com.example.experiencias.repository.ExperienciaRepository;
import com.example.experiencias.repository.PagoRepository;
import com.example.experiencias.repository.ReservaRepository;
import com.example.experiencias.service.PagoSimuladoService;

import jakarta.validation.Valid;

/**
 * CarritoController — gestiona el carrito del usuario autenticado.
 *
 * Todas las rutas parten de /api/me/carrito.
 * El prefijo /me indica que las operaciones se realizan sobre el usuario en sesión.
 * El userId NUNCA se recibe del cliente: siempre viene de @SessionAttribute("userId").
 *
 * Flujo de compra:
 *   1. GET  /api/me/carrito                      → ver carrito actual
 *   2. POST /api/me/carrito                      → añadir experiencia
 *   3. PUT  /api/me/carrito/{id}                 → modificar personas
 *   4. DELETE /api/me/carrito/{id}               → eliminar item
 *   5. POST /api/me/carrito/checkout             → generar preview (snapshot de condiciones)
 *   6. POST /api/me/carrito/checkout/confirm     → confirmar compra + PAGO SIMULADO
 */
@RestController
@RequestMapping("/api/me/carrito")
public class CarritoController {

    private final DataSource ds;

    public CarritoController(DataSource ds) {
        this.ds = ds;
    }

    // ── GET /api/me/carrito ───────────────────────────────────────────────────
    @GetMapping
    public List<CarritoItemDetalle> index(@SessionAttribute("userId") int userId) {
        try (Connection con = ds.getConnection()) {
            return new CarritoItemRepository(con).findDetalleByUserId(userId);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ── POST /api/me/carrito ──────────────────────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarritoItemResponse add(
            @RequestBody @Valid CarritoItemStoreRequest req,
            @SessionAttribute("userId") int userId) {

        try (Connection con = ds.getConnection()) {

            ExperienciaRepository expRepo = new ExperienciaRepository(con);
            Experiencia exp = expRepo.findOrThrow(req.experienciaId());

            CarritoItemRepository carritoRepo = new CarritoItemRepository(con);
            CarritoItem existente = carritoRepo.findByUserIdAndExperienciaId(userId, req.experienciaId());

            if (existente != null) {
                int nuevasPersonas = existente.getPersonas() + req.personas();
                if (nuevasPersonas > 12) {
                    throw new BusinessException("No se pueden reservar más de 12 personas por experiencia");
                }
                existente.setPersonas(nuevasPersonas);
                existente.setPrecio(exp.getPrecio());
                carritoRepo.update(existente);
                return toResponse(existente);
            } else {
                if (req.personas() > 12) {
                    throw new BusinessException("No se pueden reservar más de 12 personas por experiencia");
                }
                CarritoItem ci = new CarritoItem(null, userId, req.experienciaId(), req.personas(), exp.getPrecio());
                carritoRepo.insert(ci);
                return toResponse(ci);
            }

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ── PUT /api/me/carrito/{id} ──────────────────────────────────────────────
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(
            @PathVariable("id") int id,
            @RequestBody @Valid CarritoItemUpdateRequest req,
            @SessionAttribute("userId") int userId) {

        try (Connection con = ds.getConnection()) {
            CarritoItemRepository carritoRepo = new CarritoItemRepository(con);
            ExperienciaRepository expRepo    = new ExperienciaRepository(con);

            CarritoItem item = carritoRepo.findByIdAndUserIdOrThrow(id, userId);
            Experiencia exp  = expRepo.findOrThrow(item.getExperienciaId());

            if (req.personas() > 12) {
                throw new BusinessException("No se pueden reservar más de 12 personas por experiencia");
            }

            item.setPersonas(req.personas());
            item.setPrecio(exp.getPrecio());
            carritoRepo.update(item);

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ── DELETE /api/me/carrito/{id} ───────────────────────────────────────────
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable("id") int id,
            @SessionAttribute("userId") int userId) {

        try (Connection con = ds.getConnection()) {
            new CarritoItemRepository(con).deleteByIdAndUserIdOrThrow(id, userId);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ── POST /api/me/carrito/checkout ─────────────────────────────────────────
    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutPreviewResponse preview(@SessionAttribute("userId") int userId) {
        return Tx.run(ds, con -> {

            CarritoItemRepository carritoRepo = new CarritoItemRepository(con);
            List<CarritoItemDetalle> items = carritoRepo.findDetalleByUserId(userId);

            if (items.isEmpty()) {
                throw new BadRequestException("El carrito está vacío");
            }

            List<CheckoutItemPreview> previewItems = new ArrayList<>();
            double total = 0;

            for (var item : items) {
                int    personasAnterior = item.personas();
                int    personasFinal    = item.personas();
                double precioAnterior   = item.precioCarrito();
                double precioFinal      = item.precioActual();
                boolean hayCambios      = precioAnterior != precioFinal;

                previewItems.add(new CheckoutItemPreview(
                    item.experienciaId(),
                    item.titulo(),
                    personasAnterior,
                    personasFinal,
                    precioAnterior,
                    precioFinal,
                    hayCambios
                ));

                total += personasFinal * precioFinal;
            }

            String data = previewItems.stream()
                .map(i -> i.experienciaId() + ":" + i.personasFinal() + ":" + String.format("%.2f", i.precioFinal()))
                .sorted()
                .collect(Collectors.joining("|"));

            CheckoutPreviewRepository previewRepo = new CheckoutPreviewRepository(con);
            CheckoutPreview preview = new CheckoutPreview(null, userId, data, LocalDateTime.now());
            previewRepo.insert(preview);

            return new CheckoutPreviewResponse(preview.getId(), previewItems, total);
        });
    }

    // ── POST /api/me/carrito/checkout/confirm ─────────────────────────────────
    /**
     * Confirma la compra con pago simulado.
     *
     * Pasos:
     *  1. Recuperar y verificar el preview.
     *  2. Comparar estado actual del carrito con el snapshot del preview.
     *  3. Crear una Reserva (estado "pendiente") por cada experiencia.
     *  4. Invocar PagoSimuladoService → genera referencia, persiste pago,
     *     y cambia todas las reservas a "confirmada".
     *  5. Vaciar carrito y previews del usuario.
     *  6. Devolver PagoResponse con la referencia y las reservas confirmadas.
     *
     * Body esperado:
     * {
     *   "previewId": 5,
     *   "metodoPago": "tarjeta",          // "tarjeta" | "transferencia" | "paypal"
     *   "fechas": [
     *     { "experienciaId": 3, "fecha": "2026-06-15T10:00" }
     *   ]
     * }
     */
    @PostMapping("/checkout/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public PagoResponse confirm(
            @RequestBody @Valid CheckoutConfirmRequest req,
            @SessionAttribute("userId") int userId) {

        return Tx.run(ds, con -> {

            // 1. Recuperar el preview
            CheckoutPreviewRepository previewRepo = new CheckoutPreviewRepository(con);
            CheckoutPreview preview = previewRepo.findByIdAndUserIdOrThrow(req.previewId(), userId);
            String stored = preview.getData();

            // 2. Estado actual del carrito
            CarritoItemRepository carritoRepo = new CarritoItemRepository(con);
            List<CarritoItemDetalle> items = carritoRepo.findDetalleByUserId(userId);

            if (items.isEmpty()) {
                throw new BadRequestException("El carrito está vacío");
            }

            String current = items.stream()
                .map(i -> i.experienciaId() + ":" + i.personas() + ":" + String.format("%.2f", i.precioActual()))
                .sorted()
                .collect(Collectors.joining("|"));

            if (!stored.equals(current)) {
                throw new ConflictException(
                    "Las condiciones han cambiado desde el checkout. Por favor, revisa el carrito antes de confirmar.");
            }

            // 3. Mapa de fechas elegidas
            Map<Integer, LocalDateTime> fechasPorExp = new java.util.HashMap<>();
            if (req.fechas() != null) {
                for (var fi : req.fechas()) {
                    try {
                        fechasPorExp.put(fi.experienciaId(), LocalDateTime.parse(fi.fecha()));
                    } catch (DateTimeParseException ignored) {}
                }
            }

            // 4. Crear reservas en estado "pendiente"
            ReservaRepository reservaRepo = new ReservaRepository(con);
            List<ReservaResponse> reservasResp = new ArrayList<>();
            List<Integer> reservaIds = new ArrayList<>();
            LocalDateTime ahora = LocalDateTime.now();
            double importeTotal = 0;

            for (var item : items) {
                double total = item.personas() * item.precioActual();
                importeTotal += total;
                LocalDateTime fechaActividad = fechasPorExp.getOrDefault(
                    item.experienciaId(), ahora.plusDays(1));

                Reserva reserva = new Reserva(
                    null,
                    userId,
                    item.experienciaId(),
                    fechaActividad,
                    item.personas(),
                    total,
                    "pendiente"
                );
                reservaRepo.insert(reserva);
                reservaIds.add(reserva.getId());

                reservasResp.add(new ReservaResponse(
                    reserva.getId(),
                    item.experienciaId(),
                    fechaActividad,
                    item.personas(),
                    total,
                    "pendiente",   // se actualizará a "confirmada" tras el pago
                    ahora
                ));
            }

            // 5. Procesar pago simulado → confirma reservas y guarda registro de pago
            PagoRepository pagoRepo = new PagoRepository(con);
            PagoSimuladoService pagoService = new PagoSimuladoService(pagoRepo, reservaRepo);
            Pago pago = pagoService.procesarPago(userId, req.metodoPago(), reservaIds, importeTotal);

            // Actualizar el estado en la respuesta de reservas a "confirmada"
            List<ReservaResponse> reservasConfirmadas = reservasResp.stream()
                .map(r -> new ReservaResponse(
                    r.id(), r.experienciaId(), r.fechaReserva(),
                    r.numeroPersonas(), r.precioTotal(), "confirmada", r.fechaCreacion()))
                .toList();

            // 6. Limpiar carrito y previews del usuario
            carritoRepo.deleteByUserId(userId);
            previewRepo.deleteByUserId(userId);

            return new PagoResponse(
                pago.getId(),
                pago.getReferencia(),
                pago.getMetodoPago(),
                pago.getEstadoPago(),
                pago.getImporteTotal(),
                pago.getFechaPago(),
                reservasConfirmadas
            );
        });
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private CarritoItemResponse toResponse(CarritoItem ci) {
        return new CarritoItemResponse(ci.getId(), ci.getExperienciaId(), ci.getPersonas(), ci.getPrecio());
    }
}
