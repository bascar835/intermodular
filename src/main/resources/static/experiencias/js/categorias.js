const IMG_PLACEHOLDER = "https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=500&q=80";

async function cargarCategorias() {
    const res = await fetch('/api/categorias');
    const categorias = await res.json();

    const grid = document.getElementById('grid-categorias');
    grid.innerHTML = '';

    categorias.forEach(c => {
        const imgUrl = c.imagen_url || IMG_PLACEHOLDER;

        grid.innerHTML += `
            <div class="card" onclick="cargarExperiencias(${c.id}, '${escapar(c.nombre)}')">
                <div class="card-img" style="background-image: url('${imgUrl}'); height:140px; background-size:cover; background-position:center; border-radius:8px 8px 0 0;"></div>
                <div style="padding:12px;">
                    <h3 style="margin:0 0 4px;">${c.nombre}</h3>
                    <p style="margin:0;font-size:0.9em;color:#6b7280;">${c.descripcion ?? ''}</p>
                </div>
            </div>
        `;
    });
}

async function cargarExperiencias(categoriaId, nombreCategoria) {

    document.getElementById('titulo-categoria').innerText = nombreCategoria;

    const res = await fetch('/api/categorias/experiencias', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ categoriaId })
    });

    const datos = await res.json();

    const grid = document.getElementById('grid-experiencias');
    const badge = document.getElementById('badge-total');

    grid.innerHTML = '';
    badge.innerText = `(${datos.total})`;

    if (!datos.data || datos.data.length === 0) {
        grid.innerHTML = '<p>No hay experiencias</p>';
        return;
    }

    datos.data.forEach(e => {
        const imgUrl = e.imagen_url || IMG_PLACEHOLDER;

        grid.innerHTML += `
            <div class="card">
                <div class="card-img" style="background-image: url('${imgUrl}'); height:160px; background-size:cover; background-position:center; border-radius:8px 8px 0 0;"></div>
                <div style="padding:12px;">
                    <h4 style="margin:0 0 4px;">${e.titulo}</h4>
                    <p style="margin:0 0 6px;font-size:0.9em;color:#6b7280;">${e.descripcion ?? ''}</p>
                    <p style="margin:0 0 4px;"><strong>${e.precio} €</strong></p>
                    <p style="margin:0;font-size:0.85em;color:#9ca3af;">${e.ubicacion ?? ''}</p>
                </div>
            </div>
        `;
    });
}

function escapar(str) {
    return str.replace(/'/g, "\\'");
}

cargarCategorias();
