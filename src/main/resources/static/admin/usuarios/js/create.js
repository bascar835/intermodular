async function guardar(e) {
    e.preventDefault();

    const response = await authFetch("/api/admin/users", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            name: document.getElementById("nombre").value,
            email: document.getElementById("email").value,
            password: document.getElementById("password").value,
            role: document.getElementById("rol").value
        })
    });

    if (response && response.ok) {
        location.href = "index.html";
    } else {
        alert("Error al crear el usuario. Revisa los datos.");
    }
}
