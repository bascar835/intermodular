function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargarCategoria() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/categorias/${id}`);
    if (!response) return;
    const c = await response.json();

    document.getElementById("titulo-header").textContent = c.nombre;
    document.getElementById("nombre").textContent = c.nombre;
    document.getElementById("descripcion").textContent = c.descripcion;
    document.getElementById("editar").href = `edit.html?id=${id}`;
}

cargarCategoria();
