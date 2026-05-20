/**
 * carrito.js — Carrito con backend real
 *
 * El carrito NO se guarda en sessionStorage.
 * Todas las operaciones van al servidor: POST/PUT/DELETE /api/me/carrito
 * El estado siempre se lee desde la API, garantizando consistencia.
 *
 * Flujo:
 *   show.js → addToCart()      → POST /api/me/carrito
 *   carrito.html               → GET  /api/me/carrito  (render)
 *   Botón Finalizar compra     → POST /api/me/carrito/checkout (preview)
 *   Modal confirmar            → POST /api/me/carrito/checkout/confirm
 */

// ── Init ──────────────────────────────────────────────────────────────────────
document.addEventListener("DOMContentLoaded", function () {
    mostrarCarrito();
});

// ── API pública — llamada desde show.js ───────────────────────────────────────

/**
 * Añade una experiencia al carrito vía API.
 * Si el usuario no está logueado, redirige al login.
 */
async function addToCart(experienciaId, titulo, precio, personas) {
    const res = await fetch("/api/me/carrito", {
        method:  "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ experienciaId, personas })
    });

    if (res.status === 401) {
        // No logueado: guardar intención y redirigir al login
        sessionStorage.setItem("pendingCart", JSON.stringify({ experienciaId, titulo, precio, personas }));
        window.location.href = "/experiencias/login.html";
        return;
    }

    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        alert(err.message || "Error al añadir al carrito");
    }
    // El feedback visual lo gestiona show.js
    // Actualizar badge en esta página y notificar otras pestañas
    if (typeof actualizarCarritoBadge === "function") actualizarCarritoBadge();
}

// ── Render del carrito ────────────────────────────────────────────────────────

async function mostrarCarrito() {
    const emptyCart    = document.getElementById("emptyCart");
    const cartProducts = document.getElementById("cartProducts");
    const productsList = document.getElementById("productsList");
    const totalCarrito = document.getElementById("totalCarrito");

    if (!emptyCart || !cartProducts || !productsList) return;

    // Mostrar spinner mientras cargamos
    productsList.innerHTML = '<p style="color:#888;padding:20px 0;">Cargando carrito...</p>';
    cartProducts.style.display = "block";
    emptyCart.style.display    = "none";

    const res = await fetch("/api/me/carrito");

    if (res.status === 401) {
        // No logueado
        emptyCart.style.display    = "block";
        cartProducts.style.display = "none";
        return;
    }

    if (!res.ok) {
        productsList.innerHTML = '<p style="color:#c00;">Error cargando el carrito.</p>';
        return;
    }

    const items = await res.json();
    productsList.innerHTML = "";

    if (items.length === 0) {
        emptyCart.style.display    = "block";
        cartProducts.style.display = "none";
        if (typeof actualizarCarritoBadge === "function") actualizarCarritoBadge();
        return;
    }

    emptyCart.style.display    = "none";
    cartProducts.style.display = "block";

    let total = 0;
    let hayCambios = false;

    items.forEach(item => {
        const subtotal       = item.precioActual * item.personas;
        const precioDistinto = Math.abs(item.precioCarrito - item.precioActual) > 0.001;
        total += subtotal;
        if (precioDistinto) hayCambios = true;

        const div = document.createElement("div");
        div.className = "cart-product";
        div.innerHTML = `
            <div class="cart-product-info">
                <h3>${esc(item.titulo)}</h3>
                ${precioDistinto ? `
                  <p class="precio-cambio">
                    ⚠ Precio actualizado: <s>${fmt(item.precioCarrito)}</s> → <strong>${fmt(item.precioActual)}</strong> / persona
                  </p>` : `
                  <p>${fmt(item.precioActual)} / persona</p>`
                }
                <div class="cart-personas-control">
                    <label>Personas:</label>
                    <button class="btn-persona-menos" data-id="${item.id}">−</button>
                    <span class="cart-personas-num">${item.personas}</span>
                    <button class="btn-persona-mas"  data-id="${item.id}" data-personas="${item.personas}">+</button>
                </div>
                <p class="cart-subtotal">Subtotal: ${fmt(subtotal)}</p>
            </div>
            <button class="btn-delete" data-id="${item.id}">🗑 Eliminar</button>
        `;
        productsList.appendChild(div);
    });

    // Botón vaciar (único, generado por JS)
    const btnVaciar = document.createElement("button");
    btnVaciar.className = "btn-vaciar";
    btnVaciar.innerHTML = "<span style=\'font-size:1rem;\'>🗑️</span> Vaciar carrito";
    btnVaciar.onclick   = vaciarCarrito;
    // Actualizar badge del navbar
    if (typeof actualizarCarritoBadge === "function") actualizarCarritoBadge();
    productsList.appendChild(btnVaciar);

    // Aviso de cambios de precio
    if (hayCambios) {
        const aviso = document.createElement("div");
        aviso.style.cssText = "background:#fff3cd;border:1px solid #ffc107;border-radius:8px;padding:10px 14px;margin:12px 0;font-size:.85rem;color:#856404;";
        aviso.textContent   = "⚠ Algunos precios han cambiado desde que añadiste los artículos al carrito. El total refleja los precios actuales.";
        productsList.prepend(aviso);
    }

    // Total y botón de checkout
    if (totalCarrito) {
        totalCarrito.innerHTML =
            `${items.length} experiencia${items.length !== 1 ? "s" : ""} · Total: <strong>${fmt(total)}</strong>`;
    }

    // Eventos
    productsList.querySelectorAll(".btn-delete").forEach(btn =>
        btn.addEventListener("click", () => eliminarItem(parseInt(btn.dataset.id)))
    );
    productsList.querySelectorAll(".btn-persona-menos").forEach(btn =>
        btn.addEventListener("click", () => cambiarPersonas(parseInt(btn.dataset.id), -1, btn))
    );
    productsList.querySelectorAll(".btn-persona-mas").forEach(btn =>
        btn.addEventListener("click", () => cambiarPersonas(parseInt(btn.dataset.id), +1, btn))
    );
}

