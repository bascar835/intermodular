// carrito.js — gestión del carrito con localStorage + finalizar compra por experiencia

document.addEventListener("DOMContentLoaded", function () {
    mostrarCarrito();
});

function addToCart(id, nombre, precio) {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    carrito.push({ id, nombre, precio: parseFloat(precio) });
    localStorage.setItem("carrito", JSON.stringify(carrito));
    mostrarCarrito();
}

function mostrarCarrito() {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];

    const emptyCart    = document.getElementById("emptyCart");
    const cartProducts = document.getElementById("cartProducts");
    const productsList = document.getElementById("productsList");
    const totalCarrito = document.getElementById("totalCarrito");

    if (!productsList || !emptyCart || !cartProducts || !totalCarrito) return;

    productsList.innerHTML = "";

    if (carrito.length === 0) {
        emptyCart.style.display = "block";
        cartProducts.style.display = "none";
        return;
    }

    emptyCart.style.display = "none";
    cartProducts.style.display = "block";

    let total = 0;
    carrito.forEach((producto, index) => {
        total += parseFloat(producto.precio);
        productsList.innerHTML += `
            <div class="cart-product">
                <h3>${producto.nombre}</h3>
                <p>Precio base: ${producto.precio.toFixed(2)}€</p>
                <button class="btn-delete" onclick="eliminarProducto(${index})">Eliminar</button>
            </div>
            <hr>
        `;
    });

    totalCarrito.innerText = `${carrito.length} experiencia${carrito.length > 1 ? 's' : ''} en el carrito`;

    productsList.innerHTML += `
        <button class="btn-secondary" onclick="vaciarCarrito()">Vaciar carrito</button>
    `;
}

function eliminarProducto(index) {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    carrito.splice(index, 1);
    localStorage.setItem("carrito", JSON.stringify(carrito));
    mostrarCarrito();
}

function vaciarCarrito() {
    localStorage.removeItem("carrito");
    mostrarCarrito();
}

// ─── Finalizar compra — paso a paso por experiencia ──────────────────────────

let _carritoCheckout = [];
let _pasoActual = 0;

async function abrirModalCompra() {
    const carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    if (carrito.length === 0) return;

    const meRes = await fetch("/api/auth/me");
    if (!meRes.ok) {
        window.location.href = "/experiencias/login.html";
        return;
    }

    _carritoCheckout = carrito.map(item => ({ ...item, fecha: null, personas: 1 }));
    _pasoActual = 0;
    mostrarPaso(_pasoActual);
    document.getElementById("modal-compra").style.display = "flex";
}

function mostrarPaso(index) {
    const item  = _carritoCheckout[index];
    const total = _carritoCheckout.length;

    document.getElementById("modal-paso-label").textContent = `Experiencia ${index + 1} de ${total}`;
    document.getElementById("modal-nombre-exp").textContent = item.nombre;
    document.getElementById("modal-precio-exp").textContent = `Precio base: ${item.precio.toFixed(2)}€`;

    const manana = new Date();
    manana.setDate(manana.getDate() + 1);
    const minFecha = manana.toISOString().slice(0, 16);
    const inputFecha = document.getElementById("input-fecha");
    inputFecha.min = minFecha;
    inputFecha.value = item.fecha || minFecha;

    document.getElementById("input-personas").value = item.personas || 1;

    const btnSig = document.getElementById("btn-siguiente");
    btnSig.textContent = index < total - 1 ? "Siguiente →" : "✅ Confirmar todo";

    const pct = Math.round((index / total) * 100);
    document.getElementById("modal-progreso-bar").style.width = pct + "%";

    document.getElementById("modal-compra-error").style.display = "none";
}

function siguientePaso() {
    const fecha    = document.getElementById("input-fecha").value;
    const personas = parseInt(document.getElementById("input-personas").value, 10);
    const errorEl  = document.getElementById("modal-compra-error");

    if (!fecha) {
        errorEl.textContent = "Por favor selecciona una fecha.";
        errorEl.style.display = "block";
        return;
    }
    if (!personas || personas < 1) {
        errorEl.textContent = "El número de personas debe ser al menos 1.";
        errorEl.style.display = "block";
        return;
    }

    _carritoCheckout[_pasoActual].fecha    = fecha;
    _carritoCheckout[_pasoActual].personas = personas;

    if (_pasoActual < _carritoCheckout.length - 1) {
        _pasoActual++;
        mostrarPaso(_pasoActual);
    } else {
        enviarReservas();
    }
}

async function enviarReservas() {
    const btnSig = document.getElementById("btn-siguiente");
    btnSig.disabled = true;
    btnSig.textContent = "Procesando...";
    document.getElementById("modal-compra-error").style.display = "none";
    document.getElementById("modal-progreso-bar").style.width = "100%";

    let errores = 0;

    for (const item of _carritoCheckout) {
        if (!item.id) { errores++; continue; }

        const body = {
            experiencia_id:  item.id,
            fecha_reserva:   item.fecha,
            numero_personas: item.personas,
            precio_total:    item.precio * item.personas,
            estado:          "pendiente"
        };

        try {
            const res = await fetch("/api/reservas", {
                method:  "POST",
                headers: { "Content-Type": "application/json" },
                body:    JSON.stringify(body)
            });
            if (!res.ok) errores++;
        } catch (e) {
            errores++;
        }
    }

    btnSig.disabled = false;
    cerrarModalCompra();

    const total = _carritoCheckout.length;

    if (errores === 0) {
        localStorage.removeItem("carrito");
        document.getElementById("emptyCart").style.display = "none";
        document.getElementById("cartProducts").style.display = "block";
        document.getElementById("cartProducts").innerHTML = `
            <div style="text-align:center;padding:60px 20px;">
                <div style="font-size:64px;margin-bottom:16px;">✅</div>
                <h2 style="color:#065f46;margin-bottom:8px;">¡Reserva${total > 1 ? 's realizadas' : ' realizada'} con éxito!</h2>
                <p style="color:#555;margin-bottom:24px;">
                    ${total} experiencia${total > 1 ? 's han sido reservadas' : ' ha sido reservada'}.
                </p>
                <a href="reservas.html" class="btn-main">Ver mis reservas 📅</a>
            </div>
        `;
    } else {
        const errorEl = document.getElementById("modal-compra-error");
        document.getElementById("modal-compra").style.display = "flex";
        errorEl.textContent = `Se produjeron ${errores} error(es). Comprueba tu sesión e inténtalo de nuevo.`;
        errorEl.style.display = "block";
        btnSig.textContent = "✅ Confirmar todo";
    }
}

function cerrarModalCompra() {
    document.getElementById("modal-compra").style.display = "none";
}

window.addEventListener("click", function (e) {
    const modal = document.getElementById("modal-compra");
    if (modal && e.target === modal) cerrarModalCompra();
});
