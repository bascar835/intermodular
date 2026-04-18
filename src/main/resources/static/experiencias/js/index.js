// index.js — carga experiencias reales desde /api/experiencias

const IMG_PLACEHOLDER = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=500&q=80";

let isProfileOpen = false;

document.addEventListener("DOMContentLoaded", async function () {
    actualizarContadorCarrito();
    await cargarUsuarioSesion();
    await cargarExperiencias();
});

// ─── Sesión ──────────────────────────────────────────────────────────────────

async function cargarUsuarioSesion() {
    try {
        const res = await fetch("/api/auth/me");
        if (!res.ok) return;

        const user = await res.json();
        localStorage.setItem("userName", user.name || "");

        const loginLink = document.querySelector('a[href="login.html"]');
        if (loginLink) {
            loginLink.textContent = "👤 " + (user.name || "Mi cuenta");
            loginLink.href = "#";
            loginLink.onclick = (e) => { e.preventDefault(); toggleProfile(); };
        }

        const panelTitle = document.querySelector("#userPanel .panel-header h3");
        if (panelTitle) panelTitle.textContent = user.name || "Mi Perfil";

    } catch (e) {
        // Sin sesión, no pasa nada
    }
}

// ─── Experiencias desde la API ───────────────────────────────────────────────

async function cargarExperiencias() {
    try {
        const res = await fetch("/api/experiencias");
        if (!res.ok) throw new Error("Error cargando experiencias");

        const experiencias = await res.json();
        const grid = document.querySelector(".experience-grid");
        if (!grid) return;

        grid.innerHTML = "";

        if (experiencias.length === 0) {
            grid.innerHTML = "<p style='color:var(--color-text-secondary)'>No hay experiencias disponibles.</p>";
            return;
        }

        experiencias.forEach(exp => {
            grid.appendChild(crearCard(exp));
        });

    } catch (e) {
        console.error("Error al cargar experiencias:", e);
    }
}

function crearCard(exp) {
    // Usar imagen subida por el admin; si no tiene, mostrar placeholder
    const imgUrl = exp.imagen_url || IMG_PLACEHOLDER;

    const card = document.createElement("div");
    card.className = "card";
    card.innerHTML = `
        <div class="card-img" style="background-image: url('${imgUrl}');">
            <div class="wishlist-heart" onclick="toggleFavorite(this)">
                <i class="fa-regular fa-heart"></i>
            </div>
        </div>
        <div class="card-body">
            <span class="cat-label">${exp.ubicacion || "Experiencia"}</span>
            <h3>${exp.titulo}</h3>
            <p>${exp.descripcion || ""}</p>
            <div class="card-footer">
                <span class="price">${exp.precio.toFixed(2)}€</span>
                <button class="btn-add" onclick="addToCart(${exp.id}, '${escapar(exp.titulo)}', ${exp.precio})">
                    Añadir 🛒
                </button>
            </div>
        </div>
    `;
    return card;
}

function escapar(str) {
    return str.replace(/'/g, "\\'");
}

// ─── Carrito ─────────────────────────────────────────────────────────────────

function addToCart(id, nombre, precio) {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    carrito.push({ id, nombre, precio: parseFloat(precio) });
    localStorage.setItem("carrito", JSON.stringify(carrito));
    actualizarContadorCarrito();

    const modal = document.getElementById("modalConfirmacion");
    if (modal) modal.style.display = "flex";
}

function actualizarContadorCarrito() {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    const badge = document.getElementById("cart-count");
    if (badge) badge.innerText = carrito.length;
}

// ─── Modal ───────────────────────────────────────────────────────────────────

function cerrarModal() {
    const modal = document.getElementById("modalConfirmacion");
    if (modal) modal.style.display = "none";
}

window.onclick = function (event) {
    const modal = document.getElementById("modalConfirmacion");
    if (modal && event.target === modal) cerrarModal();
};

// ─── Panel perfil ─────────────────────────────────────────────────────────────

function toggleProfile() {
    const panel = document.getElementById("userPanel");
    if (!panel) return;

    isProfileOpen = !isProfileOpen;
    panel.classList.toggle("open", isProfileOpen);

    if (isProfileOpen) createOverlay();
    else removeOverlay();
}

function createOverlay() {
    if (document.getElementById("overlay")) return;
    const div = document.createElement("div");
    div.id = "overlay";
    div.className = "panel-overlay";
    div.style.display = "block";
    div.onclick = toggleProfile;
    document.body.appendChild(div);
}

function removeOverlay() {
    const overlay = document.getElementById("overlay");
    if (overlay) overlay.remove();
}

// ─── Favoritos ───────────────────────────────────────────────────────────────

function toggleFavorite(elemento) {
    const icono = elemento.querySelector("i");
    if (!icono) return;
    if (icono.classList.contains("fa-regular")) {
        icono.classList.replace("fa-regular", "fa-solid");
    } else {
        icono.classList.replace("fa-solid", "fa-regular");
    }
}

// ─── Logout ──────────────────────────────────────────────────────────────────

async function logout() {
    await fetch("/api/auth/logout", { method: "POST" });
    localStorage.removeItem("userName");
    localStorage.removeItem("carrito");
    window.location.href = "/experiencias/login.html";
}
