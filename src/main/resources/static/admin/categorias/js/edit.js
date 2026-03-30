function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargar() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/categorias/${id}`);
    if (!response) return;
    const c = await response.json();

    nombre.value = c.nombre;
    descripcion.value = c.descripcion;
}

async function guardar(e) {
    e.preventDefault();
    const id = obtenerId();

    await authFetch(`/api/admin/categorias/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nombre: nombre.value,
            descripcion: descripcion.value
        })
    });

    location.href = "index.html";
}

cargar();