// ── Acciones ──────────────────────────────────────────────────────────────────

async function eliminarItem(id) {
    const res = await fetch(`/api/me/carrito/${id}`, { method: "DELETE" });
    if (res.ok || res.status === 204) {
        mostrarCarrito();
    } else {
        alert("Error al eliminar el artículo");
    }
}

async function cambiarPersonas(id, delta, btn) {
    // Leer personas actuales del DOM
    const numEl   = btn.closest(".cart-product-info").querySelector(".cart-personas-num");
    const actual  = parseInt(numEl.textContent);
    const nuevas  = actual + delta;
    if (nuevas < 1 || nuevas > 12) return;

    const res = await fetch(`/api/me/carrito/${id}`, {
        method:  "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ personas: nuevas })
    });

    if (res.ok || res.status === 204) {
        mostrarCarrito();
    } else {
        const err = await res.json().catch(() => ({}));
        alert(err.message || "Error al actualizar");
    }
}

async function vaciarCarrito() {
    if (!confirm("¿Vaciar todo el carrito?")) return;
    // No hay endpoint de vaciar todo — eliminamos item a item
    const res  = await fetch("/api/me/carrito");
    const items = await res.json();
    await Promise.all(items.map(i => fetch(`/api/me/carrito/${i.id}`, { method: "DELETE" })));
    mostrarCarrito();
}

// ── Checkout — modal paso a paso ──────────────────────────────────────────────

let _previewId   = null;
let _pasoActual  = 0;
let _reservasPendientes = [];
let _totalPedido = 0;

async function abrirModalCompra() {
    const btnFin = document.getElementById("btn-finalizar");
    if (btnFin) { btnFin.disabled = true; btnFin.textContent = "Procesando..."; }

    const res = await fetch("/api/me/carrito/checkout", { method: "POST" });

    if (btnFin) { btnFin.disabled = false; btnFin.textContent = "💳 Finalizar compra"; }

    if (res.status === 401) { window.location.href = "/experiencias/login.html"; return; }

    if (!res.ok) {
        const err = await res.json().catch(() => ({}));
        alert(err.message || "Error al iniciar el checkout");
        return;
    }

    const preview = await res.json();
    _previewId  = preview.previewId;
    _pasoActual = 0;
    _totalPedido = preview.total || 0;
    _reservasPendientes = preview.items.map(i => ({ ...i, fecha: null }));

    if (preview.items.some(i => i.hayCambios)) {
        const ok = confirm(
            "⚠ Algunos precios han cambiado desde que añadiste las experiencias al carrito.\n" +
            "¿Deseas continuar con los precios actualizados?"
        );
        if (!ok) { _previewId = null; return; }
    }

    mostrarPasoModal(_pasoActual);
    document.getElementById("modal-compra").style.display = "flex";
}

