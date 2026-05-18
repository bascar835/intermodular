function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargarSelects(reserva) {
    const resUsers = await authFetch("/api/admin/users");
    if (!resUsers) return;
    const usuarios = await resUsers.json();
    const selUsuario = document.getElementById("usuario_id");
    usuarios.forEach(u => {
        const opt = document.createElement("option");
        opt.value = u.id;
        opt.textContent = `#${u.id} — ${u.name || u.nombre} (${u.email})`;
        if (u.id === reserva.usuario_id) opt.selected = true;
        selUsuario.appendChild(opt);
    });

    const resExp = await authFetch("/api/admin/experiencias");
    if (!resExp) return;
    const experiencias = await resExp.json();
    const selExp = document.getElementById("experiencia_id");
    experiencias.forEach(e => {
        const opt = document.createElement("option");
        opt.value = e.id;
        opt.textContent = `#${e.id} — ${e.titulo} (${e.precio}€)`;
        if (e.id === reserva.experiencia_id) opt.selected = true;
        selExp.appendChild(opt);
    });
}

async function cargar() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/reservas/${id}`);
    if (!response) return;
    const r = await response.json();

    document.getElementById("fecha_reserva").value   = r.fecha_reserva ? r.fecha_reserva.substring(0, 16) : "";
    document.getElementById("numero_personas").value = r.numero_personas;
    document.getElementById("precio_total").value    = r.precio_total;
    document.getElementById("estado").value          = r.estado;

    await cargarSelects(r);
}

async function guardar(e) {
    e.preventDefault();
    limpiarTodosLosErrores();

    const ok = validarFormulario([
        { id: "usuario_id",      label: "Usuario",            tipo: "select" },
        { id: "experiencia_id",  label: "Experiencia",        tipo: "select" },
        { id: "fecha_reserva",   label: "Fecha Reserva",      tipo: "datetime" },
        { id: "numero_personas", label: "Número de personas", tipo: "entero" },
        { id: "precio_total",    label: "Precio total",       tipo: "numero" },
        { id: "estado",          label: "Estado",             tipo: "select" },
    ]);
    if (!ok) return;

    const id = obtenerId();
    const body = {
        usuario_id:      parseInt(document.getElementById("usuario_id").value),
        experiencia_id:  parseInt(document.getElementById("experiencia_id").value),
        fecha_reserva:   document.getElementById("fecha_reserva").value,
        numero_personas: parseInt(document.getElementById("numero_personas").value),
        precio_total:    parseFloat(document.getElementById("precio_total").value),
        estado:          document.getElementById("estado").value
    };

    const res = await authFetch(`/api/admin/reservas/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(body)
    });

    if (res && res.ok) {
        location.href = "index.html";
    } else if (res) {
        const data = await res.json();
        mostrarErrorGeneral(data.message || JSON.stringify(data.errors));
    }
}

cargar();
