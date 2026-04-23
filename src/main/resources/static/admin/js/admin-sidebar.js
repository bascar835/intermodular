// admin-sidebar.js
// Inyecta el sidebar en todas las páginas de admin y marca el enlace activo.
// Incluir con: <script src="/admin/js/admin-sidebar.js"></script>

(function () {
    // ── Detectar qué sección está activa ────────────────────────────────────
    const path = window.location.pathname;

    function isActive(section) {
        return path.includes('/admin/' + section) ? 'active' : '';
    }

    // ── HTML del sidebar ────────────────────────────────────────────────────
    const sidebarHTML = `
    <aside class="admin-sidebar">

        <div class="sidebar-logo">
            <div class="logo-title">🎁 XPerio<span>PRO</span></div>
            <div class="logo-sub">Panel de administración</div>
        </div>

        <nav class="sidebar-nav">
            <div class="nav-section-label">Principal</div>

            <a class="nav-item ${isActive('index')}" href="/admin/index.html">
                <span class="nav-icon">📊</span>
                <span>Dashboard</span>
            </a>

            <div class="nav-section-label">Gestión</div>

            <a class="nav-item ${isActive('experiencias')}" href="/admin/experiencias/index.html">
                <span class="nav-icon">🎯</span>
                <span>Experiencias</span>
            </a>

            <a class="nav-item ${isActive('categorias')}" href="/admin/categorias/index.html">
                <span class="nav-icon">🏷️</span>
                <span>Categorías</span>
            </a>

            <a class="nav-item ${isActive('reservas')}" href="/admin/reservas/index.html">
                <span class="nav-icon">📅</span>
                <span>Reservas</span>
            </a>

            <a class="nav-item ${isActive('usuarios')}" href="/admin/usuarios/index.html">
                <span class="nav-icon">👥</span>
                <span>Usuarios</span>
            </a>
        </nav>

        <div class="sidebar-footer">
            <a href="/experiencias/index.html">
                <span class="nav-icon">👁️</span>
                <span>Ver web pública</span>
            </a>
            <a href="#" onclick="logout()">
                <span class="nav-icon">🚪</span>
                <span>Cerrar sesión</span>
            </a>
        </div>

    </aside>`;

    // ── Inyectar sidebar antes del .admin-main ────────────────────────────
    document.body.insertAdjacentHTML('afterbegin', sidebarHTML);

    // ── Logout ────────────────────────────────────────────────────────────
    window.logout = async function () {
        await fetch('/api/auth/logout', { method: 'POST' });
        localStorage.removeItem('userName');
        window.location.href = '/experiencias/login.html';
    };

    // ── Verificar sesión y rol admin ──────────────────────────────────────
    fetch('/api/auth/me')
        .then(r => {
            if (!r.ok) { window.location.href = '/experiencias/login.html'; return null; }
            return r.json();
        })
        .then(user => {
            if (!user) return;
            if (user.role !== 'ROLE_ADMIN') {
                window.location.href = '/experiencias/index.html';
                return;
            }
            // Mostrar nombre en topbar si existe el elemento
            const userEl = document.querySelector('.topbar-user .user-name');
            if (userEl) userEl.textContent = user.name || 'Admin';
        })
        .catch(() => { window.location.href = '/experiencias/login.html'; });

})();
