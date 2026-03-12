async function cargarCategorias() {

    const response = await fetch("/api/admin/categorias");
    const peliculas = await response.json();

    const tabla = document.getElementById("tabla-categorias");

    peliculas.forEach(c => {

        const tr = document.createElement("tr");

		tr.innerHTML = `
					<td>${c.id}</td>
		            <td>${c.nombre}</td>
		            <td>${c.descripcion}</td>
					<td>${c.activa}</td>
		            <td>

		                <a href="show.html?id=${c.id}"
		                   class="btn btn-sm btn-info">
		                   Ver
		                </a>

		                <a href="edit.html?id=${c.id}"
		                   class="btn btn-sm btn-warning">
		                   Editar
		                </a>

		                <button class="btn btn-sm btn-danger"
		                        onclick="eliminar(${c.id})">
		                        Eliminar
		                </button>

		            </td>
		        `;

        tabla.appendChild(tr);
    });
}

async function eliminar(id) {

    if (!confirm("¿Eliminar esta categoria?")) return;

    await fetch(`/api/admin/categorias/${id}`, {
        method: "DELETE"
    });

    location.reload();
}

cargarCategorias();