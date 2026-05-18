/**
 * form-validation.js
 * Sistema de validación centralizado para todos los formularios del admin.
 *
 * Uso:
 *   const ok = validarFormulario([
 *     { id: "nombre",   label: "Nombre" },
 *     { id: "precio",   label: "Precio", tipo: "numero" },
 *     { id: "email",    label: "Email",  tipo: "email" },
 *     { id: "cat_id",   label: "Categoría", tipo: "select" },
 *   ]);
 *   if (!ok) return;
 */

/**
 * Valida una lista de campos y muestra errores inline bajo cada uno.
 * Devuelve true si todo está correcto, false si hay algún error.
 *
 * @param {Array<{id, label, tipo?, requerido?}>} campos
 *   - id:        id del elemento HTML
 *   - label:     nombre legible del campo (para el mensaje)
 *   - tipo:      "texto" (default) | "numero" | "email" | "select" | "datetime"
 *   - requerido: true por defecto
 * @returns {boolean}
 */
function validarFormulario(campos) {
    let valido = true;

    campos.forEach(({ id, label, tipo = "texto", requerido = true }) => {
        const el = document.getElementById(id);
        if (!el) return;

        // Limpiar error previo
        limpiarError(el);

        if (!requerido) return;

        const valor = el.value ? el.value.trim() : "";

        if (tipo === "select") {
            if (!valor || valor === "") {
                mostrarErrorCampo(el, `El campo "${label}" es obligatorio.`);
                valido = false;
            }
            return;
        }

        if (!valor) {
            mostrarErrorCampo(el, `El campo "${label}" es obligatorio.`);
            valido = false;
            return;
        }

        if (tipo === "numero") {
            const num = parseFloat(valor);
            if (isNaN(num) || num <= 0) {
                mostrarErrorCampo(el, `"${label}" debe ser un número mayor que 0.`);
                valido = false;
            }
            return;
        }

        if (tipo === "entero") {
            const num = parseInt(valor);
            if (isNaN(num) || num < 1) {
                mostrarErrorCampo(el, `"${label}" debe ser un número entero de al menos 1.`);
                valido = false;
            }
            return;
        }

        if (tipo === "email") {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(valor)) {
                mostrarErrorCampo(el, `"${label}" no tiene un formato de email válido.`);
                valido = false;
            }
            return;
        }

        if (tipo === "datetime") {
            if (!valor) {
                mostrarErrorCampo(el, `El campo "${label}" es obligatorio.`);
                valido = false;
            }
            return;
        }
    });

    return valido;
}

function mostrarErrorCampo(el, mensaje) {
    el.classList.add("input-error");

    // Eliminar mensaje previo si existe
    const prev = el.parentElement.querySelector(".field-error-msg");
    if (prev) prev.remove();

    const msg = document.createElement("span");
    msg.className = "field-error-msg";
    msg.textContent = "⚠ " + mensaje;
    el.insertAdjacentElement("afterend", msg);

    // Scroll al primer error
    el.scrollIntoView({ behavior: "smooth", block: "center" });
}

function limpiarError(el) {
    el.classList.remove("input-error");
    const prev = el.parentElement.querySelector(".field-error-msg");
    if (prev) prev.remove();
}

/** Limpia todos los errores del formulario */
function limpiarTodosLosErrores() {
    document.querySelectorAll(".input-error").forEach(el => el.classList.remove("input-error"));
    document.querySelectorAll(".field-error-msg").forEach(el => el.remove());
    const formErr = document.getElementById("form-error");
    if (formErr) formErr.style.display = "none";
}

/** Muestra un error general en el form-error div */
function mostrarErrorGeneral(msg) {
    let el = document.getElementById("form-error");
    if (!el) {
        el = document.createElement("div");
        el.id = "form-error";
        document.querySelector(".admin-form")?.appendChild(el);
    }
    el.style.cssText = "color:#c00;background:#fff0f0;border:1px solid #f99;border-radius:6px;padding:10px 14px;margin-bottom:12px;";
    el.textContent = msg;
    el.style.display = "block";
    el.scrollIntoView({ behavior: "smooth", block: "center" });
}

// Limpiar error inline al escribir en el campo
document.addEventListener("input", e => {
    if (e.target.matches("input, textarea, select")) limpiarError(e.target);
});
document.addEventListener("change", e => {
    if (e.target.matches("select")) limpiarError(e.target);
});
