async function cargarReservas() {
    const response = await authFetch("/api/admin/reservas");
    if (!response) return;

    const reservas = await response.json();

    const tabla = document.getElementById("tabla-reservas");
    tabla.innerHTML = "";

    reservas.forEach(r => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${r.id}</td>
            <td>${r.usuario_id}</td>
            <td>${r.experiencia_id}</td>
            <td>${r.fecha_reserva}</td>
            <td>${r.numero_personas}</td>
            <td>${r.precio_total} €</td>
            <td>${r.estado}</td>
            <td>
                <a href="show.html?id=${r.id}" class="btn btn-sm btn-info">Ver</a>
                <a href="edit.html?id=${r.id}" class="btn btn-sm btn-warning">Editar</a>
                <button class="btn btn-sm btn-danger" onclick="eliminar(${r.id})">Eliminar</button>
            </td>
        `;
        tabla.appendChild(tr);
    });
}

async function eliminar(id) {
    if (!confirm("¿Eliminar esta reserva?")) return;

    await authFetch(`/api/admin/reservas/${id}`, {
        method: "DELETE"
    });

    location.reload();
}

cargarReservas();
