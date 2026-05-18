/**
 * navbar.js
 * Inyecta el header unificado y detecta la sesión activa.
 * - Sin sesión:        muestra "Iniciar sesión"
 * - Usuario normal:    muestra "Mis reservas" + nombre + "Cerrar sesión"
 * - Administrador:     además muestra "⚙ Panel Admin" destacado
 */
(function () {
    const path = window.location.pathname;

    function isActive(href) {
        return path.includes(href) ? ' active' : '';
    }

    const headerHTML = `
    <header class="site-header">
        <div class="hdr-inner">
            <a href="/experiencias/index.html" class="logo">Xperiabox<span class="logo-dot">.</span></a>
            <nav class="nav">
                <a href="/experiencias/como-funciona.html" class="nav-a${isActive('/como-funciona')}">Cómo funciona</a>
                <a href="/experiencias/index.html"         class="nav-a${isActive('/experiencias/index')}">Experiencias</a>
                <a href="/experiencias/categorias.html"    class="nav-a${isActive('/experiencias/categorias')}">Categorías</a>
                <a href="/experiencias/carrito.html"       class="nav-a${isActive('/experiencias/carrito')}">🛒 Carrito</a>
                <a href="/experiencias/login.html"         class="nav-a" id="nav-auth-link">👤 Iniciar sesión</a>
            </nav>
        </div>
    </header>`;

    // Inyectar solo si no existe ya el header
    if (!document.querySelector('.site-header')) {
        document.body.insertAdjacentHTML('afterbegin', headerHTML);
    }

    // Detectar sesión y actualizar el nav
    fetch('/api/auth/me', { credentials: 'include' })
        .then(res => res.ok ? res.json() : null)
        .then(user => {
            const link = document.getElementById('nav-auth-link');
            if (!link) return;

            if (!user) return; // Sin sesión: dejar "Iniciar sesión"

            const nombre       = user.name || user.nombre || 'Usuario';
            const esAdmin      = user.role === 'ROLE_ADMIN';
            const reservasActive = path.includes('/reservas') ? ' active' : '';

            const wrapper = document.createElement('span');
            wrapper.className = 'nav-user';
            wrapper.innerHTML = `
                <a href="/experiencias/reservas.html" class="nav-a${reservasActive}">📅 Mis reservas</a>
                ${esAdmin ? `<a href="/admin/index.html" class="nav-a nav-admin-btn">⚙ Panel Admin</a>` : ''}
                <span class="nav-a nav-username">👤 ${escHtml(nombre)}</span>
                <button class="nav-a nav-logout-btn" onclick="cerrarSesion()">Cerrar sesión</button>
            `;
            link.replaceWith(wrapper);
        })
        .catch(() => {});

})();

function cerrarSesion() {
    fetch('/api/auth/logout', { method: 'POST', credentials: 'include' })
        .catch(() => {})
        .finally(() => {
            sessionStorage.removeItem('xperiabox_carrito');
            window.location.href = '/experiencias/login.html';
        });
}

function escHtml(s) {
    return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;')
                          .replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
