function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

function previewImagen() {
    const file = document.getElementById("imagen").files[0];
    const preview = document.getElementById("preview");

    if (!file) {
        preview.style.display = "none";
        preview.src = "";
        return;
    }

    preview.src = URL.createObjectURL(file);
    preview.style.display = "block";
}

async function cargarCategorias(selectedId) {
    const response = await authFetch("/api/admin/categorias");
    if (!response) return;
    const categorias = await response.json();

    const select = document.getElementById("categoria_id");
    categorias.forEach(c => {
        const option = document.createElement("option");
        option.value = c.id;
        option.textContent = c.nombre;
        if (c.id === selectedId) option.selected = true;
        select.appendChild(option);
    });
}

async function cargar() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/experiencias/${id}`);
    if (!response) return;
    const exp = await response.json();

    document.getElementById("titulo").value        = exp.titulo;
    document.getElementById("descripcion").value   = exp.descripcion || "";
    document.getElementById("precio").value        = exp.precio;
    document.getElementById("ubicacion").value     = exp.ubicacion || "";
    document.getElementById("duracion_horas").value= exp.duracion_horas;

    // Mostrar imagen actual si existe
    const imgActual = document.getElementById("imagen-actual");
    const sinImagen = document.getElementById("sin-imagen");

    if (exp.imagen_url) {
        imgActual.src = exp.imagen_url;
        imgActual.style.display = "block";
        sinImagen.style.display = "none";
    } else {
        imgActual.style.display = "none";
        sinImagen.style.display = "inline";
    }

    await cargarCategorias(exp.categoria_id);
}

async function guardar(e) {
    e.preventDefault();

    const id = obtenerId();

    const fd = new FormData();
    fd.append("titulo",        document.getElementById("titulo").value);
    fd.append("descripcion",   document.getElementById("descripcion").value);
    fd.append("precio",        document.getElementById("precio").value);
    fd.append("ubicacion",     document.getElementById("ubicacion").value);
    fd.append("duracion_horas",document.getElementById("duracion_horas").value);
    fd.append("categoria_id",  document.getElementById("categoria_id").value);

    const file = document.getElementById("imagen").files[0];
    if (file) {
        fd.append("imagen", file);
    }

    await authFetch(`/api/admin/experiencias/${id}`, {
        method: "PUT",
        body: fd
    });

    location.href = "index.html";
}

cargar();
