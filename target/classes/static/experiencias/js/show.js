/**
 * show.js — Página de detalle de experiencia
 */

const params = new URLSearchParams(window.location.search);
const expId  = parseInt(params.get('id'));

let experiencia = null;
let numPersonas = 1;

const spinner    = document.getElementById('spinner');
const errorBox   = document.getElementById('error-box');
const detalle    = document.getElementById('detalle');
const btnMenos   = document.getElementById('btn-menos');
const btnMas     = document.getElementById('btn-mas');
const numEl      = document.getElementById('num-personas');
const totalEl    = document.getElementById('compra-total');
const btnCarrito = document.getElementById('btn-carrito');

document.addEventListener('DOMContentLoaded', () => {
    if (!expId || isNaN(expId)) { mostrarError('ID inválido'); return; }
    cargarDetalle(expId);
});

// ── GET /api/experiencias/{id} ────────────────────────────────────────
async function cargarDetalle(id) {
    try {
        const res   = await fetch(`/api/experiencias/${id}`);
        const datos = await res.json();

        console.log('[show] HTTP', res.status, datos);

        if (!res.ok || !datos.ok) {
            const msg = datos.message || datos.error || `HTTP ${res.status}`;
            console.error('[show] Error del servidor:', msg, '| causa:', datos.causa);
            mostrarError(msg);
            return;
        }

        experiencia = datos.data;
        renderDetalle(experiencia);

    } catch (e) {
        console.error('[show] excepción:', e);
        mostrarError(e.message);
    }
}

// ── RENDER ────────────────────────────────────────────────────────────
function renderDetalle(exp) {
    const slug      = catSlug(exp.categoria_nombre);
    const precioFmt = formatPrecio(exp.precio);
    const durLabel  = `${exp.duracion_horas} hora${exp.duracion_horas !== 1 ? 's' : ''}`;

    if (slug) document.getElementById('det-hero').classList.add(slug);
    const heroImgUrl = exp.imagenes && exp.imagenes.length > 0 ? exp.imagenes[0].url : null;
    if (heroImgUrl) {
        const hero = document.getElementById('det-hero');
        hero.style.backgroundImage = `url('${heroImgUrl}')`;
        hero.style.backgroundSize = 'cover';
        hero.style.backgroundPosition = 'center';
    }

    if (exp.imagenes && exp.imagenes.length > 0) {
        document.getElementById('galeria-section').style.display = 'block';
        const principal = document.getElementById('galeria-img-principal');
        const thumbsEl  = document.getElementById('galeria-thumbs');

        function activarImagen(url, thumbActiva) {
            principal.style.opacity = '0';
            setTimeout(() => { principal.src = url; principal.style.opacity = '1'; }, 150);
            document.querySelectorAll('.galeria-thumb').forEach(t => t.classList.remove('activa'));
            if (thumbActiva) thumbActiva.classList.add('activa');
        }

        principal.src = exp.imagenes[0].url;
        exp.imagenes.forEach((img, i) => {
            const th = document.createElement('img');
            th.src       = img.url;
            th.alt       = `Imagen ${i + 1}`;
            th.className = 'galeria-thumb' + (i === 0 ? ' activa' : '');
            th.addEventListener('click', () => activarImagen(img.url, th));
            thumbsEl.appendChild(th);
        });
    }

    document.getElementById('bc-categoria').textContent = exp.categoria_nombre;
    document.getElementById('bc-titulo').textContent    = exp.titulo;
    document.getElementById('det-badge').textContent    = exp.categoria_nombre;
    document.getElementById('det-titulo').textContent   = exp.titulo;
    document.getElementById('det-ubicacion').innerHTML  = `📍 ${esc(exp.ubicacion)}`;
    document.getElementById('det-duracion').innerHTML   = `⏱ ${durLabel}`;

    const descBD = exp.descripcion && exp.descripcion.trim() !== '' ? exp.descripcion : null;
    document.getElementById('det-desc').textContent =
        descBD ?? textoDescripcion(exp.titulo, exp.ubicacion, exp.categoria_nombre);

    document.getElementById('det-detalles-grid').innerHTML = [
        { icon: '📍', label: 'Ubicación',  valor: exp.ubicacion },
        { icon: '⏱',  label: 'Duración',   valor: durLabel },
        { icon: '🏷',  label: 'Categoría',  valor: exp.categoria_nombre },
        { icon: '💶',  label: 'Precio',     valor: `${precioFmt} / persona` },
        { icon: '👥',  label: 'Grupo',      valor: 'Máx. 12 personas' },
        { icon: '🗓',  label: 'Disponible', valor: 'Todo el año' },
    ].map(d => `
        <div class="detalle-item">
          <span class="detalle-icon">${d.icon}</span>
          <span class="detalle-label">${d.label}</span>
          <span class="detalle-valor">${esc(d.valor)}</span>
        </div>
    `).join('');

    document.getElementById('det-recomendaciones').innerHTML =
        recomendaciones(exp.titulo, exp.ubicacion, exp.categoria_id);

    const items = queIncluye(exp.categoria_id);
    document.getElementById('det-incluye').innerHTML =
        items.map(item => `<li>${esc(item)}</li>`).join('');

    document.getElementById('compra-precio').textContent    = precioFmt;
    document.getElementById('compra-ubicacion').textContent = exp.ubicacion;
    document.getElementById('compra-duracion').textContent  = durLabel;
    document.getElementById('compra-categoria').textContent = exp.categoria_nombre;
    actualizarTotal();

    document.title = `${exp.titulo} · Xperiabox`;
    spinner.style.display = 'none';
    detalle.style.display = 'block';

    initPersonas();
    initCarrito();
}

