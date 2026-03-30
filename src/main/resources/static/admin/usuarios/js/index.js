// Cargar usuarios desde la API
async function cargarUsuarios() {
    try {
        const response = await fetch("/api/users");
        const usuarios = await response.json();

        const tabla = document.getElementById("tabla-usuarios");
        tabla.innerHTML = ""; // Limpiar tabla

        usuarios.forEach(u => {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td>${u.name}</td>
                <td>${u.email}</td>
                <td>${u.role}</td>
                <td>
                    <a href="show.html?id=${u.id}" class="btn btn-sm btn-info">Ver</a>
                    <a href="edit.html?id=${u.id}" class="btn btn-sm btn-warning">Editar</a>
                    <button class="btn btn-sm btn-danger" onclick="eliminarUsuario(${u.id})">Eliminar</button>
                </td>
            `;
            tabla.appendChild(tr);
        });

    } catch (error) {
        console.error("Error cargando usuarios:", error);
    }
}

// Eliminar usuario
async function eliminarUsuario(id) {
    if (!confirm("¿Eliminar este usuario?")) return;

    try {
        await fetch(`/api/users/${id}`, { method: "DELETE" });
        cargarUsuarios(); // recarga tabla
    } catch (error) {
        console.error("Error eliminando usuario:", error);
    }
}

// Ejecutar al cargar la página
cargarUsuarios();