function mostrarPasoModal(index) {
    const item  = _reservasPendientes[index];
    const total = _reservasPendientes.length;

    document.getElementById("modal-paso-label").textContent  = `Experiencia ${index + 1} de ${total}`;
    document.getElementById("modal-nombre-exp").textContent  = item.titulo;
    document.getElementById("modal-precio-exp").textContent  =
        `${fmt(item.precioFinal)} × ${item.personasFinal} persona${item.personasFinal !== 1 ? "s" : ""} = ${fmt(item.precioFinal * item.personasFinal)}`;

    const manana = new Date();
    manana.setDate(manana.getDate() + 1);
    const minFecha = manana.toISOString().slice(0, 16);
    const inputFecha = document.getElementById("input-fecha");
    inputFecha.min   = minFecha;
    inputFecha.value = item.fecha || minFecha;

    document.getElementById("btn-siguiente").textContent =
        index < total - 1 ? "Siguiente →" : "Ir al pago →";

    document.getElementById("modal-progreso-bar").style.width =
        Math.round((index / total) * 100) + "%";
    document.getElementById("modal-compra-error").style.display = "none";
}

async function siguientePaso() {
    const fecha   = document.getElementById("input-fecha").value;
    const errorEl = document.getElementById("modal-compra-error");

    if (!fecha) {
        errorEl.textContent   = "Por favor selecciona una fecha y hora para la actividad.";
        errorEl.style.display = "block";
        return;
    }

    // Validar que la fecha no sea anterior a ahora mismo
    const fechaSeleccionada = new Date(fecha);
    const ahora = new Date();
    if (fechaSeleccionada <= ahora) {
        errorEl.textContent   = "La fecha y hora seleccionada debe ser posterior a la fecha actual.";
        errorEl.style.display = "block";
        return;
    }

    _reservasPendientes[_pasoActual].fecha = fecha;

    if (_pasoActual < _reservasPendientes.length - 1) {
        _pasoActual++;
        mostrarPasoModal(_pasoActual);
    } else {
        // Todas las fechas elegidas → abrir pasarela de pago
        cerrarModalCompra();
        abrirModalPago();
    }
}

function cerrarModalCompra() {
    document.getElementById("modal-compra").style.display = "none";
}

// ── Pasarela de pago ──────────────────────────────────────────────────────────

function abrirModalPago() {
    // Rellenar resumen del pedido
    const resumenEl = document.getElementById("pago-resumen");
    if (resumenEl) {
        const lineas = _reservasPendientes.map(i =>
            `<div style="display:flex;justify-content:space-between;margin-bottom:4px;">
                <span>${esc(i.titulo)} × ${i.personasFinal} persona${i.personasFinal !== 1 ? "s" : ""}</span>
                <strong>${fmt(i.precioFinal * i.personasFinal)}</strong>
             </div>`
        ).join("");
        resumenEl.innerHTML = lineas +
            `<div style="border-top:1px solid #e5e5e5;margin-top:8px;padding-top:8px;display:flex;justify-content:space-between;font-weight:800;color:#1a1a2e;font-size:14px;">
                <span>Total a pagar</span>
                <span style="color:#e11d48;">${fmt(_totalPedido)}</span>
             </div>`;
    }
    // Limpiar errores y campos
    document.getElementById("pago-error").style.display = "none";
    document.getElementById("modal-pago").style.display = "flex";
}

function cerrarModalPago() {
    document.getElementById("modal-pago").style.display = "none";
    // Reabrir el último paso de fechas
    mostrarPasoModal(_reservasPendientes.length - 1);
    document.getElementById("modal-compra").style.display = "flex";
}

