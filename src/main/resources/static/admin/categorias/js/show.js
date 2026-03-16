function obtenerId() {

    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

async function cargarPelicula() {

    const id = obtenerId();

    const response = await fetch(`/api/admin/categorias/${id}`);
    const c = await response.json();

    document.getElementById("nombre").textContent = c.nombre;
    document.getElementById("descripcion").textContent = c.descripcion;
    document.getElementById("activa").textContent = c.activa ? "Si":"No";

    document.getElementById("editar").href = `edit.html?id=${id}`;
}

cargarPelicula();