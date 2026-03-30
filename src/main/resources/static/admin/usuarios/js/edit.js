function obtenerId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

// Cargar usuario en el formulario
async function cargarUsuario() {
    const id = obtenerId();
    const response = await fetch(`/api/users/${id}`);
    const u = await response.json();

    document.getElementById("name").value = u.name;
    document.getElementById("email").value = u.email;
    document.getElementById("role").value = u.role;
    document.getElementById("password").value = ""; // nueva contraseña opcional
}

// Guardar cambios
async function guardarUsuario(e) {
    e.preventDefault();

    const id = obtenerId();
    const user = {
        name: document.getElementById("name").value,
        email: document.getElementById("email").value,
        role: document.getElementById("role").value,
        password: document.getElementById("password").value
    };

    await fetch(`/api/users/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(user)
    });

    location.href = "index.html";
}

async function init() {
    await cargarUsuario();
    document.getElementById("form-edit").addEventListener("submit", guardarUsuario);
}

init();