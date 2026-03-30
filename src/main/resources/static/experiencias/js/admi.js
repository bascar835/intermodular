function nuevaExperiencia() {
    alert("Aquí podrás añadir una nueva experiencia próximamente.");
}

function editarExperiencia(id) {
    alert("Editar experiencia con ID: " + id);
}

function eliminarExperiencia(id) {
    let confirmar = confirm("¿Seguro que quieres eliminar esta experiencia?");
    
    if (confirmar) {
        alert("Experiencia eliminada: " + id);
    }
}
