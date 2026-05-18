const IMG_PLACEHOLDER = 'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=600&q=80';

function iconoCategoria(nombre) {
  if (!nombre) return '🌍';
  const n = nombre.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
  if (n.includes('aventura')) return '🏔';
  if (n.includes('cultura'))  return '🎭';
  if (n.includes('relax'))    return '🧘';
  if (n.includes('gastro'))   return '🍽';
  if (n.includes('deporte'))  return '⚽';
  if (n.includes('musica'))   return '🎵';
  return '🌍';
}

function esc(s) {
  return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

async function cargarCategorias() {
  try {
    const [resCat, resExp] = await Promise.all([
      fetch('/api/categorias'),
      fetch('/api/experiencias')
    ]);

    const categorias   = await resCat.json();
    const datosExp     = await resExp.json();
    const experiencias = Array.isArray(datosExp) ? datosExp : (datosExp.data ?? []);

    // Contar experiencias por categoria_id
    const conteo = {};
    experiencias.forEach(e => {
      conteo[e.categoria_id] = (conteo[e.categoria_id] || 0) + 1;
    });

    const grid = document.getElementById('cat-grid');
    grid.innerHTML = '';

    categorias.forEach(c => {
      const imgUrl = c.imagen_url || IMG_PLACEHOLDER;
      const total  = conteo[c.id] ?? 0;
      const icono  = iconoCategoria(c.nombre);

      const a = document.createElement('a');
      a.className = 'cat-card';
      a.href = `/experiencias/index.html?cat=${c.id}`;
      a.innerHTML = `
        <div class="cat-img" style="background-image:url('${imgUrl}')">
          <div class="cat-img-overlay"></div>
          <span class="cat-img-icon">${icono}</span>
        </div>
        <div class="cat-body">
          <div class="cat-nombre">${esc(c.nombre)}</div>
          <div class="cat-desc">${esc(c.descripcion ?? '')}</div>
          <div class="cat-foot">
            <span class="cat-count">${total} experiencia${total !== 1 ? 's' : ''}</span>
            <span class="cat-btn">Ver experiencias →</span>
          </div>
        </div>
      `;
      grid.appendChild(a);
    });

    document.getElementById('spinner').style.display = 'none';
    grid.style.display = 'grid';

  } catch (e) {
    console.error('[cargarCategorias]', e);
    document.getElementById('spinner').innerHTML = '<p style="color:#E8603C">Error cargando categorías</p>';
  }
}

cargarCategorias();
