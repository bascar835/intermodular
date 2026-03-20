async function login(e) {
    e.preventDefault();

    const response = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            email: email.value,
            password: password.value
        })
    });

    if (response.ok) {
        window.location.href = "/admin/categorias/index.html";
        return;
    }

    const error = document.getElementById("error");
    error.classList.remove("d-none");

    if (response.status === 401) {
        error.textContent = "Email o contraseña incorrectos.";
    } else {
        error.textContent = "Error inesperado. Inténtalo de nuevo.";
    }
}
