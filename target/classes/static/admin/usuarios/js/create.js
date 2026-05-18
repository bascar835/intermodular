async function guardar(e) {
    e.preventDefault();
    limpiarTodosLosErrores();

    const ok = validarFormulario([
        { id: "nombre",   label: "Nombre" },
        { id: "email",    label: "Email",      tipo: "email" },
        { id: "password", label: "Contraseña" },
        { id: "rol",      label: "Rol",        tipo: "select" },
    ]);
    if (!ok) return;

    const response = await authFetch("/api/admin/users", {
        method: "POST",
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
        mostrarErrorCampo(document.getElementById("email"), "Este correo ya está en uso. Introduce otro email.");
        return;
    }

    if (response.ok) {
        location.href = "index.html";
    } else {
        const data = await response.json().catch(() => ({}));
        mostrarErrorGeneral(data.message || "Error al crear el usuario. Revisa los datos.");
    }
}
