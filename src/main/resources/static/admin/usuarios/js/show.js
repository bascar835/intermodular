function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargarUsuario() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/users/${id}`);
    if (!response) return;
    const u = await response.json();

    document.getElementById("titulo-header").textContent = u.name;
    document.getElementById("nombre").textContent = u.name;
    document.getElementById("email").textContent = u.email;
    document.getElementById("rol").textContent = u.role;
    document.getElementById("editar").href = `edit.html?id=${id}`;
}

cargarUsuario();
