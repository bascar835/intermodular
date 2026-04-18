function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargarExperiencia() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/experiencias/${id}`);
    if (!response) return;
    const exp = await response.json();

    document.getElementById("titulo-header").textContent = exp.titulo;
    document.getElementById("titulo").textContent        = exp.titulo;
    document.getElementById("descripcion").textContent   = exp.descripcion || "-";
    document.getElementById("precio").textContent        = exp.precio;
    document.getElementById("ubicacion").textContent     = exp.ubicacion || "-";
    document.getElementById("duracion_horas").textContent= exp.duracion_horas;
    document.getElementById("categoria_id").textContent  = exp.categoria_id;

    const img = document.getElementById("imagen");
    if (exp.imagen_url) {
        img.src = exp.imagen_url;
        img.style.display = "block";
    }

    document.getElementById("editar").href = `edit.html?id=${id}`;
}

cargarExperiencia();
