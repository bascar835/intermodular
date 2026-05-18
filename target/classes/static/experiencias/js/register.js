async function registrar() {
        const nombre    = document.getElementById("nombre").value.trim();
        const correo    = document.getElementById("correo").value.trim();
        const contrasena = document.getElementById("contrasena").value.trim();
        const mensajeError = document.getElementById("mensajeError");

        mensajeError.innerText = "";

        if (!nombre || !correo || !contrasena) {
            mensajeError.innerText = "⚠️ Completa todos los campos.";
            return;
        }

        if (contrasena.length < 6) {
            mensajeError.innerText = "⚠️ La contraseña debe tener al menos 6 caracteres.";
            return;
        }

        try {
            const res = await fetch("/api/auth/register", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ name: nombre, email: correo, password: contrasena })
            });

            if (!res.ok) {
                const err = await res.json().catch(() => ({}));
                mensajeError.innerText = "⚠️ " + (err.message || "Error al registrarse.");
                return;
            }

            const user = await res.json();
            localStorage.setItem("userName", user.name || user.nombre || "");
            window.location.href = "/experiencias/index.html";

        } catch (e) {
            mensajeError.innerText = "⚠️ Error de conexión. Inténtalo de nuevo.";
        }
    }

    document.addEventListener("keydown", (e) => {
        if (e.key === "Enter") registrar();
    });