async function procesarPago() {
    const metodo = document.querySelector('input[name="metodoPago"]:checked')?.value || "tarjeta";
    const errorEl = document.getElementById("pago-error");
    errorEl.style.display = "none";

    // Validar tarjeta si corresponde
    if (metodo === "tarjeta") {
        const numero  = document.getElementById("inp-numero").value.replace(/\s/g, "");
        const titular = document.getElementById("inp-titular").value.trim();
        const expiry  = document.getElementById("inp-expiry").value;
        const cvv     = document.getElementById("inp-cvv").value;

        if (numero.length < 16) {
            mostrarErrorPago("El número de tarjeta no es válido."); return;
        }
        if (!titular) {
            mostrarErrorPago("Introduce el nombre del titular."); return;
        }
        if (!/^\d{2}\/\d{2}$/.test(expiry)) {
            mostrarErrorPago("La fecha de caducidad no es válida (MM/AA)."); return;
        }
        {
            const [mm, aa] = expiry.split("/").map(Number);
            const now = new Date();
            const currentYear  = now.getFullYear() % 100;
            const currentMonth = now.getMonth() + 1;
            if (mm < 1 || mm > 12) {
                mostrarErrorPago("El mes de caducidad no es válido (01-12)."); return;
            }
            if (aa < currentYear || (aa === currentYear && mm < currentMonth)) {
                mostrarErrorPago("La tarjeta está caducada. Introduce una fecha válida."); return;
            }
        }
        if (cvv.length < 3) {
            mostrarErrorPago("El CVV debe tener 3 dígitos."); return;
        }
    }

    // Cerrar modal pago y mostrar procesando
    document.getElementById("modal-pago").style.display = "none";
    document.getElementById("modal-procesando").style.display = "flex";

    // Animación de mensajes simulados
    const mensajes = [
        "Verificando datos de la tarjeta...",
        "Contactando con el banco...",
        "Aplicando autenticación 3D Secure...",
        "Confirmando el pago..."
    ];
    let msgIdx = 0;
    const msgEl = document.getElementById("procesando-msg");
    const intervalo = setInterval(() => {
        msgIdx = (msgIdx + 1) % mensajes.length;
        if (msgEl) msgEl.textContent = mensajes[msgIdx];
    }, 900);

    // Esperar 3.5 segundos para que se vea la animación
    await new Promise(r => setTimeout(r, 3500));
    clearInterval(intervalo);

    // Llamar al backend
    const fechas = _reservasPendientes.map(item => ({
        experienciaId: item.experienciaId,
        fecha: item.fecha
    }));

    const res = await fetch("/api/me/carrito/checkout/confirm", {
        method:  "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ previewId: _previewId, fechas, metodoPago: metodo })
    });

    document.getElementById("modal-procesando").style.display = "none";

    if (res.ok) {
        const pago = await res.json().catch(() => null);
        try {
            const meRes = await fetch('/api/auth/me', { credentials: 'include' });
            const user  = meRes.ok ? await meRes.json() : null;
            mostrarModalConfirmacion(user, pago);
        } catch (_) {
            mostrarModalConfirmacion(null, pago);
        }
    } else if (res.status === 409) {
        mostrarCarrito();
        alert("Las condiciones han cambiado. Por favor revisa el carrito antes de confirmar.");
    } else {
        // Volver al modal de pago con el error
        document.getElementById("modal-pago").style.display = "flex";
        const err = await res.json().catch(() => ({}));
        mostrarErrorPago(err.message || "Error al confirmar el pago. Inténtalo de nuevo.");
    }
}

function mostrarErrorPago(msg) {
    const el = document.getElementById("pago-error");
    el.textContent   = "⚠ " + msg;
    el.style.display = "block";
}

window.addEventListener("click", e => {
    const modal = document.getElementById("modal-compra");
    if (modal && e.target === modal) cerrarModalCompra();
});


// ── Modal de confirmación de compra ──────────────────────────────────────────

