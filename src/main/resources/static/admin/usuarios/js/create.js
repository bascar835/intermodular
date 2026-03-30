// Guardar nuevo usuario
async function guardarUsuario(e) {
    e.preventDefault();

    const user = {
        name: document.getElementById("name").value,
        email: document.getElementById("email").value,
        role: document.getElementById("role").value,
        password: document.getElementById("password").value
    };

    await fetch("/api/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(user)
    });

    location.href = "index.html"; // volver al listado
}