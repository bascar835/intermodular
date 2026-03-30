document.addEventListener("DOMContentLoaded", function () {
    mostrarReservas();
});

function mostrarReservas() {
    const tbody = document.querySelector("tbody");
    if (!tbody) return;

    let reservas = JSON.parse(localStorage.getItem("reservas")) || [];

    if (reservas.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5">No tienes reservas todavía.</td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = "";

    reservas.forEach((reserva) => {
        tbody.innerHTML += `
            <tr>
                <td>${reserva.id}</td>
                <td>${reserva.experiencia}</td>
                <td>${reserva.fecha}</td>
                <td>${reserva.personas}</td>
                <td><span class="status-badge premium">${reserva.estado}</span></td>
            </tr>
        `;
    });
}
