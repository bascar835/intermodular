function login() {
    let correo = document.getElementById("correo").value.trim();
    let contrasena = document.getElementById("contrasena").value.trim();
    let mensajeError = document.getElementById("mensajeError");

    if (correo === "" || contrasena === "") {
        mensajeError.innerText = "⚠️ Por favor, completa todos los campos.";
        return;
    }

    let usuario = {
        correo: correo,
        puntos: 1250,
        nivel: "Oro",
        descuento: "15%"
    };

    localStorage.setItem("usuario", JSON.stringify(usuario));

    window.location.href = "index.html";
}
