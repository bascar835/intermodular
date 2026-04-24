async function guardar(e) {
    e.preventDefault();

    const response = await authFetch("/api/admin/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            name: document.getElementById("nombre").value,
            email: document.getElementById("email").value,
            password: document.getElementById("password").value,
            role: document.getElementById("rol").value
        })
    });

    if (!response) return;

    if (response.status === 409) {
        // Email duplicado
        alert("Este correo ya está en uso. Introduce otro email.");
        document.getElementById("email").focus();
        return;
    }

    if (response.ok) {
        location.href = "index.html";
    } else {
        const msg = await response.text();
        alert("Error al crear el usuario: " + (msg || "Revisa los datos."));
    }
}
