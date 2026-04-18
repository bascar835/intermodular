function previewImagen() {
    const file = document.getElementById("imagen").files[0];
    const preview = document.getElementById("preview");

    if (!file) {
        preview.style.display = "none";
        preview.src = "";
        return;
    }

    preview.src = URL.createObjectURL(file);
    preview.style.display = "block";
}

async function guardar(e) {
    e.preventDefault();

    const fd = new FormData();
    fd.append("nombre",      document.getElementById("nombre").value);
    fd.append("descripcion", document.getElementById("descripcion").value);

    const file = document.getElementById("imagen").files[0];
    if (file) {
        fd.append("imagen", file);
    }

    await authFetch("/api/admin/categorias", {
        method: "POST",
        body: fd
    });

    location.href = "index.html";
}
