// login.js — conectado al backend real
// Llama a POST /api/auth/login y redirige según rol

async function login() {
    const correo    = document.getElementById("correo").value.trim();
    const contrasena = document.getElementById("contrasena").value.trim();
    const mensajeError = document.getElementById("mensajeError");

    mensajeError.innerText = "";

    if (correo === "" || contrasena === "") {
        mensajeError.innerText = "⚠️ Por favor, completa todos los campos.";
        return;
    }

    try {
        const res = await fetch("/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: correo, password: contrasena })
        });

        if (!res.ok) {
            const err = await res.json().catch(() => ({}));
            mensajeError.innerText = "⚠️ " + (err.message || "Credenciales incorrectas.");
            return;
        }

        const user = await res.json();

        // Guardar nombre en localStorage solo para mostrarlo en la UI
        // La sesión real vive en el servidor (cookie de sesión HTTP)
        localStorage.setItem("userName", user.name || user.nombre || "");

        // Redirigir según rol
        if (user.role === "ROLE_ADMIN") {
            window.location.href = "/admin/categorias/index.html";
        } else {
            window.location.href = "/experiencias/index.html";
        }

    } catch (e) {
        mensajeError.innerText = "⚠️ Error de conexión. Inténtalo de nuevo.";
    }
}

// Permitir hacer login con Enter
document.addEventListener("DOMContentLoaded", () => {
    document.addEventListener("keydown", (e) => {
        if (e.key === "Enter") login();
    });
});
