function previewImagen() {
    const file = document.getElementById("imagen").files[0];
    const preview = document.getElementById("preview");
    if (!file) { preview.style.display = "none"; preview.src = ""; return; }
    preview.src = URL.createObjectURL(file);
    preview.style.display = "block";
}

async function guardar(e) {
    e.preventDefault();
    limpiarTodosLosErrores();

    const ok = validarFormulario([
        { id: "nombre",      label: "Nombre" },
        { id: "descripcion", label: "Descripción" },
    ]);
    if (!ok) return;

    const fd = new FormData();
    fd.append("nombre",      document.getElementById("nombre").value.trim());
    fd.append("descripcion", document.getElementById("descripcion").value.trim());
    fd.append("activo",      document.getElementById("activo").checked ? "true" : "false");
    const file = document.getElementById("imagen").files[0];
    if (file) fd.append("imagen", file);

    const res = await authFetch("/api/admin/categorias", { method: "POST", body: fd });
    if (!res || !res.ok) {
        let msg = "Error al crear la categoría.";
        try { const d = await res.json(); if (d.message) msg = d.message; } catch (_) {}
        mostrarErrorGeneral(msg);
        return;
    }
    location.href = "index.html";
}
