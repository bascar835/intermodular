// reservas.js — carga las reservas reales del usuario desde /api/reservas/mis-reservas

document.addEventListener("DOMContentLoaded", async function () {
    await mostrarReservas();
});

async function mostrarReservas() {
    const tbody = document.querySelector("tbody");
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;color:var(--color-text-secondary)">Cargando...</td></tr>`;

    try {
        const res = await fetch("/api/reservas/mis-reservas");

        // Si no hay sesión, redirigir al login
        if (res.status === 401) {
            window.location.href = "/experiencias/login.html";
            return;
        }

        if (!res.ok) throw new Error("Error al cargar reservas");

        const reservas = await res.json();

        if (reservas.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="5" style="text-align:center;padding:30px;color:var(--color-text-secondary)">
                        No tienes reservas todavía.
                    </td>
                </tr>`;
            return;
        }

        tbody.innerHTML = "";

        reservas.forEach(r => {
            const fecha = r.fecha_reserva
                ? new Date(r.fecha_reserva).toLocaleDateString("es-ES")
                : "-";

            const estadoClass = {
                confirmada: "premium",
                pendiente:  "pendiente",
                cancelada:  "cancelada"
            }[r.estado] || "";

            tbody.innerHTML += `
                <tr>
                    <td>#RES-${r.id}</td>
                    <td>Experiencia #${r.experiencia_id}</td>
                    <td>${fecha}</td>
                    <td>${r.numero_personas}</td>
                    <td><span class="status-badge ${estadoClass}">${capitalizar(r.estado)}</span></td>
                </tr>`;
        });

    } catch (e) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align:center;color:var(--color-text-danger)">
                    ⚠️ Error al cargar las reservas. Inténtalo de nuevo.
                </td>
            </tr>`;
        console.error(e);
    }
}

function capitalizar(str) {
    if (!str) return "";
    return str.charAt(0).toUpperCase() + str.slice(1);
}
