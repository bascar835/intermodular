async function cargarCategorias() {

    const response = await fetch("/api/categorias");
    const peliculas = await response.json();

    const lista = document.getElementById("lista-categorias");

    peliculas.forEach(c => {

        const li = document.createElement("li");

        // clase de Bootstrap
        li.classList.add("list-group-item");

        li.innerHTML =
            `<a href="show.html?id=${c.id}">
                ${c.nombre} (${c.descripcion}) ${c.activa}
             </a>`;

        lista.appendChild(li);
    });
}

cargarCategorias();