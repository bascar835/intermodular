async function cargarSelects() {
    // Cargar usuarios
    const resUsers = await authFetch("/api/admin/users");
    if (!resUsers) return;
    const usuarios = await resUsers.json();
    const selUsuario = document.getElementById("usuario_id");
    usuarios.forEach(u => {
        const opt = document.createElement("option");
        opt.value = u.id;
        opt.textContent = `#${u.id} — ${u.name || u.nombre} (${u.email})`;
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
        selExp.appendChild(opt);
    });
}

async function guardar(e) {
    e.preventDefault();

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

cargarSelects();
