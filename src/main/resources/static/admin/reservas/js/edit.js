function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargarSelects(reserva) {
    // Cargar usuarios
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

    // Cargar experiencias
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

    // Rellenar campos simples
    document.getElementById("fecha_reserva").value   = r.fecha_reserva
        ? r.fecha_reserva.substring(0, 16)
        : "";
    document.getElementById("numero_personas").value = r.numero_personas;
    document.getElementById("precio_total").value    = r.precio_total;
    document.getElementById("estado").value          = r.estado;

    // Cargar selects con el valor actual preseleccionado
    await cargarSelects(r);
}

async function guardar(e) {
    e.preventDefault();
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
        mostrarError(data.message || JSON.stringify(data.errors));
    }
}

function mostrarError(msg) {
    let el = document.getElementById("form-error");
    if (!el) {
        el = document.createElement("p");
        el.id = "form-error";
        el.style.cssText = "color:red;margin-top:12px;font-weight:600;";
        document.querySelector(".admin-form").appendChild(el);
    }
    el.textContent = msg;
}

cargar();
