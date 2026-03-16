async function guardar(e) {
    e.preventDefault();

    const categoria = {
        nombre: nombre.value,
        descripcion: descripcion.value,
        activa: activa.checked
    };

    await fetch("/api/admin/categorias", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(categoria)
    });

    location.href = "index.html";
}