/**
 * show.js — Página de detalle de experiencia
 *
 * Flujo:
 *  1. Lee el ?id=N de la URL
 *  2. GET /api/experiencias/{id}  → recibe los datos de la BD
 *  3. Rellena el HTML con los datos reales
 *  4. Añade contenido enriquecido (recomendaciones, incluidos) según categoría
 *  5. Gestiona el selector de personas y calcula el total
 *  6. El botón "Añadir al carrito" emite un evento personalizado
 *     para que tu compañero pueda engancharlo fácilmente
 */

// ── Leer el id de la URL ─────────────────────────────────────────────
// Ejemplo: /experiencias/show.html?id=1  →  id = 1
const params = new URLSearchParams(window.location.search);
const expId  = parseInt(params.get('id'));
console.log(expId);
// ── Estado ────────────────────────────────────────────────────────────
let experiencia   = null;   // objeto con los datos de la API
let numPersonas   = 1;

// ── Elementos del DOM ─────────────────────────────────────────────────
const spinner    = document.getElementById('spinner');
const errorBox   = document.getElementById('error-box');
const detalle    = document.getElementById('detalle');
const btnMenos   = document.getElementById('btn-menos');
const btnMas     = document.getElementById('btn-mas');
const numEl      = document.getElementById('num-personas');
const totalEl    = document.getElementById('compra-total');
const btnCarrito = document.getElementById('btn-carrito');

// ══════════════════════════════════════════════════════════════════════
//  INIT
// ══════════════════════════════════════════════════════════════════════
document.addEventListener('DOMContentLoaded', () => {
    if (!expId || isNaN(expId)) {
        mostrarError();
        return;
    }
    cargarDetalle(expId);
});

// ══════════════════════════════════════════════════════════════════════
//  GET /api/experiencias/{id}
// ══════════════════════════════════════════════════════════════════════
async function cargarDetalle(id) {
    try {
        const res = await fetch(`/api/experiencias/${id}`);
        const datos = await res.json();
		console.log(datos);
        if (!res.ok || !datos.ok) {
            mostrarError();
            return;
        }

        experiencia = datos.data;
        renderDetalle(experiencia);

    } catch (e) {
        console.error('[cargarDetalle]', e);
        mostrarError();
    }
}

// ══════════════════════════════════════════════════════════════════════
//  RENDER — rellena toda la página con los datos de la API
// ══════════════════════════════════════════════════════════════════════
function renderDetalle(exp) {
    // ⚠️ SNAKE_CASE: los campos llegan como categoria_nombre, duracion_horas…
    const slug     = catSlug(exp.categoria_nombre);
    const precioFmt = formatPrecio(exp.precio);
    const durLabel  = `${exp.duracion_horas} hora${exp.duracion_horas !== 1 ? 's' : ''}`;

    // ── Hero ──────────────────────────────────────────────────────────
    document.getElementById('det-hero').classList.add(slug);
    document.getElementById('bc-categoria').textContent = exp.categoria_nombre;
    document.getElementById('bc-titulo').textContent    = exp.titulo;
    document.getElementById('det-badge').textContent    = exp.categoria_nombre;
    document.getElementById('det-titulo').textContent   = exp.titulo;
    document.getElementById('det-ubicacion').innerHTML  = `📍 ${esc(exp.ubicacion)}`;
    document.getElementById('det-duracion').innerHTML   = `⏱ ${durLabel}`;

    // ── Descripción enriquecida ───────────────────────────────────────
    // Si la BD tiene descripción, la usamos; si no, ponemos un texto
    // genérico basado en el título y la ubicación
    const descBD = exp.descripcion && exp.descripcion.trim() !== ''
        ? exp.descripcion
        : null;
    document.getElementById('det-desc').textContent =
        descBD ?? textoDescripcion(exp.titulo, exp.ubicacion, exp.categoria_nombre);

    // ── Detalles rápidos ─────────────────────────────────────────────
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

    // ── Recomendaciones por categoría ─────────────────────────────────
    document.getElementById('det-recomendaciones').innerHTML =
        recomendaciones(exp.titulo, exp.ubicacion, exp.categoria_id);

    // ── Qué incluye por categoría ─────────────────────────────────────
    const items = queIncluye(exp.categoria_id);
    document.getElementById('det-incluye').innerHTML =
        items.map(item => `<li>${esc(item)}</li>`).join('');

    // ── Sidebar ───────────────────────────────────────────────────────
    document.getElementById('compra-precio').textContent    = precioFmt;
    document.getElementById('compra-ubicacion').textContent = exp.ubicacion;
    document.getElementById('compra-duracion').textContent  = durLabel;
    document.getElementById('compra-categoria').textContent = exp.categoria_nombre;
    actualizarTotal();

    // ── Título de la pestaña ──────────────────────────────────────────
    document.title = `${exp.titulo} · Waynabox`;

    // ── Mostrar la página ─────────────────────────────────────────────
    spinner.style.display = 'none';
    detalle.style.display = 'block';

    // ── Listeners del selector de personas ───────────────────────────
    initPersonas();

    // ── Listener del botón carrito ────────────────────────────────────
    initCarrito();
}

