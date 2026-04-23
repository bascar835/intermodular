/**
 * index.js — Experiencias
 *
 * Los botones de filtro se generan dinámicamente desde GET /api/categorias,
 * así los IDs coinciden siempre con los de la base de datos.
 *
 * SNAKE_CASE activo en application.properties:
 *   La API devuelve: duracion_horas, categoria_id, categoria_nombre
 *   El filtrar manda: { "categoria_id": N }
 */

const grid       = document.getElementById('grid');
const spinner    = document.getElementById('spinner');
const vacio      = document.getElementById('vacio');
const contador   = document.getElementById('contador');
const buscador   = document.getElementById('buscador');
const btnClear   = document.getElementById('btn-clear');
const btnReset   = document.getElementById('btn-reset');
const filtersBar = document.getElementById('filters-bar');

let cache     = [];
let catActiva = 0;
let debounce;

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

// ── INIT ──────────────────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', async () => {
    await cargarCategorias();
    await cargarTodas();
    initBuscador();
    btnReset.addEventListener('click', resetTodo);

    // Si venimos de /categorias.html con ?cat=ID, activar ese filtro automáticamente
    const params = new URLSearchParams(window.location.search);
    const catParam = parseInt(params.get('cat'));
    if (catParam && !isNaN(catParam)) {
        const btnCat = filtersBar.querySelector(`[data-cat="${catParam}"]`);
        if (btnCat) {
            document.querySelectorAll('.f-btn').forEach(b => b.classList.remove('active'));
            btnCat.classList.add('active');
            catActiva = catParam;
            filtrarCategoria(catParam);
        }
    }
});

// ── GET /api/categorias → genera botones dinámicamente ────────────────
async function cargarCategorias() {
    try {
        const res = await fetch('/api/categorias');
        if (!res.ok) return;
        const categorias = await res.json(); // array directo

        // Listener para "Todas" (ya está en el HTML)
        filtersBar.querySelector('[data-cat="0"]').addEventListener('click', function () {
            onFiltroClick(this);
        });

        categorias.forEach(cat => {
            const btn = document.createElement('button');
            btn.className = 'f-btn';
            btn.dataset.cat = cat.id;
            btn.innerHTML = `${iconoCategoria(cat.nombre)} ${esc(cat.nombre)} <span class="f-count">–</span>`;
            btn.addEventListener('click', () => onFiltroClick(btn));
            filtersBar.appendChild(btn);
        });

    } catch (e) {
        console.error('[cargarCategorias]', e);
    }
}

function onFiltroClick(btn) {
    document.querySelectorAll('.f-btn').forEach(b => b.classList.remove('active'));
    btn.classList.add('active');
    filtrarCategoria(parseInt(btn.dataset.cat));
}

// ── GET /api/experiencias ─────────────────────────────────────────────
async function cargarTodas() {
    mostrarSpinner(true);
    try {
        const res = await fetch('/api/experiencias');
        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.error || err.message || `HTTP ${res.status}`);
        }
        const datos = await res.json();
        // Soporta tanto { ok, total, data: [...] } como array directo [...]
        cache = Array.isArray(datos) ? datos : (datos.data ?? []);
        actualizarContadoresFiltros(cache);
        renderCards(cache, '');
    } catch (e) {
        console.error('[cargarTodas]', e);
        mostrarError(`No se pudieron cargar las experiencias: ${e.message}`);
    } finally {
        mostrarSpinner(false);
    }
}

// Actualiza el número entre paréntesis de cada botón con el conteo real
function actualizarContadoresFiltros(lista) {
    const btnTodas = filtersBar.querySelector('[data-cat="0"] .f-count');
    if (btnTodas) btnTodas.textContent = lista.length;

    const porCategoria = {};
    lista.forEach(e => {
        porCategoria[e.categoria_id] = (porCategoria[e.categoria_id] || 0) + 1;
    });

    filtersBar.querySelectorAll('.f-btn:not([data-cat="0"])').forEach(btn => {
        const count = btn.querySelector('.f-count');
        if (count) count.textContent = porCategoria[parseInt(btn.dataset.cat)] ?? 0;
    });
}

// ── POST /api/experiencias/filtrar ────────────────────────────────────
async function filtrarCategoria(catId) {
    catActiva = catId;
    const q = buscador.value.trim();

    if (catId === 0) {
        const lista = q.length >= 2 ? filtrarLocal(cache, q) : cache;
        renderCards(lista, q);
        return;
    }

    mostrarSpinner(true);
    try {
        const res = await fetch('/api/experiencias/filtrar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ categoria_id: catId })
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const datos = await res.json();
        let lista = Array.isArray(datos) ? datos : (datos.data ?? []);
        if (q.length >= 2) lista = filtrarLocal(lista, q);
        renderCards(lista, q);
    } catch (e) {
        console.error('[filtrarCategoria]', e);
        mostrarError('Error al filtrar. Inténtalo de nuevo.');
    } finally {
        mostrarSpinner(false);
    }
}

// ── POST /api/experiencias/buscar ─────────────────────────────────────
async function buscarTexto(q) {
    mostrarSpinner(true);
    try {
        const res = await fetch('/api/experiencias/buscar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ q })
        });
        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const datos = await res.json();
        let lista = Array.isArray(datos) ? datos : (datos.data ?? []);
        if (catActiva !== 0) lista = lista.filter(e => e.categoria_id === catActiva);
        renderCards(lista, q);
    } catch (e) {
        console.error('[buscarTexto]', e);
        mostrarError('Error en la búsqueda.');
    } finally {
        mostrarSpinner(false);
    }
}

