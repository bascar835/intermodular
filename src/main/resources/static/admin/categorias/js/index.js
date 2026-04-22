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

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${c.id}</td>
            
            <td>${c.nombre}</td>
            <td>${c.descripcion ?? "-"}</td>
			<td>${imgHtml}</td>
            <td>
                <a href="show.html?id=${c.id}" class="btn-sm btn-info">Ver</a>
                <a href="edit.html?id=${c.id}" class="btn-sm btn-warning">Editar</a>
                <button class="btn-sm btn-danger" onclick="eliminar(${c.id})">Eliminar</button>
            </td>
        `;
        tabla.appendChild(tr);
    });
}

async function eliminar(id) {
    if (!confirm("¿Eliminar esta categoría?")) return;
    await authFetch(`/api/admin/categorias/${id}`, { method: "DELETE" });
    location.reload();
}

cargarCategorias();
