function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargarReserva() {
    const id = obtenerId();

    if (!id) {
        document.querySelector(".admin-card").innerHTML = "<p style='color:red'>No se proporcionó un ID de reserva.</p>";
        return;
    }

    const response = await authFetch(`/api/admin/reservas/${id}`);
    if (!response) return;

    if (!response.ok) {
        document.querySelector(".admin-card").innerHTML = "<p style='color:red'>Reserva no encontrada.</p>";
        return;
    }

    const r = await response.json();

    // Actualizar el título del topbar con el número de reserva
    document.querySelector(".page-title").textContent = `Reserva #${r.id}`;

    document.getElementById("id").textContent             = r.id;
    document.getElementById("usuario_id").textContent     = r.usuario_id;
    document.getElementById("experiencia_id").textContent = r.experiencia_id;
    document.getElementById("fecha_reserva").textContent  = r.fecha_reserva
        ? r.fecha_reserva.replace("T", " ").substring(0, 16)
        : "—";
    document.getElementById("numero_personas").textContent = r.numero_personas;
    document.getElementById("precio_total").textContent   = parseFloat(r.precio_total).toFixed(2) + " €";
    document.getElementById("estado").textContent         = r.estado;
    document.getElementById("editar").href                = `edit.html?id=${id}`;
}

cargarReserva();