function initPersonas() {
    btnMenos.addEventListener('click', () => {
        if (numPersonas > 1) { numPersonas--; numEl.textContent = numPersonas; actualizarTotal(); }
    });
    btnMas.addEventListener('click', () => {
        if (numPersonas < 12) { numPersonas++; numEl.textContent = numPersonas; actualizarTotal(); }
    });
}

function actualizarTotal() {
    totalEl.textContent = formatPrecio(parseFloat(experiencia.precio) * numPersonas);
}

function initCarrito() {
    btnCarrito.addEventListener('click', () => {
        addToCart(experiencia.id, experiencia.titulo, parseFloat(experiencia.precio), numPersonas);
        btnCarrito.textContent = '✓ Añadido al carrito';
        btnCarrito.classList.add('añadido');
        setTimeout(() => {
            btnCarrito.innerHTML = `
              <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="9" cy="21" r="1"/><circle cx="20" cy="21" r="1"/>
                <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"/>
              </svg>
              Añadir al carrito`;
            btnCarrito.classList.remove('añadido');
        }, 2000);
    });
}

function textoDescripcion(titulo, ubicacion, categoria) {
    const textos = {
        'Aventura': `${titulo} en ${ubicacion} es una experiencia diseñada para quienes buscan romper con la rutina y conectar con la naturaleza de una forma auténtica.`,
        'Cultura':  `${titulo} en ${ubicacion} es una inmersión en la historia, el arte y las tradiciones más ricas de España.`,
        'Relax':    `${titulo} en ${ubicacion} es tu escape perfecto del ritmo frenético del día a día.`
    };
    return textos[categoria] ?? `Descubre ${titulo} en ${ubicacion}, una experiencia única.`;
}

function recomendaciones(titulo, ubicacion, categoriaId) {
    const textos = {
        1: `<p><strong>Prepárate bien:</strong> Lleva ropa cómoda, calzado cerrado y protector solar.</p>
            <p><strong>Antes de la actividad:</strong> Evita comer en abundancia las 2 horas previas.</p>`,
        2: `<p><strong>Sumérgete en el contexto:</strong> Lee algo sobre la historia de ${ubicacion} antes de llegar.</p>
            <p><strong>Viste cómodo:</strong> Opta por calzado cómodo, caminarás bastante.</p>`,
        3: `<p><strong>Llega sin prisas:</strong> Llega unos minutos antes para entrar en modo relajación.</p>
            <p><strong>Desconecta el móvil:</strong> Silencia las notificaciones y estate presente al 100%.</p>`
    };
    return textos[categoriaId] ?? '<p>Consulta con nuestro equipo para sacar el máximo partido.</p>';
}

function queIncluye(categoriaId) {
    const listas = {
        1: ['Equipo de seguridad completo', 'Monitor o guía titulado', 'Sesión de instrucción previa', 'Seguro de responsabilidad civil', 'Fotografías del grupo'],
        2: ['Guía experto en historia y cultura local', 'Entrada a los espacios incluidos', 'Material informativo digital', 'Seguro de responsabilidad civil'],
        3: ['Acceso completo a las instalaciones', 'Toalla y productos de higiene', 'Bata y zapatillas', 'Bebida de bienvenida']
    };
    return listas[categoriaId] ?? ['Monitor o guía profesional', 'Seguro de responsabilidad civil', 'Material necesario'];
}

function mostrarError(detalle) {
    spinner.style.display = 'none';
    errorBox.style.display = 'flex';
    // Mostrar detalle del error debajo del mensaje principal si existe
    const sub = errorBox.querySelector('.error-sub');
    if (sub && detalle) sub.textContent = detalle;
}

function catSlug(nombre) {
    if (!nombre) return '';
    const n = nombre.toLowerCase().normalize('NFD').replace(/\p{Diacritic}/gu, '');
    if (n.includes('aventura')) return 'av';
    if (n.includes('cultura'))  return 'cu';
    if (n.includes('relax'))    return 're';
    return 'gen'; // clase genérica para categorías no mapeadas
}

function formatPrecio(p) {
    return new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR', maximumFractionDigits: 0 }).format(p);
}

function esc(s) {
    return String(s ?? '').replace(/&/g,'&amp;').replace(/</g,'&lt;').replace(/>/g,'&gt;').replace(/"/g,'&quot;');
}
