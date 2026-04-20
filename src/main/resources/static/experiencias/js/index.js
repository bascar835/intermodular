/**
 * index.js — Experiencias
 *
 * IMPORTANTE — SNAKE_CASE:
 * El application.properties tiene SNAKE_CASE activo.
 * Eso significa que la API devuelve los campos así:
 *   duracion_horas   (NO duracionHoras)
 *   categoria_id     (NO categoriaId)
 *   categoria_nombre (NO categoriaNombre)
 *
 * Y el filtrar manda: { "categoria_id": 1 }  (NO "categoriaId")
 *
 * Flujo:
 *  Carga → GET /api/experiencias → renderCards(lista, '')
 *  Filtro → POST /api/experiencias/filtrar { "categoria_id": N }
 *  Búsqueda → POST /api/experiencias/buscar { "q": "texto" }
 */

// ── Elementos del DOM ─────────────────────────────────────────────────
const grid     = document.getElementById('grid');
const spinner  = document.getElementById('spinner');
const vacio    = document.getElementById('vacio');
const contador = document.getElementById('contador');
const buscador = document.getElementById('buscador');
const btnClear = document.getElementById('btn-clear');
const btnReset = document.getElementById('btn-reset');

// ── Estado ────────────────────────────────────────────────────────────
let cache     = [];   // todas las experiencias cargadas al inicio
let catActiva = 0;    // 0 = Todas
let debounce;

// ── Iconos por categoria_id ───────────────────────────────────────────
const ICONOS = { 1: '🏔', 2: '🎭', 3: '🧘' };

// ══════════════════════════════════════════════════════════════════════
//  INIT
// ══════════════════════════════════════════════════════════════════════
document.addEventListener('DOMContentLoaded', () => {
    cargarTodas();
    initFiltros();
    initBuscador();
    btnReset.addEventListener('click', resetTodo);
});

// ══════════════════════════════════════════════════════════════════════
//  GET /api/experiencias
// ══════════════════════════════════════════════════════════════════════
async function cargarTodas() {
    mostrarSpinner(true);
    try {
        const res = await fetch('/api/experiencias');

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            throw new Error(err.error || `HTTP ${res.status}`);
        }

        const datos = await res.json();
        cache = datos.data ?? [];
        renderCards(cache, '');

    } catch (e) {
        console.error('[cargarTodas]', e);
        mostrarError(`No se pudieron cargar las experiencias: ${e.message}`);
    } finally {
        mostrarSpinner(false);
    }
}

// ══════════════════════════════════════════════════════════════════════
//  POST /api/experiencias/filtrar  —  body: { "categoria_id": N }
//  ⚠️  snake_case: "categoria_id", NO "categoriaId"
// ══════════════════════════════════════════════════════════════════════
async function filtrarCategoria(catId) {
    catActiva = catId;
    const q = buscador.value.trim();

    if (catId === 0) {
        // Sin filtro de categoría → mostramos caché (o búsqueda activa)
        const lista = q.length >= 2 ? filtrarLocal(cache, q) : cache;
        renderCards(lista, q);
        return;
    }

    mostrarSpinner(true);
    try {
        const res = await fetch('/api/experiencias/filtrar', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            // ⚠️ SNAKE_CASE: enviamos "categoria_id"
            body: JSON.stringify({ categoria_id: catId })
        });

        if (!res.ok) throw new Error(`HTTP ${res.status}`);
        const datos = await res.json();

        let lista = datos.data ?? [];
        // Si además hay texto en el buscador, filtramos localmente
        if (q.length >= 2) lista = filtrarLocal(lista, q);

        renderCards(lista, q);

    } catch (e) {
        console.error('[filtrarCategoria]', e);
        mostrarError('Error al filtrar. Inténtalo de nuevo.');
    } finally {
        mostrarSpinner(false);
    }
}

// ══════════════════════════════════════════════════════════════════════
//  POST /api/experiencias/buscar  —  body: { "q": "texto" }
// ══════════════════════════════════════════════════════════════════════
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

        let lista = datos.data ?? [];
        // Si hay categoría activa, cruzamos
        // ⚠️ SNAKE_CASE: el campo del JSON es "categoria_id"
        if (catActiva !== 0) {
            lista = lista.filter(e => e.categoria_id === catActiva);
        }

        renderCards(lista, q);

    } catch (e) {
        console.error('[buscarTexto]', e);
        mostrarError('Error en la búsqueda.');
    } finally {
        mostrarSpinner(false);
    }
}

