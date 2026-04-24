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
    // Comprobar si tiene reservas
    const checkRes = await authFetch(`/api/admin/users/${id}/has-reservas`);
    const tieneReservas = checkRes && await checkRes.json();

    if (tieneReservas) {
        const migrar = confirm(
            "Este usuario tiene reservas activas.\n\n" +
            "¿Deseas migrar sus reservas a otro usuario antes de eliminar?\n\n" +
            "Pulsa Aceptar para migrar, Cancelar para eliminar sin migrar."
        );

        if (migrar) {
            const destino = prompt("Introduce el ID del usuario destino para las reservas:");
            if (!destino) return;

            const res = await authFetch(
                `/api/admin/users/${id}?migrateTo=${destino}`,
                { method: "DELETE" }
            );
            const msg = await res.text();
            alert(res.ok ? "Usuario eliminado y reservas migradas correctamente." : msg);
        } else {
            const confirmar = confirm(
                "¿Seguro que deseas eliminar el usuario SIN migrar sus reservas?\n" +
                "Las reservas quedarán huérfanas."
            );
            if (!confirmar) return;

            const res = await authFetch(`/api/admin/users/${id}`, { method: "DELETE" });
            const msg = await res.text();
            alert(res.ok ? "Usuario eliminado." : msg);
        }
    } else {
        if (!confirm("¿Eliminar este usuario?")) return;

        const res = await authFetch(`/api/admin/users/${id}`, { method: "DELETE" });
        if (res && res.ok) {
            // ok
        } else {
            const msg = await res.text();
            // Puede ser error de último admin
            alert(msg || "Error al eliminar el usuario");
        }
    }

    cargarUsuarios();
}

cargarUsuarios();