// ══════════════════════════════════════════════════════════════════════
//  SELECTOR DE PERSONAS
// ══════════════════════════════════════════════════════════════════════
function initPersonas() {
    btnMenos.addEventListener('click', () => {
        if (numPersonas > 1) {
            numPersonas--;
            numEl.textContent = numPersonas;
            actualizarTotal();
        }
    });
    btnMas.addEventListener('click', () => {
        if (numPersonas < 12) {
            numPersonas++;
            numEl.textContent = numPersonas;
            actualizarTotal();
        }
    });
}

function actualizarTotal() {
    const total = parseFloat(experiencia.precio) * numPersonas;
    totalEl.textContent = formatPrecio(total);
}

// ══════════════════════════════════════════════════════════════════════
//  BOTÓN CARRITO
//  Emite un CustomEvent con todos los datos para que tu compañero
//  pueda escucharlo desde su código con:
//  document.addEventListener('añadirAlCarrito', e => { ... })
// ══════════════════════════════════════════════════════════════════════
function initCarrito() {
    btnCarrito.addEventListener('click', () => {
        const payload = {
            id:           experiencia.id,
            titulo:       experiencia.titulo,
            precio:       parseFloat(experiencia.precio),
            ubicacion:    experiencia.ubicacion,
            categoria:    experiencia.categoria_nombre,
            numPersonas:  numPersonas,
            totalPrecio:  parseFloat(experiencia.precio) * numPersonas
        };

        // Evento personalizado — tu compañero lo escucha con addEventListener
        document.dispatchEvent(new CustomEvent('añadirAlCarrito', { detail: payload }));

        // Feedback visual temporal
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

// ══════════════════════════════════════════════════════════════════════
//  CONTENIDO ENRIQUECIDO POR CATEGORÍA
// ══════════════════════════════════════════════════════════════════════

/**
 * Descripción enriquecida cuando la BD solo tiene texto corto.
 * Usa el título y la ubicación para personalizarla.
 */
function textoDescripcion(titulo, ubicacion, categoria) {
    const textos = {
        'Aventura': `${titulo} en ${ubicacion} es una experiencia diseñada para quienes buscan romper con la rutina y conectar con la naturaleza de una forma auténtica. Guías expertos te acompañarán en todo momento garantizando tu seguridad mientras vives momentos únicos que recordarás siempre. No se requiere experiencia previa — solo ganas de disfrutar.`,
        'Cultura':  `${titulo} en ${ubicacion} es una inmersión en la historia, el arte y las tradiciones más ricas de España. Acompañado por expertos locales apasionados, descubrirás rincones y secretos que los turistas habituales nunca llegan a conocer. Una experiencia que abre la mente y alimenta el alma.`,
        'Relax':    `${titulo} en ${ubicacion} es tu escape perfecto del ritmo frenético del día a día. Un espacio diseñado para que desconectes por completo, recuperes energía y vuelvas renovado. Instalaciones de primer nivel y profesionales dedicados a que cada minuto sea de bienestar absoluto.`
    };
    return textos[categoria] ?? `Descubre ${titulo} en ${ubicacion}, una experiencia única que no te dejará indiferente.`;
}

/**
 * Recomendaciones personalizadas por categoría_id
 */
function recomendaciones(titulo, ubicacion, categoriaId) {
    const textos = {
        1: `
          <p><strong>Prepárate bien:</strong> Te recomendamos llevar ropa cómoda que pueda mojarse o ensuciarse, calzado cerrado con buena sujeción y protector solar. Nada de ropa de marca nueva — ¡aquí vamos a disfrutar de verdad!</p>
          <p><strong>Antes de la actividad:</strong> Evita comer en abundancia las 2 horas previas. Un desayuno ligero y mucha hidratación es la clave para rendir al máximo y disfrutar sin molestias.</p>
          <p><strong>Lleva lo justo:</strong> Deja el móvil en la taquilla o lleva una funda impermeable. El equipo y las medidas de seguridad las proporciona la empresa — tú solo preocúpate de vivir el momento.</p>
        `,
        2: `
          <p><strong>Sumérgete en el contexto:</strong> Si puedes, lee algo sobre la historia de ${ubicacion} antes de llegar — la experiencia ganará muchísimo. No hace falta ser un experto; la curiosidad es suficiente.</p>
          <p><strong>Viste cómodo:</strong> Caminarás y estarás de pie en varios momentos, así que opta por calzado cómodo. Si la actividad es en interiores, ten en cuenta que algunos espacios pueden estar frescos.</p>
          <p><strong>Haz preguntas:</strong> Nuestros guías locales adoran compartir anécdotas y curiosidades que no encontrarás en ninguna guía. No tengas vergüenza — las mejores historias salen de las mejores preguntas.</p>
        `,
        3: `
          <p><strong>Llega sin prisas:</strong> Parte del disfrute empieza en el camino. Llega unos minutos antes para orientarte, guardar tus cosas y entrar en modo relajación desde el principio.</p>
          <p><strong>Desconecta el móvil:</strong> Estas horas son tuyas. Silencia las notificaciones y permítete estar presente al 100% — tu mente y tu cuerpo te lo agradecerán.</p>
          <p><strong>Hidratación:</strong> Bebe agua antes y después, especialmente si incluye termas o actividad física suave. Muchos de nuestros centros ofrecen infusiones y agua de cortesía incluida.</p>
        `
    };
    return textos[categoriaId] ?? '<p>Consulta con nuestro equipo para sacar el máximo partido a esta experiencia.</p>';
}

/**
 * Lista de elementos incluidos según categoría_id
 */
function queIncluye(categoriaId) {
    const listas = {
        1: [
            'Equipo de seguridad completo (casco, arnés, neopreno si aplica)',
            'Monitor o guía titulado durante toda la actividad',
            'Sesión de instrucción y briefing de seguridad previo',
            'Seguro de responsabilidad civil',
            'Fotografías del grupo al finalizar la actividad',
            'Uso de instalaciones (vestuarios, taquillas)',
        ],
        2: [
            'Guía experto en historia y cultura local',
            'Entrada a los espacios o instalaciones incluidas',
            'Material informativo digital (dossier, mapas)',
            'Traducción disponible bajo petición',
            'Seguro de responsabilidad civil',
            'Bebida de bienvenida al inicio de la experiencia',
        ],
        3: [
            'Acceso completo a las instalaciones durante la duración contratada',
            'Toalla y productos de higiene de cortesía',
            'Bata y zapatillas de uso en las instalaciones',
            'Bebida de bienvenida (agua, infusión o zumo)',
            'Taquilla de seguridad para tus pertenencias',
            'Asesoramiento personalizado del equipo de bienestar',
        ]
    };
    return listas[categoriaId] ?? ['Monitor o guía profesional', 'Seguro de responsabilidad civil', 'Material necesario para la actividad'];
}

// ══════════════════════════════════════════════════════════════════════
//  HELPERS
// ══════════════════════════════════════════════════════════════════════
function mostrarError() {
    spinner.style.display = 'none';
    errorBox.style.display = 'flex';
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
    return new Intl.NumberFormat('es-ES', {
        style: 'currency', currency: 'EUR', maximumFractionDigits: 0
    }).format(p);
}

function esc(s) {
    return String(s ?? '')
        .replace(/&/g, '&amp;').replace(/</g, '&lt;')
        .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}
