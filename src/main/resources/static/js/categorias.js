async function cargarCategorias() {
    const res = await fetch('/api/categorias');
    const categorias = await res.json();

    const grid = document.getElementById('grid-categorias');
    grid.innerHTML = '';

    categorias.forEach(c => {
        grid.innerHTML += `
            <div class="card" onclick="cargarExperiencias(${c.id}, '${c.nombre}')">
                <h3>${c.nombre}</h3>
                <p>${c.descripcion ?? ''}</p>
            </div>
        `;
    });
}

async function cargarExperiencias(categoriaId, nombreCategoria) {

    // Título
    document.getElementById('titulo-categoria').innerText = nombreCategoria;

    // Petición (POST con JSON → EXACTO como el PDF)
    const res = await fetch('/api/categorias/experiencias', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ categoriaId })
    });

    const datos = await res.json();

    const grid = document.getElementById('grid-experiencias');
    const badge = document.getElementById('badge-total');

    // Reset
    grid.innerHTML = '';
    badge.innerText = `(${datos.total})`;

    // Sin datos
    if (!datos.data || datos.data.length === 0) {
        grid.innerHTML = '<p>No hay experiencias</p>';
        return;
    }

    // Pintar cards
    datos.data.forEach(e => {
        grid.innerHTML += `
            <div class="card">
                <h4>${e.titulo}</h4>
                <p>${e.descripcion}</p>
                <p><strong>${e.precio} €</strong></p>
                <p>${e.ubicacion ?? ''}</p>
            </div>
        `;
    });
}

cargarCategorias();