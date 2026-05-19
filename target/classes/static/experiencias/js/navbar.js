/**
 * navbar.js
 * Inyecta el header unificado y detecta la sesión activa.
 * - Sin sesión:        muestra "Iniciar sesión"
 * - Usuario normal:    muestra "Mis reservas" + nombre + "Cerrar sesión"
 * - Administrador:     además muestra "⚙ Panel Admin" destacado
 *
 * Badge del carrito sincronizado entre todas las páginas via BroadcastChannel.
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
                <a href="/experiencias/carrito.html"       class="nav-a${isActive('/experiencias/carrito')} nav-carrito-link">
                    🛒 Carrito<span id="carrito-badge" class="carrito-badge" style="display:none;"></span>
                </a>
                <a href="/experiencias/login.html" class="nav-a" id="nav-auth-link">👤 Iniciar sesión</a>
            </nav>
        </div>
    </header>
    <style>
        .nav-carrito-link { position: relative; }
        .carrito-badge {
            position: absolute;
            top: -8px; right: -10px;
            background: #e53935;
            color: #fff;
            font-size: 0.68rem;
            font-weight: 700;
            min-width: 18px; height: 18px;
            border-radius: 9px;
            display: flex !important;
            align-items: center; justify-content: center;
            padding: 0 4px; line-height: 1;
            box-shadow: 0 1px 4px rgba(0,0,0,0.28);
            pointer-events: none;
            animation: badge-pop 0.22s ease;
        }
        @keyframes badge-pop {
            0%   { transform: scale(0.5); opacity: 0; }
            70%  { transform: scale(1.25); }
            100% { transform: scale(1);   opacity: 1; }
        }
    </style>`;

    if (!document.querySelector('.site-header')) {
        document.body.insertAdjacentHTML('afterbegin', headerHTML);
    }

    // ── BroadcastChannel: escuchar actualizaciones desde cualquier pestaña ──
    if (typeof BroadcastChannel !== 'undefined') {
        const bc = new BroadcastChannel('xperiabox_carrito');
        bc.onmessage = function (e) {
            if (e.data && e.data.type === 'carrito_update') {
                _renderBadge(e.data.count);
            }
        };
    }

    // ── Detectar sesión ──
    fetch('/api/auth/me', { credentials: 'include' })
        .then(res => res.ok ? res.json() : null)
        .then(user => {
            const link = document.getElementById('nav-auth-link');
            if (!link) return;
            if (!user) return;

            const nombre        = user.name || user.nombre || 'Usuario';
            const esAdmin       = user.role === 'ROLE_ADMIN';
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

            // Cargar badge inicial
            actualizarCarritoBadge();
        })
        .catch(() => {});

})();

/** Renderiza el badge con el número dado (0 = oculto). */
function _renderBadge(count) {
    const badge = document.getElementById('carrito-badge');
    if (!badge) return;
    if (count > 0) {
        badge.textContent = count > 99 ? '99+' : count;
        badge.style.display = 'flex';
        // Re-trigger animation
        badge.style.animation = 'none';
        badge.offsetHeight; // reflow
        badge.style.animation = '';
    } else {
        badge.style.display = 'none';
    }
}

/**
 * Consulta la API, actualiza el badge local y notifica al resto de pestañas.
 * Llama a esta función desde cualquier página después de añadir/eliminar items.
 */
function actualizarCarritoBadge() {
    fetch('/api/me/carrito', { credentials: 'include' })
        .then(res => res.ok ? res.json() : [])
        .then(items => {
            const count = Array.isArray(items) ? items.length : 0;
            _renderBadge(count);
            // Notificar al resto de pestañas abiertas
            if (typeof BroadcastChannel !== 'undefined') {
                try {
                    const bc = new BroadcastChannel('xperiabox_carrito');
                    bc.postMessage({ type: 'carrito_update', count });
                    bc.close();
                } catch (_) {}
            }
        })
        .catch(() => {});
}

function cerrarSesion() {
    fetch('/api/auth/logout', { method: 'POST', credentials: 'include' })
        .catch(() => {})
        .finally(() => {
            sessionStorage.removeItem('xperiabox_carrito');
            // Avisar a otras pestañas que el carrito es 0
            if (typeof BroadcastChannel !== 'undefined') {
                try {
                    const bc = new BroadcastChannel('xperiabox_carrito');
                    bc.postMessage({ type: 'carrito_update', count: 0 });
                    bc.close();
                } catch (_) {}
            }
            window.location.href = '/experiencias/login.html';
        });
}

function escHtml(s) {
    return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;')
                          .replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