function mostrarModalConfirmacion(user, pago) {
    // Cerrar modal de compra si sigue abierto
    cerrarModalCompra();

    const nombre = user ? (user.name || user.nombre || 'Cliente') : 'Cliente';
    const correo = user ? (user.email || '') : '';

    // Crear overlay
    const overlay = document.createElement('div');
    overlay.id = 'modal-confirmacion-overlay';
    overlay.style.cssText = [
        'position:fixed;inset:0;background:rgba(0,0,0,0.55);',
        'display:flex;align-items:center;justify-content:center;z-index:9999;',
        'animation:fadeInOverlay .25s ease;'
    ].join('');

    overlay.innerHTML = `
        <div style="
            background:#fff;border-radius:20px;padding:44px 40px 36px;max-width:500px;width:92%;
            text-align:center;box-shadow:0 12px 48px rgba(0,0,0,0.28);
            animation:slideUpModal .35s ease;
        ">
            <!-- Icono animado -->
            <div style="
                width:72px;height:72px;background:linear-gradient(135deg,#4caf50,#2e7d32);
                border-radius:50%;display:flex;align-items:center;justify-content:center;
                margin:0 auto 20px;font-size:2.2rem;
                box-shadow:0 4px 18px rgba(76,175,80,0.4);
            ">✅</div>

            <!-- Título -->
            <h2 style="margin:0 0 6px;font-size:1.65rem;color:#1a1a2e;font-family:Montserrat,sans-serif;font-weight:800;">
                ¡Reserva confirmada!
            </h2>
            <p style="color:#777;margin:0 0 22px;font-size:0.95rem;">
                Gracias por confiar en <strong style="color:#e53935;">Xperiabox</strong>
            </p>

            <!-- Saludo personalizado -->
            <div style="background:#fafafa;border-radius:12px;padding:16px 20px;margin-bottom:16px;text-align:left;">
                <p style="margin:0 0 6px;font-size:1rem;color:#333;line-height:1.55;">
                    Hola <strong>${escHtml(nombre)}</strong> 👋, tu experiencia está reservada y lista para disfrutar.
                </p>
                ${correo ? `<p style="margin:0;font-size:.88rem;color:#666;">
                    Hemos enviado todos los detalles a <strong style="color:#e53935;">${escHtml(correo)}</strong>
                </p>` : ''}
            </div>

            <!-- Datos del pago -->
            ${pago ? `
            <div style="background:#f0fdf4;border:1px solid #bbf7d0;border-radius:12px;padding:14px 18px;margin-bottom:16px;text-align:left;font-size:13px;color:#166534;">
                <p style="margin:0 0 6px;font-weight:800;font-size:12px;text-transform:uppercase;letter-spacing:.06em;">🧾 Resumen del pago</p>
                <div style="display:flex;justify-content:space-between;margin-bottom:3px;">
                    <span>Referencia</span>
                    <strong style="font-family:monospace;">${escHtml(pago.referencia || '')}</strong>
                </div>
                <div style="display:flex;justify-content:space-between;margin-bottom:3px;">
                    <span>Método</span>
                    <strong>${escHtml((pago.metodoPago || '').charAt(0).toUpperCase() + (pago.metodoPago || '').slice(1))}</strong>
                </div>
                <div style="display:flex;justify-content:space-between;">
                    <span>Total pagado</span>
                    <strong>${fmt(pago.importeTotal || 0)}</strong>
                </div>
            </div>` : ''}

            <!-- Pasos siguientes -->
            <div style="text-align:left;margin-bottom:22px;">
                <p style="margin:0 0 10px;font-size:.82rem;font-weight:700;color:#999;letter-spacing:.06em;text-transform:uppercase;">¿Qué pasa ahora?</p>
                <div style="display:flex;flex-direction:column;gap:10px;">
                    <div style="display:flex;align-items:flex-start;gap:12px;">
                        <span style="font-size:1.2rem;flex-shrink:0;">📧</span>
                        <span style="font-size:.9rem;color:#444;line-height:1.45;">
                            Recibirás un correo con el resumen completo de tu reserva y los próximos pasos.
                        </span>
                    </div>
                    <div style="display:flex;align-items:flex-start;gap:12px;">
                        <span style="font-size:1.2rem;flex-shrink:0;">📅</span>
                        <span style="font-size:.9rem;color:#444;line-height:1.45;">
                            Consulta la fecha y hora de tu experiencia en <em>Mis reservas</em> en cualquier momento.
                        </span>
                    </div>
                    <div style="display:flex;align-items:flex-start;gap:12px;">
                        <span style="font-size:1.2rem;flex-shrink:0;">🎉</span>
                        <span style="font-size:.9rem;color:#444;line-height:1.45;">
                            ¡Solo queda prepararte y disfrutar de la experiencia!
                        </span>
                    </div>
                </div>
            </div>

            <!-- Botón principal -->
            <button onclick="cerrarModalConfirmacion()" style="
                width:100%;background:linear-gradient(135deg,#e53935,#c62828);color:#fff;border:none;border-radius:12px;
                padding:15px 32px;font-size:1rem;font-weight:700;cursor:pointer;
                font-family:Montserrat,sans-serif;transition:opacity .2s;letter-spacing:.02em;
            " onmouseover="this.style.opacity='.88'" onmouseout="this.style.opacity='1'">
                📋 Ver mis reservas
            </button>
        </div>
        <style>
            @keyframes fadeInOverlay { from{opacity:0} to{opacity:1} }
            @keyframes slideUpModal  { from{transform:translateY(40px);opacity:0} to{transform:translateY(0);opacity:1} }
        </style>
    `;

    document.body.appendChild(overlay);

    // Actualizar badge (carrito vacío tras compra)
    if (typeof actualizarCarritoBadge === 'function') actualizarCarritoBadge();
}

function cerrarModalConfirmacion() {
    const overlay = document.getElementById('modal-confirmacion-overlay');
    if (overlay) overlay.remove();
    sessionStorage.setItem('reservaExito', '1');
    window.location.href = '/experiencias/reservas.html';
}

// ── Helpers ───────────────────────────────────────────────────────────────────
function fmt(v) {
    return new Intl.NumberFormat("es-ES", {
        style: "currency", currency: "EUR", maximumFractionDigits: 2
    }).format(v);
}

function esc(s) {
    return String(s ?? "")
        .replace(/&/g,"&amp;").replace(/</g,"&lt;")
        .replace(/>/g,"&gt;").replace(/"/g,"&quot;");
}

function escHtml(s) { return esc(s); }
