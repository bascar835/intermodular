async function cargarReservas() {
    const response = await authFetch("/api/admin/reservas");
    if (!response) return;

    const reservas = await response.json();

    const tabla = document.getElementById("tabla-reservas");
    tabla.innerHTML = "";

    reservas.forEach(r => {
        // Formatear fecha: reemplazar la T por un espacio y quitar segundos
        const fechaRaw = r.fecha_reserva || "";
        const fechaFormateada = fechaRaw.replace("T", " ").substring(0, 16);

        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${r.usuario_id}</td>
            <td>${r.experiencia_id}</td>
            <td>${fechaFormateada}</td>
            <td>${r.numero_personas}</td>
            <td>${r.precio_total} €</td>
            <td>${r.estado}</td>
            <td>
                <a href="show.html?id=${r.id}" title="Ver detalle" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">👁️‍🗨️</a>
                <a href="edit.html?id=${r.id}" title="Editar" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">🖋️</a>
                <button onclick="eliminar(${r.id})" title="Eliminar" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">🗑️</button>
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
