function previewImagen() {
    const file = document.getElementById("imagen").files[0];
    const preview = document.getElementById("preview");
    if (!file) { preview.style.display = "none"; preview.src = ""; return; }
    preview.src = URL.createObjectURL(file);
    preview.style.display = "block";
}

async function cargarCategorias() {
    const response = await authFetch("/api/admin/categorias");
    if (!response) return;
    const categorias = await response.json();

    const select = document.getElementById("categoria_id");
    // Opción vacía por defecto para forzar selección
    const defaultOpt = document.createElement("option");
    defaultOpt.value = "";
    defaultOpt.textContent = "— Selecciona una categoría —";
    select.appendChild(defaultOpt);

    categorias.forEach(c => {
        const option = document.createElement("option");
        option.value = c.id;
        option.textContent = c.activo ? c.nombre : `${c.nombre} (inactiva)`;
        if (!c.activo) { option.disabled = true; option.style.color = "#9ca3af"; }
        select.appendChild(option);
    });
}

async function guardar(e) {
    e.preventDefault();
    limpiarTodosLosErrores();

    const ok = validarFormulario([
        { id: "titulo",         label: "Título" },
        { id: "descripcion",    label: "Descripción" },
        { id: "precio",         label: "Precio",           tipo: "numero" },
        { id: "ubicacion",      label: "Ubicación" },
        { id: "duracion_horas", label: "Duración (horas)", tipo: "entero" },
        { id: "categoria_id",   label: "Categoría",        tipo: "select" },
    ]);
    if (!ok) return;

    const fd = new FormData();
    fd.append("titulo",         document.getElementById("titulo").value.trim());
    fd.append("descripcion",    document.getElementById("descripcion").value.trim());
    fd.append("precio",         document.getElementById("precio").value);
    fd.append("ubicacion",      document.getElementById("ubicacion").value.trim());
    fd.append("duracion_horas", document.getElementById("duracion_horas").value);
    fd.append("categoria_id",   document.getElementById("categoria_id").value);
    const file = document.getElementById("imagen").files[0];
    if (file) fd.append("imagen", file);

    const res = await authFetch("/api/admin/experiencias", { method: "POST", body: fd });
    if (!res || !res.ok) {
        let msg = "Error al crear la experiencia.";
        try { const d = await res.json(); if (d.message) msg = d.message; } catch (_) {}
        mostrarErrorGeneral(msg);
        return;
    }
    location.href = "index.html";
}

cargarCategorias();