// ── RENDER ────────────────────────────────────────────────────────────
function renderCards(lista, q) {
    grid.innerHTML = '';
    vacio.style.display = 'none';
    actualizarContador(lista.length, q);

    if (!lista.length) {
        vacio.style.display = 'block';
        return;
    }

    lista.forEach((exp, i) => {
        const s   = catSlug(exp.categoria_nombre);
        const fmt = formatPrecio(exp.precio);

        const card = document.createElement('div');
        card.className = 'card';
        card.style.animationDelay = `${i * 0.055}s`;

        card.innerHTML = `
          <div class="card-img ${s}" ${exp.imagen ? `style="background-image:url('${exp.imagen}');background-size:cover;background-position:center;"` : ''}>
            ${!exp.imagen ? `<span class="img-icon">${iconoCategoria(exp.categoria_nombre)}</span>` : ''}
            <div class="img-precio">${fmt} <sub>/ persona</sub></div>
          </div>
          <div class="card-body">
            <span class="card-badge ${s}">${esc(exp.categoria_nombre)}</span>
            <div class="card-title">${resaltar(exp.titulo, q)}</div>
            <div class="card-desc">${resaltar(exp.descripcion ?? '', q)}</div>
            <div class="card-meta">
              <span class="meta-i"><span class="meta-icon">📍</span>${resaltar(exp.ubicacion, q)}</span>
              <span class="meta-i"><span class="meta-icon">⏱</span>${exp.duracion_horas}h</span>
            </div>
          </div>
          <div class="card-foot">
            <p class="card-dur">Duración <strong>${exp.duracion_horas} hora${exp.duracion_horas !== 1 ? 's' : ''}</strong></p>
            <button class="card-btn" onclick="verDetalle(${exp.id})">Ver experiencia →</button>
          </div>
        `;
        grid.appendChild(card);
    });
}

// ── BUSCADOR ──────────────────────────────────────────────────────────
function initBuscador() {
    buscador.addEventListener('input', () => {
        const q = buscador.value.trim();
        btnClear.classList.toggle('visible', q.length > 0);
        clearTimeout(debounce);
        debounce = setTimeout(async () => {
            if (q.length === 0) {
                if (catActiva === 0) renderCards(cache, '');
                else await filtrarCategoria(catActiva);
            } else if (q.length >= 2) {
                await buscarTexto(q);
            }
        }, 320);
    });
    buscador.addEventListener('keydown', e => { if (e.key === 'Escape') resetTodo(); });
    btnClear.addEventListener('click', resetTodo);
}

// ── HELPERS ───────────────────────────────────────────────────────────
function resetTodo() {
    buscador.value = '';
    btnClear.classList.remove('visible');
    catActiva = 0;
    document.querySelectorAll('.f-btn').forEach((b, i) => b.classList.toggle('active', i === 0));
    renderCards(cache, '');
}

function mostrarSpinner(v) { spinner.style.display = v ? 'flex' : 'none'; }

function mostrarError(msg) {
    grid.innerHTML = `<p style="color:var(--coral);padding:24px;font-size:.9rem;grid-column:1/-1;">⚠️ ${esc(msg)}</p>`;
}

function actualizarContador(total, q) {
    if (q && q.length >= 2) {
        contador.innerHTML = `<strong>${total}</strong> resultado${total !== 1 ? 's' : ''} para "<em>${esc(q)}</em>"`;
    } else {
        contador.innerHTML = `<strong>${total}</strong> experiencia${total !== 1 ? 's' : ''} disponible${total !== 1 ? 's' : ''}`;
    }
}

function filtrarLocal(lista, q) {
    const ql = q.toLowerCase();
    return lista.filter(e =>
        (e.titulo      ?? '').toLowerCase().includes(ql) ||
        (e.descripcion ?? '').toLowerCase().includes(ql) ||
        (e.ubicacion   ?? '').toLowerCase().includes(ql)
    );
}

function resaltar(texto, q) {
    if (!q || q.length < 2 || !texto) return esc(texto);
    const safe = esc(texto);
    const pat  = q.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    return safe.replace(new RegExp(pat, 'gi'), m => `<mark>${m}</mark>`);
}

function esc(s) {
    return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}

function catSlug(nombre) {
    if (!nombre) return '';
    const n = nombre.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
    if (n.includes('aventura')) return 'av';
    if (n.includes('cultura'))  return 'cu';
    if (n.includes('relax'))    return 're';
    return '';
}

function formatPrecio(p) {
    return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(p);
}

function verDetalle(id) {
    window.location.href = `/experiencias/show.html?id=${id}`;
}
