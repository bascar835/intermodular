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

async function cargarCategorias(selectedId) {
    const response = await authFetch("/api/admin/categorias");
    if (!response) return;
    const categorias = await response.json();
    const select = document.getElementById("categoria_id");
    categorias.forEach(c => {
        const option = document.createElement("option");
        option.value = c.id;
        option.textContent = c.activo ? c.nombre : `${c.nombre} (inactiva)`;
        if (!c.activo) { option.disabled = true; option.style.color = "#9ca3af"; }
        if (c.id === selectedId) option.selected = true;
        select.appendChild(option);
    });
}

function renderGaleria(imagenes) {
    const galeriaEl = document.getElementById("galeria-actual");
    const sinImg    = document.getElementById("sin-imagen");
    galeriaEl.innerHTML = "";

    if (!imagenes || imagenes.length === 0) { sinImg.style.display = "inline"; return; }
    sinImg.style.display = "none";

    imagenes.forEach(img => {
        const wrap = document.createElement("div");
        wrap.style.cssText = "position:relative;display:inline-block;";
        const im = document.createElement("img");
        im.src = img.url;
        im.style.cssText = "width:100px;height:75px;object-fit:cover;border-radius:6px;display:block;";
        const btn = document.createElement("button");
        btn.textContent = "✕"; btn.type = "button"; btn.title = "Eliminar imagen";
        btn.style.cssText = "position:absolute;top:3px;right:3px;background:rgba(0,0,0,.65);color:#fff;border:none;border-radius:50%;width:20px;height:20px;font-size:11px;cursor:pointer;line-height:1;padding:0;";
        btn.addEventListener("click", () => eliminarImagen(img.id, wrap));
        wrap.appendChild(im); wrap.appendChild(btn); galeriaEl.appendChild(wrap);
    });
}

async function eliminarImagen(imgId, wrapEl) {
    if (!confirm("¿Eliminar esta imagen?")) return;
    const id = obtenerId();
    const res = await authFetch(`/api/admin/experiencias/${id}/imagenes/${imgId}`, { method: "DELETE" });
    if (res && (res.ok || res.status === 204)) {
        wrapEl.remove();
        if (document.getElementById("galeria-actual").children.length === 0)
            document.getElementById("sin-imagen").style.display = "inline";
    }
}

async function cargar() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/experiencias/${id}`);
    if (!response) return;
    const exp = await response.json();

    document.getElementById("titulo").value         = exp.titulo;
    document.getElementById("descripcion").value    = exp.descripcion || "";
    document.getElementById("precio").value         = exp.precio;
    document.getElementById("ubicacion").value      = exp.ubicacion || "";
    document.getElementById("duracion_horas").value = exp.duracion_horas;

    renderGaleria(exp.imagenes);
    await cargarCategorias(exp.categoria_id);
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

    const id = obtenerId();
    const fd = new FormData();
    fd.append("titulo",         document.getElementById("titulo").value.trim());
    fd.append("descripcion",    document.getElementById("descripcion").value.trim());
    fd.append("precio",         document.getElementById("precio").value);
    fd.append("ubicacion",      document.getElementById("ubicacion").value.trim());
    fd.append("duracion_horas", document.getElementById("duracion_horas").value);
    fd.append("categoria_id",   document.getElementById("categoria_id").value);
    const file = document.getElementById("imagen").files[0];
    if (file) fd.append("imagen", file);

    const res = await authFetch(`/api/admin/experiencias/${id}`, { method: "PUT", body: fd });
    if (!res || !res.ok) {
        let msg = "Error al guardar los cambios.";
        try { const d = await res.json(); if (d.message) msg = d.message; } catch (_) {}
        mostrarErrorGeneral(msg);
        return;
    }
    location.href = "index.html";
}

cargar();
