function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargar() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/reservas/${id}`);
    if (!response) return;

    const r = await response.json();

    usuario_id.value = r.usuario_id;
    experiencia_id.value = r.experiencia_id;
    fecha_reserva.value = r.fecha_reserva.substring(0, 16);
    numero_personas.value = r.numero_personas;
    precio_total.value = r.precio_total;
    estado.value = r.estado;
}

async function guardar(e) {
    e.preventDefault();
    const id = obtenerId();

    await authFetch(`/api/admin/reservas/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            usuario_id: parseInt(usuario_id.value),
            experiencia_id: parseInt(experiencia_id.value),
            fecha_reserva: fecha_reserva.value,
            numero_personas: parseInt(numero_personas.value),
            precio_total: parseFloat(precio_total.value),
            estado: estado.value
        })
    });

    location.href = "index.html";
}

cargar();
