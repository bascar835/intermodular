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
import com.example.experiencias.dto.ReservaResponse;
import com.example.experiencias.entity.CarritoItem;
import com.example.experiencias.entity.CheckoutPreview;
import com.example.experiencias.entity.Experiencia;
import com.example.experiencias.entity.Reserva;
import com.example.experiencias.exception.BadRequestException;
import com.example.experiencias.exception.BusinessException;
import com.example.experiencias.exception.ConflictException;
import com.example.experiencias.exception.DataAccessException;
import com.example.experiencias.repository.CarritoItemRepository;
import com.example.experiencias.repository.CheckoutPreviewRepository;
import com.example.experiencias.repository.ExperienciaRepository;
import com.example.experiencias.repository.ReservaRepository;

import jakarta.validation.Valid;

/**
 * CarritoController — gestiona el carrito del usuario autenticado.
 *
 * Todas las rutas parten de /api/me/carrito.
 * El prefijo /me indica que las operaciones se realizan sobre el usuario en sesión.
 * El userId NUNCA se recibe del cliente: siempre viene de @SessionAttribute("userId").
 *
 * Flujo de compra:
 *   1. GET  /api/me/carrito              → ver carrito actual
 *   2. POST /api/me/carrito              → añadir experiencia
 *   3. PUT  /api/me/carrito/{id}         → modificar personas
 *   4. DELETE /api/me/carrito/{id}       → eliminar item
 *   5. POST /api/me/carrito/checkout     → generar preview (snapshot de condiciones)
 *   6. POST /api/me/carrito/checkout/confirm → confirmar compra con el previewId
 */
@RestController
@RequestMapping("/api/me/carrito")
public class CarritoController {

    private final DataSource ds;

    public CarritoController(DataSource ds) {
        this.ds = ds;
    }

    // ── GET /api/me/carrito ───────────────────────────────────────────────────
    // Devuelve los items del carrito con precio actual y snapshot para mostrar cambios
    @GetMapping
    public List<CarritoItemDetalle> index(@SessionAttribute("userId") int userId) {
        try (Connection con = ds.getConnection()) {
            return new CarritoItemRepository(con).findDetalleByUserId(userId);
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ── POST /api/me/carrito ──────────────────────────────────────────────────
    // Añade una experiencia al carrito. Si ya existe, acumula personas.
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
                // Ya existe → acumular personas (máx. 12)
                int nuevasPersonas = existente.getPersonas() + req.personas();
                if (nuevasPersonas > 12) {
                    throw new BusinessException("No se pueden reservar más de 12 personas por experiencia");
                }
                existente.setPersonas(nuevasPersonas);
                existente.setPrecio(exp.getPrecio()); // refresca snapshot de precio
                carritoRepo.update(existente);
                return toResponse(existente);
            } else {
                // Nueva línea
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
    // Modifica el número de personas de un item del carrito
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
            item.setPrecio(exp.getPrecio()); // refresca snapshot
            carritoRepo.update(item);

        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // ── DELETE /api/me/carrito/{id} ───────────────────────────────────────────
    // Elimina un item del carrito del usuario
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
    // Genera un checkout preview: snapshot de las condiciones actuales.
    // Detecta cambios de precio respecto al carrito e informa al usuario.
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
                int    personasFinal    = item.personas(); // sin stock, no se limita
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

            // Serializar el estado actual para compararlo en la confirmación
            // Formato: experienciaId:personas:precio|experienciaId:personas:precio
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
    // Confirma la compra. Verifica que el estado no ha cambiado desde el preview
    // y crea una Reserva por cada experiencia del carrito.
    @PostMapping("/checkout/confirm")
    @ResponseStatus(HttpStatus.CREATED)
    public List<ReservaResponse> confirm(
            @RequestBody @Valid CheckoutConfirmRequest req,
            @SessionAttribute("userId") int userId) {

        return Tx.run(ds, con -> {

            // 1. Recuperar el preview (solo del usuario en sesión)
            CheckoutPreviewRepository previewRepo = new CheckoutPreviewRepository(con);
            CheckoutPreview preview = previewRepo.findByIdAndUserIdOrThrow(req.previewId(), userId);
            String stored = preview.getData();

            // 2. Cargar el estado actual del carrito
            CarritoItemRepository carritoRepo = new CarritoItemRepository(con);
            List<CarritoItemDetalle> items = carritoRepo.findDetalleByUserId(userId);

            if (items.isEmpty()) {
                throw new BadRequestException("El carrito está vacío");
            }

            // 3. Reconstruir el estado actual y comparar con el preview
            String current = items.stream()
                .map(i -> i.experienciaId() + ":" + i.personas() + ":" + String.format("%.2f", i.precioActual()))
                .sorted()
                .collect(Collectors.joining("|"));

            if (!stored.equals(current)) {
                throw new ConflictException(
                    "Las condiciones han cambiado desde el checkout. Por favor, revisa el carrito antes de confirmar.");
            }

            // 4. Construir mapa experienciaId → fecha elegida por el usuario
            Map<Integer, LocalDateTime> fechasPorExp = new java.util.HashMap<>();
            if (req.fechas() != null) {
                for (var fi : req.fechas()) {
                    try {
                        fechasPorExp.put(fi.experienciaId(), LocalDateTime.parse(fi.fecha()));
                    } catch (DateTimeParseException ignored) {}
                }
            }

            // 5. Crear una Reserva por cada experiencia del carrito
            ReservaRepository reservaRepo = new ReservaRepository(con);
            List<ReservaResponse> reservas = new ArrayList<>();
            LocalDateTime ahora = LocalDateTime.now();

            for (var item : items) {
                double total = item.personas() * item.precioActual();
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

                reservas.add(new ReservaResponse(
                    reserva.getId(),
                    item.experienciaId(),
                    fechaActividad,
                    item.personas(),
                    total,
                    "pendiente",
                    ahora
                ));
            }

            // 6. Limpiar carrito y previews del usuario
            carritoRepo.deleteByUserId(userId);
            previewRepo.deleteByUserId(userId);

            return reservas;
        });
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private CarritoItemResponse toResponse(CarritoItem ci) {
        return new CarritoItemResponse(ci.getId(), ci.getExperienciaId(), ci.getPersonas(), ci.getPrecio());
    }
}
