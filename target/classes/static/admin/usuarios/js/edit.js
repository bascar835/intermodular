function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargar() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/users/${id}`);
    if (!response) return;
    const u = await response.json();

    document.getElementById("nombre").value   = u.name;
    document.getElementById("email").value    = u.email;
    document.getElementById("rol").value      = u.role;
    document.getElementById("password").value = "";
}

async function guardar(e) {
    e.preventDefault();
    limpiarTodosLosErrores();

    const ok = validarFormulario([
        { id: "nombre", label: "Nombre" },
        { id: "email",  label: "Email",  tipo: "email" },
        { id: "rol",    label: "Rol",    tipo: "select" },
        // Contraseña: opcional en edición
    ]);
    if (!ok) return;

    const id = obtenerId();
    const response = await authFetch(`/api/admin/users/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            name:     document.getElementById("nombre").value.trim(),
            email:    document.getElementById("email").value.trim(),
            password: document.getElementById("password").value,
            role:     document.getElementById("rol").value
        })
    });

    if (!response) return;

    if (response.status === 409) {
        mostrarErrorCampo(document.getElementById("email"), "Ese correo ya pertenece a otro usuario. Elige uno diferente.");
        return;
    }

    if (response.ok) {
        location.href = "index.html";
    } else {
        const data = await response.json().catch(() => ({}));
        mostrarErrorGeneral(data.message || "Error al guardar. Revisa los datos.");
    }
}

cargar();