// ══════════════════════════════════════════════════════════════════════
//  RENDER — genera las cards en el DOM
// ══════════════════════════════════════════════════════════════════════
function renderCards(lista, q) {
    grid.innerHTML = '';
    vacio.style.display = 'none';
    actualizarContador(lista.length, q);

    if (!lista.length) {
        vacio.style.display = 'block';
        return;
    }

    lista.forEach((exp, i) => {
        // ⚠️ SNAKE_CASE: leemos categoria_nombre, duracion_horas, categoria_id
        const s   = catSlug(exp.categoria_nombre);
        const fmt = formatPrecio(exp.precio);

        const card = document.createElement('div');
        card.className = 'card';
        card.style.animationDelay = `${i * 0.055}s`;

        card.innerHTML = `
          <div class="card-img ${s}">
            <span class="img-icon">${ICONOS[exp.categoria_id] ?? '🌍'}</span>
            <div class="img-precio">${fmt} <sub>/ persona</sub></div>
          </div>
          <div class="card-body">
            <span class="card-badge ${s}">${esc(exp.categoria_nombre)}</span>
            <div class="card-title">${resaltar(exp.titulo, q)}</div>
            <div class="card-desc">${resaltar(exp.descripcion ?? '', q)}</div>
            <div class="card-meta">
              <span class="meta-i">
                <span class="meta-icon">📍</span>
                ${resaltar(exp.ubicacion, q)}
              </span>
              <span class="meta-i">
                <span class="meta-icon">⏱</span>
                ${exp.duracion_horas}h
              </span>
            </div>
          </div>
          <div class="card-foot">
            <p class="card-dur">
              Duración <strong>${exp.duracion_horas} hora${exp.duracion_horas !== 1 ? 's' : ''}</strong>
            </p>
            <button class="card-btn" onclick="verDetalle(${exp.id})">
              Ver experiencia →
            </button>
          </div>
        `;

        grid.appendChild(card);
    });
}

// ══════════════════════════════════════════════════════════════════════
//  FILTROS — listeners
// ══════════════════════════════════════════════════════════════════════
function initFiltros() {
    document.querySelectorAll('.f-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.f-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            filtrarCategoria(parseInt(btn.dataset.cat));
        });
    });
}

// ══════════════════════════════════════════════════════════════════════
//  BUSCADOR — debounce 320ms
// ══════════════════════════════════════════════════════════════════════
function initBuscador() {
    buscador.addEventListener('input', () => {
        const q = buscador.value.trim();
        btnClear.classList.toggle('visible', q.length > 0);

        clearTimeout(debounce);
        debounce = setTimeout(async () => {
            if (q.length === 0) {
                // Buscador vaciado → volver al estado del filtro activo
                if (catActiva === 0) renderCards(cache, '');
                else await filtrarCategoria(catActiva);
            } else if (q.length >= 2) {
                await buscarTexto(q);
            }
        }, 320);
    });

    buscador.addEventListener('keydown', e => {
        if (e.key === 'Escape') resetTodo();
    });
    btnClear.addEventListener('click', resetTodo);
}

// ══════════════════════════════════════════════════════════════════════
//  HELPERS
// ══════════════════════════════════════════════════════════════════════

function resetTodo() {
    buscador.value = '';
    btnClear.classList.remove('visible');
    catActiva = 0;
    document.querySelectorAll('.f-btn').forEach((b, i) =>
        b.classList.toggle('active', i === 0)
    );
    renderCards(cache, '');
}

function mostrarSpinner(v) { spinner.style.display = v ? 'flex' : 'none'; }

function mostrarError(msg) {
    grid.innerHTML = `
      <p style="color:var(--coral);padding:24px;font-size:.9rem;grid-column:1/-1;">
        ⚠️ ${esc(msg)}
      </p>`;
}

function actualizarContador(total, q) {
    if (q && q.length >= 2) {
        contador.innerHTML = `<strong>${total}</strong> resultado${total !== 1 ? 's' : ''} para "<em>${esc(q)}</em>"`;
    } else {
        contador.innerHTML = `<strong>${total}</strong> experiencia${total !== 1 ? 's' : ''} disponible${total !== 1 ? 's' : ''}`;
    }
}

// Filtro local (combina categoría + búsqueda sin petición extra)
function filtrarLocal(lista, q) {
    const ql = q.toLowerCase();
    return lista.filter(e =>
        (e.titulo      ?? '').toLowerCase().includes(ql) ||
        (e.descripcion ?? '').toLowerCase().includes(ql) ||
        (e.ubicacion   ?? '').toLowerCase().includes(ql)
    );
}

// Resalta coincidencias con <mark>
function resaltar(texto, q) {
    if (!q || q.length < 2 || !texto) return esc(texto);
    const safe = esc(texto);
    const pat  = q.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    return safe.replace(new RegExp(pat, 'gi'), m => `<mark>${m}</mark>`);
}

// Previene XSS
function esc(s) {
    return String(s ?? '')
        .replace(/&/g, '&amp;').replace(/</g, '&lt;')
        .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

// ⚠️ SNAKE_CASE: el campo es categoria_nombre
function catSlug(nombre) {
    if (!nombre) return '';
    const n = nombre.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
    if (n.includes('aventura')) return 'av';
    if (n.includes('cultura'))  return 'cu';
    if (n.includes('relax'))    return 're';
    return '';
}

function formatPrecio(p) {
    return new Intl.NumberFormat('es-ES', {
        style: 'currency', currency: 'EUR', maximumFractionDigits: 0
    }).format(p);
}

// Navegación al detalle (próximo hito)
function verDetalle(id) {
    window.location.href = `/experiencias/show.html?id=${id}`;
}
