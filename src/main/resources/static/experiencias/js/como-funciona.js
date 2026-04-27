/**
 * como-funciona.js
 * Lógica para el acordeón de FAQs y animaciones de scroll
 */

// ── FAQs accordion ────────────────────────────────────────────────────
function toggleFaq(el) {
    const isOpen = el.classList.contains('open');
    // Cerrar todas
    document.querySelectorAll('.faq').forEach(f => f.classList.remove('open'));
    // Abrir la clicada (si no estaba ya abierta)
    if (!isOpen) el.classList.add('open');
}

// ── Animación de entrada al hacer scroll ─────────────────────────────
// Añade la clase 'visible' cuando el elemento entra en el viewport
const observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            entry.target.classList.add('visible');
            observer.unobserve(entry.target);
        }
    });
}, { threshold: 0.12 });

document.addEventListener('DOMContentLoaded', () => {
    // Observamos pasos, items de incluye y faqs
    document.querySelectorAll('.paso, .incluye-item, .faq, .cat-card')
        .forEach(el => observer.observe(el));
});
