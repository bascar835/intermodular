async function cargarStats() {
    try {
        const [exp, cat, res, usr] = await Promise.all([
            fetch('/api/experiencias').then(r => r.json()),
            fetch('/api/admin/categorias').then(r => r.json()),
            fetch('/api/admin/reservas').then(r => r.json()),
            fetch('/api/admin/users').then(r => r.json()),
        ]);
        document.getElementById('stat-experiencias').textContent =
            exp.total ?? (exp.data ? exp.data.length : exp.length);
        document.getElementById('stat-categorias').textContent = cat.length;
        document.getElementById('stat-reservas').textContent   = res.length;
        document.getElementById('stat-usuarios').textContent   = usr.length;
    } catch (e) {
        console.error('Error cargando stats:', e);
    }
}

cargarStats();
