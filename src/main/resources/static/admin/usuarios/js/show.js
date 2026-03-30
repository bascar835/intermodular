function obtenerId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

async function cargarUsuario() {
    const id = obtenerId();
    const response = await fetch(`/api/users/${id}`);
    const u = await response.json();

    document.getElementById("name").textContent = u.name;
    document.getElementById("email").textContent = u.email;
    document.getElementById("role").textContent = u.role;

    document.getElementById("editar").href = `edit.html?id=${id}`;
}

cargarUsuario();