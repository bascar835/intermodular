function obtenerId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

async function cargar() {
    const id = obtenerId();

    const response = await fetch(`/api/admin/categorias/${id}`);
    const c = await response.json();

    nombre.value = c.nombre;
    descripcion.value = c.descripcion;
    activa.checked = c.activa;
}

async function guardar(e) {
    e.preventDefault();

    const id = obtenerId();

    const categoria = {
        nombre: nombre.value,
        descripcion: descripcion.value,
        activa: activa.checked
    };

    await fetch(`/api/admin/categorias/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(categoria)
    });

    location.href = "index.html";
}

cargar();