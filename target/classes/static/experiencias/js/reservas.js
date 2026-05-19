// reservas.js — carga las reservas reales del usuario desde /api/reservas/mis-reservas

document.addEventListener("DOMContentLoaded", async function () {
    await mostrarReservas();
});

async function mostrarReservas() {
    const tbody = document.querySelector("tbody");
    if (!tbody) return;

    tbody.innerHTML = `<tr><td colspan="5" style="text-align:center;color:var(--color-text-secondary)">Cargando...</td></tr>`;

    try {
        // Cargar reservas y experiencias en paralelo
        const [resReservas, resExp] = await Promise.all([
            fetch("/api/reservas/mis-reservas"),
            fetch("/api/experiencias")
        ]);

        // Si no hay sesión, redirigir al login
        if (resReservas.status === 401) {
            window.location.href = "/experiencias/login.html";
            return;
        }

        if (!resReservas.ok) throw new Error("Error al cargar reservas");

        const reservas = await resReservas.json();

        // Construir mapa id → titulo de experiencias
        const expMap = {};
        if (resExp.ok) {
            const datosExp = await resExp.json();
            const lista = Array.isArray(datosExp) ? datosExp : (datosExp.data ?? []);
            lista.forEach(e => { expMap[e.id] = e.titulo; });
        }

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

            // Usar el título real si existe, si no mostrar el ID como fallback
            const nombreExp = expMap[r.experiencia_id] || `Experiencia #${r.experiencia_id}`;

            const estadoClass = {
                confirmada: "confirmada",
                pendiente:  "pendiente",
                cancelada:  "cancelada"
            }[r.estado] || "";

            tbody.innerHTML += `
                <tr>
                    <td>#RES-${r.id}</td>
                    <td>${nombreExp}</td>
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