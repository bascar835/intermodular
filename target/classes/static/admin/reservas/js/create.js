async function cargarSelects() {
    const resUsers = await authFetch("/api/admin/users");
    if (!resUsers) return;
    const usuarios = await resUsers.json();
    const selUsuario = document.getElementById("usuario_id");
    const defU = document.createElement("option"); defU.value = ""; defU.textContent = "— Selecciona un usuario —"; selUsuario.appendChild(defU);
    usuarios.forEach(u => {
        const opt = document.createElement("option");
        opt.value = u.id;
        opt.textContent = `#${u.id} — ${u.name || u.nombre} (${u.email})`;
        selUsuario.appendChild(opt);
    });

    const resExp = await authFetch("/api/admin/experiencias");
    if (!resExp) return;
    const experiencias = await resExp.json();
    const selExp = document.getElementById("experiencia_id");
    const defE = document.createElement("option"); defE.value = ""; defE.textContent = "— Selecciona una experiencia —"; selExp.appendChild(defE);
    experiencias.forEach(e => {
        const opt = document.createElement("option");
        opt.value = e.id;
        opt.textContent = `#${e.id} — ${e.titulo} (${e.precio}€)`;
        selExp.appendChild(opt);
    });
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

    const body = {
        usuario_id:      parseInt(document.getElementById("usuario_id").value),
        experiencia_id:  parseInt(document.getElementById("experiencia_id").value),
        fecha_reserva:   document.getElementById("fecha_reserva").value,
        numero_personas: parseInt(document.getElementById("numero_personas").value),
        precio_total:    parseFloat(document.getElementById("precio_total").value),
        estado:          document.getElementById("estado").value
    };

    const res = await authFetch("/api/admin/reservas", {
        method: "POST",
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

cargarSelects();
