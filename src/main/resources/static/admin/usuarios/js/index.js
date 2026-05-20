async function cargarUsuarios() {
    const response = await authFetch("/api/admin/users");
    if (!response) return;
    const usuarios = await response.json();

    const tabla = document.getElementById("tabla-usuarios");
    tabla.innerHTML = "";

    usuarios.forEach(u => {
        const tr = document.createElement("tr");
        tr.innerHTML = `
            <td>${u.name}</td>
            <td>${u.email}</td>
            <td>${u.role.replace("ROLE_", "")}</td>
            <td>
                <a href="show.html?id=${u.id}" title="Ver detalle" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">👁️‍🗨️</a>
                <a href="edit.html?id=${u.id}" title="Editar" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">🖋️</a>
                <button onclick="eliminar(${u.id})" title="Eliminar" style="font-size:1.45rem;line-height:1;padding:3px 6px;cursor:pointer;background:none;border:none;border-radius:6px;text-decoration:none;">🗑️</button>
            </td>
        `;
        tabla.appendChild(tr);
    });
}

async function eliminar(id) {
    if (!confirm("¿Eliminar este usuario? Se eliminarán también todas sus reservas asociadas.")) return;

    const res = await authFetch(`/api/admin/users/${id}`, { method: "DELETE" });
    const msg = await res.text();

    if (res.ok) {
        alert("Usuario y sus reservas eliminados correctamente.");
    } else {
        alert(msg || "Error al eliminar el usuario.");
    }

    cargarUsuarios();
}

cargarUsuarios();
