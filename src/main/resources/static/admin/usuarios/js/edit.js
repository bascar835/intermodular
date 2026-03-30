function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargar() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/users/${id}`);
    if (!response) return;
    const u = await response.json();

    document.getElementById("nombre").value = u.name;
    document.getElementById("email").value = u.email;
    document.getElementById("rol").value = u.role;
    document.getElementById("password").value = "";
}

async function guardar(e) {
    e.preventDefault();
    const id = obtenerId();

    await authFetch(`/api/admin/users/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            name: document.getElementById("nombre").value,
            email: document.getElementById("email").value,
            password: document.getElementById("password").value,
            role: document.getElementById("rol").value
        })
    });

    location.href = "index.html";
}

cargar();
