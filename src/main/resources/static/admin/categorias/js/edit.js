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

async function cargar() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/categorias/${id}`);
    if (!response) return;
    const c = await response.json();

    document.getElementById("nombre").value      = c.nombre;
    document.getElementById("descripcion").value = c.descripcion || "";

    const imgActual = document.getElementById("imagen-actual");
    const sinImagen = document.getElementById("sin-imagen");

    if (c.imagen_url) {
        imgActual.src = c.imagen_url;
        imgActual.style.display = "block";
        sinImagen.style.display = "none";
    } else {
        imgActual.style.display = "none";
        sinImagen.style.display = "inline";
    }
}

async function guardar(e) {
    e.preventDefault();
    const id = obtenerId();

    const fd = new FormData();
    fd.append("nombre",      document.getElementById("nombre").value);
    fd.append("descripcion", document.getElementById("descripcion").value);

    const file = document.getElementById("imagen").files[0];
    if (file) {
        fd.append("imagen", file);
    }

    await authFetch(`/api/admin/categorias/${id}`, {
        method: "PUT",
        body: fd
    });

    location.href = "index.html";
}

cargar();
