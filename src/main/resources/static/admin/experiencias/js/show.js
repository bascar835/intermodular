function obtenerId() {
    return new URLSearchParams(window.location.search).get("id");
}

async function cargarExperiencia() {
    const id = obtenerId();
    const response = await authFetch(`/api/admin/experiencias/${id}`);
    if (!response) return;
    const exp = await response.json();

    document.getElementById("titulo-header").textContent = exp.titulo;
    document.getElementById("titulo").textContent        = exp.titulo;
    document.getElementById("descripcion").textContent   = exp.descripcion || "-";
    document.getElementById("precio").textContent        = exp.precio;
    document.getElementById("ubicacion").textContent     = exp.ubicacion || "-";
    document.getElementById("duracion_horas").textContent= exp.duracion_horas;
    document.getElementById("categoria_id").textContent  = exp.categoria_id;

    // Galería
    if (exp.imagenes && exp.imagenes.length > 0) {
        const wrap      = document.getElementById("galeria-wrap");
        const principal = document.getElementById("galeria-principal");
        const thumbsEl  = document.getElementById("galeria-thumbs");

        wrap.style.display = "block";
        principal.src = exp.imagenes[0].url;

        function activar(url, thumb) {
            principal.style.opacity = "0";
            setTimeout(() => { principal.src = url; principal.style.opacity = "1"; }, 150);
            document.querySelectorAll(".adm-thumb").forEach(t => t.style.outline = "none");
            if (thumb) thumb.style.outline = "2px solid #e11d48";
        }

        exp.imagenes.forEach((img, i) => {
            const th = document.createElement("img");
            th.src = img.url;
            th.alt = `Imagen ${i + 1}`;
            th.className = "adm-thumb";
            th.style.cssText = "width:72px;height:54px;object-fit:cover;border-radius:6px;cursor:pointer;opacity:.6;transition:opacity .2s,transform .15s;";
            if (i === 0) th.style.outline = "2px solid #e11d48";
            th.addEventListener("mouseover", () => th.style.opacity = "1");
            th.addEventListener("mouseout",  () => th.style.opacity = th.style.outline ? "1" : ".6");
            th.addEventListener("click", () => activar(img.url, th));
            thumbsEl.appendChild(th);
        });
    }

    document.getElementById("editar").href = `edit.html?id=${id}`;
}

cargarExperiencia();
