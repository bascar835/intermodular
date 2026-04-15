async function cargarExperiencias() {
    const response = await authFetch("/api/admin/experiencias");
    if (!response) return;
    const experiencias = await response.json();

    const tabla = document.getElementById("tabla-experiencias");
    tabla.innerHTML = "";

    experiencias.forEach(p => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${p.id}</td>
            <td>${p.titulo}</td>
            <td>${p.precio} €</td>
            <td>${p.ubicacion}</td>
            <td>${p.categoria}</td>
            <td>
                <a href="show.html?id=${p.id}" class="btn-sm btn-info">Ver</a>
                <a href="edit.html?id=${p.id}" class="btn-sm btn-warning">Editar</a>
                <button class="btn-sm btn-danger" onclick="eliminar(${p.id})">Eliminar</button>
            </td>
        `;
        tabla.appendChild(tr);
    });
}

async function eliminar(id) {
    if (!confirm("¿Eliminar esta experiencia?")) return;
    await authFetch(`/api/admin/experiencias/${id}`, { method: "DELETE" });
    location.reload();
}

cargarExperiencias();
