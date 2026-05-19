async function cargarCategorias() {
    const response = await authFetch("/api/admin/categorias");
    if (!response) return;
    const categorias = await response.json();

    const tabla = document.getElementById("tabla-categorias");
    tabla.innerHTML = "";

    categorias.forEach(c => {
        const imgHtml = c.imagen_url
            ? `<img src="${c.imagen_url}" style="width:56px;height:42px;object-fit:cover;border-radius:5px;">`
            : `<span style="color:#9ca3af;font-size:.75rem;">Sin imagen</span>`;

        const activoBadge = c.activo
            ? `<span class="badge-activo">✔ Activa</span>`
            : `<span class="badge-inactivo">✘ Inactiva</span>`;

        const toggleLabel = c.activo ? "🟥" : "🟩";
        const toggleClass = c.activo ? "btn-sm btn-warning" : "btn-sm btn-success";

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${c.id}</td>
            <td>${c.nombre}</td>
            <td>${c.descripcion ?? "-"}</td>
            <td>${imgHtml}</td>
            <td>${activoBadge}</td>
            <td>
                <a href="show.html?id=${c.id}" title="Ver detalle" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">👁️‍🗨️</a>
                <a href="edit.html?id=${c.id}" title="Editar" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">🖋️</a>
                <button onclick="toggleActivo(${c.id}, ${c.activo})" title="${c.activo ? 'Desactivar' : 'Activar'}" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">${toggleLabel}</button>
                <button onclick="eliminar(${c.id})" title="Eliminar" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">🗑️</button>
            </td>
        `;
        tabla.appendChild(tr);
    });
}

async function toggleActivo(id, activoActual) {
    const accion = activoActual ? "desactivar" : "activar";
    if (!confirm(`¿Deseas ${accion} esta categoría?`)) return;

    const res = await authFetch(`/api/admin/categorias/${id}/toggle`, { method: "PATCH" });
    if (res && res.ok) {
        cargarCategorias();
    } else {
        alert("Error al cambiar el estado de la categoría.");
    }
}

async function eliminar(id) {
    if (!confirm("¿Eliminar esta categoría? Esta acción no se puede deshacer.")) return;
    await authFetch(`/api/admin/categorias/${id}`, { method: "DELETE" });
    location.reload();
}

cargarCategorias();
