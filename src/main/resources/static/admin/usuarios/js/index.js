async function cargarUsuarios() {
    const response = await authFetch("/api/admin/users");
    if (!response) return;
    const usuarios = await response.json();

    const tabla = document.getElementById("tabla-usuarios");
    tabla.innerHTML = "";

    usuarios.forEach(u => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${u.id}</td>
            <td>${u.name}</td>
            <td>${u.email}</td>
            <td>${u.role}</td>
            <td>
                <a href="show.html?id=${u.id}" class="btn btn-sm btn-info">Ver</a>
                <a href="edit.html?id=${u.id}" class="btn btn-sm btn-warning">Editar</a>
                <button class="btn btn-sm btn-danger" onclick="eliminar(${u.id})">Eliminar</button>
            </td>
        `;
        tabla.appendChild(tr);
    });
}

async function eliminar(id) {
    if (!confirm("¿Eliminar este usuario?")) return;
    const res = await authFetch(`/api/admin/users/${id}`, { method: "DELETE" });
    if (res && res.ok) {
        location.reload();
    } else {
        alert("Error al eliminar el usuario");
    }
}

cargarUsuarios();
