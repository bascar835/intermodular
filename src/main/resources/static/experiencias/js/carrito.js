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

async function abrirModalCompra() {
    // 1. Generar el preview en el servidor (snapshot de condiciones)
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
        index < total - 1 ? "Siguiente →" : "✅ Confirmar reservas";

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

    _reservasPendientes[_pasoActual].fecha = fecha;

    if (_pasoActual < _reservasPendientes.length - 1) {
        _pasoActual++;
        mostrarPasoModal(_pasoActual);
    } else {
        await confirmarCompra();
    }
}

async function confirmarCompra() {
    const btnSig  = document.getElementById("btn-siguiente");
    const errorEl = document.getElementById("modal-compra-error");

    btnSig.disabled    = true;
    btnSig.textContent = "Procesando...";
    document.getElementById("modal-progreso-bar").style.width = "100%";

    // Confirmar con el servidor usando el previewId y las fechas elegidas
    const fechas = _reservasPendientes.map(item => ({
        experienciaId: item.experienciaId,
        fecha: item.fecha
    }));

    const res = await fetch("/api/me/carrito/checkout/confirm", {
        method:  "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ previewId: _previewId, fechas })
    });

    btnSig.disabled = false;
    cerrarModalCompra();

    if (res.ok) {
        // Éxito: ahora hay que asignar las fechas a las reservas creadas
        // (el servidor crea las reservas, nosotros hacemos PUT para añadir la fecha)
        // Por simplicidad: redirigir a reservas con mensaje de éxito
        sessionStorage.setItem("reservaExito", "1");
        window.location.href = "/experiencias/reservas.html";
    } else if (res.status === 409) {
        // Las condiciones cambiaron: recargar el carrito
        mostrarCarrito();
        alert("Las condiciones han cambiado. Por favor revisa el carrito antes de confirmar.");
    } else {
        const err = await res.json().catch(() => ({}));
        alert(err.message || "Error al confirmar la compra");
    }
}

function cerrarModalCompra() {
    document.getElementById("modal-compra").style.display = "none";
}

window.addEventListener("click", e => {
    const modal = document.getElementById("modal-compra");
    if (modal && e.target === modal) cerrarModalCompra();
});

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
