function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

function previewImagen() {
    const file = document.getElementById("imagen").files[0];
    const preview = document.getElementById("preview");
    if (!file) { preview.style.display = "none"; preview.src = ""; return; }
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
    document.getElementById("activo").checked    = c.activo;

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
    limpiarTodosLosErrores();

    const ok = validarFormulario([
        { id: "nombre",      label: "Nombre" },
        { id: "descripcion", label: "Descripción" },
    ]);
    if (!ok) return;

    const id = obtenerId();
    const fd = new FormData();
    fd.append("nombre",      document.getElementById("nombre").value.trim());
    fd.append("descripcion", document.getElementById("descripcion").value.trim());
    fd.append("activo",      document.getElementById("activo").checked ? "true" : "false");
    const file = document.getElementById("imagen").files[0];
    if (file) fd.append("imagen", file);

    const res = await authFetch(`/api/admin/categorias/${id}`, { method: "PUT", body: fd });
    if (!res || !res.ok) {
        let msg = "Error al guardar los cambios.";
        try { const d = await res.json(); if (d.message) msg = d.message; } catch (_) {}
        mostrarErrorGeneral(msg);
        return;
    }
    location.href = "index.html";
}

cargar();
