async function guardar(e) {
    e.preventDefault();

    await authFetch("/api/admin/categorias", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            nombre: nombre.value,
            descripcion: descripcion.value
        })
    });

    location.href = "index.html";
}
