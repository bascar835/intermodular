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

async function cargarCategorias() {
    const response = await authFetch("/api/admin/categorias");
    if (!response) return;
    const categorias = await response.json();

    const select = document.getElementById("categoria_id");
    categorias.forEach(c => {
        const option = document.createElement("option");
        option.value = c.id;
        option.textContent = c.nombre;
        select.appendChild(option);
    });
}

async function guardar(e) {
    e.preventDefault();

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

    await authFetch("/api/admin/experiencias", {
        method: "POST",
        body: fd
        // SIN Content-Type: el navegador lo pone automáticamente con el boundary correcto
    });

    location.href = "index.html";
}

cargarCategorias();
