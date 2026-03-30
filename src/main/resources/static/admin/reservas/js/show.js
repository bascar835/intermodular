function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargarReserva() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/reservas/${id}`);
    if (!response) return;

    const r = await response.json();

    document.getElementById("titulo-header").textContent = `Reserva #${r.id}';
    document.getElementById("id").textContent = r.id;
    document.getElementById("usuario_id").textContent = r.usuario_id;
    document.getElementById("experiencia_id").textContent = r.experiencia_id;
    document.getElementById("fecha_reserva").textContent = r.fecha_reserva;
    document.getElementById("numero_personas").textContent = r.numero_personas;
    document.getElementById("precio_total").textContent = r.precio_total + " €";
    document.getElementById("estado").textContent = r.estado;
    document.getElementById("editar").href = `edit.html?id=${id}`;
}

cargarReserva();
