async function guardar(e) {
    e.preventDefault();

    await authFetch("/api/admin/reservas", {
        method: "POST",
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
