let isProfileOpen = false;

// Cuando cargue la página
document.addEventListener("DOMContentLoaded", function () {
    actualizarContadorCarrito();
});

// PERFIL LATERAL
function toggleProfile() {
    const panel = document.getElementById('userPanel');

    if (!panel) return;

    isProfileOpen = !isProfileOpen;

    if (isProfileOpen) {
        panel.classList.add('open');
        createOverlay();
    } else {
        panel.classList.remove('open');
        removeOverlay();
    }
}

function createOverlay() {
    if (document.getElementById('overlay')) return;

    const div = document.createElement('div');
    div.id = 'overlay';
    div.className = 'panel-overlay';
    div.style.display = 'block';
    div.onclick = toggleProfile;
    document.body.appendChild(div);
}

function removeOverlay() {
    const overlay = document.getElementById('overlay');
    if (overlay) overlay.remove();
}

// CARRITO
function addToCart(nombre, precio) {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];

    carrito.push({
        nombre: nombre,
        precio: parseFloat(precio)
    });

    localStorage.setItem("carrito", JSON.stringify(carrito));

    actualizarContadorCarrito();

    const modal = document.getElementById('modalConfirmacion');
    if (modal) {
        modal.style.display = "flex";
    }
}

function actualizarContadorCarrito() {
    let carrito = JSON.parse(localStorage.getItem("carrito")) || [];
    let cartBadge = document.getElementById('cart-count');

    if (cartBadge) {
        cartBadge.innerText = carrito.length;
    }
}

// MODAL
function cerrarModal() {
    const modal = document.getElementById('modalConfirmacion');
    if (modal) {
        modal.style.display = "none";
    }
}

// Cerrar modal al hacer clic fuera
window.onclick = function(event) {
    const modal = document.getElementById('modalConfirmacion');

    if (modal && event.target === modal) {
        cerrarModal();
    }
};

// FAVORITOS
function toggleFavorite(elemento) {
    const icono = elemento.querySelector("i");

    if (icono.classList.contains("fa-regular")) {
        icono.classList.remove("fa-regular");
        icono.classList.add("fa-solid");
    } else {
        icono.classList.remove("fa-solid");
        icono.classList.add("fa-regular");
    }
}
