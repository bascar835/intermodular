/**
 * navbar.js
 * Inyecta el header unificado en cualquier página que lo incluya
 * y detecta la sesión activa para mostrar el nombre del usuario.
 *
 * Uso: <script src="/experiencias/js/navbar.js"></script>
 * El header se inserta al inicio de <body> si aún no existe .site-header.
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
                <a href="/experiencias/index.html"   class="nav-a${isActive('/experiencias/index')}">Experiencias</a>
                <a href="/experiencias/categorias.html" class="nav-a${isActive('/experiencias/categorias')}">Categorías</a>
                <a href="/experiencias/carrito.html" class="nav-a${isActive('/experiencias/carrito')}">🛒 Carrito</a>
                <a href="/experiencias/login.html"   class="nav-a" id="nav-auth-link">👤 Iniciar sesión</a>
            </nav>
        </div>
    </header>`;

    // Inyectar solo si no existe ya el header unificado
    if (!document.querySelector('.site-header')) {
        document.body.insertAdjacentHTML('afterbegin', headerHTML);
    }

    // Detectar sesión
    fetch('/api/auth/me', { credentials: 'include' })
        .then(res => res.ok ? res.json() : null)
        .then(user => {
            const link = document.getElementById('nav-auth-link');
            if (!link) return;
            if (!user) return; // no hay sesión, dejar "Iniciar sesión"

            const nombre = user.name || user.nombre || 'Usuario';
            const wrapper = document.createElement('span');
            wrapper.className = 'nav-user';
            wrapper.innerHTML = `
                <span class="nav-a nav-username">👤 ${escHtml(nombre)}</span>
                <button class="nav-a nav-logout-btn" onclick="cerrarSesion()">Cerrar sesión</button>
            `;
            link.replaceWith(wrapper);
        })
        .catch(() => {}); // sin sesión o sin red: dejamos el enlace

})();

function cerrarSesion() {
    fetch('/api/auth/logout', { method: 'POST', credentials: 'include' })
        .catch(() => {})
        .finally(() => {
            localStorage.removeItem('userName');
            window.location.href = '/experiencias/login.html';
        });
}

function escHtml(s) {
    return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;')
                          .replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
