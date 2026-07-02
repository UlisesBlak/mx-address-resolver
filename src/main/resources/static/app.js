const API_BASE = "/api/direccion";

const elCp = document.getElementById("cp");
const elEstado = document.getElementById("estado");
const elMunicipio = document.getElementById("municipio");
const elLocalidad = document.getElementById("localidad");
const elColonia = document.getElementById("colonia");
const elCalleYNumero = document.getElementById("calleYNumero");
const elForm = document.getElementById("formDireccion");
const elMensajeResultado = document.getElementById("mensajeResultado");
const elBtnContinuar = document.getElementById("btnContinuar");

function poblarSelect(select, opciones, placeholder, campoClave, campoDescripcion) {
    select.innerHTML = `<option value="">${placeholder}</option>`;
    for (const opcion of opciones) {
        const option = document.createElement("option");
        option.value = opcion[campoClave];
        option.textContent = opcion[campoDescripcion];
        select.appendChild(option);
    }
    select.disabled = opciones.length === 0;
}

function limpiarError(campo) {
    document.getElementById(`error${campo}`).textContent = "";
}

function mostrarError(campo, mensaje) {
    document.getElementById(`error${campo}`).textContent = mensaje;
}

function limpiarTodosLosErrores() {
    for (const span of document.querySelectorAll(".error")) {
        span.textContent = "";
    }
}

async function cargarEstados() {
    const respuesta = await fetch(`${API_BASE}/estados`);
    const estados = await respuesta.json();
    poblarSelect(elEstado, estados, "Seleccione...", "clave", "nombreEstado");
}

async function cargarMunicipiosYLocalidades(claveEstado) {
    const [municipios, localidades] = await Promise.all([
        fetch(`${API_BASE}/municipios?estado=${claveEstado}`).then(r => r.json()),
        fetch(`${API_BASE}/localidades?estado=${claveEstado}`).then(r => r.json())
    ]);
    poblarSelect(elMunicipio, municipios, "Seleccione...", "clave", "descripcion");
    poblarSelect(elLocalidad, localidades, "Seleccione...", "clave", "descripcion");
}

elEstado.addEventListener("change", async () => {
    limpiarError("Estado");
    elColonia.innerHTML = `<option value="">Seleccione...</option>`;
    elColonia.disabled = true;

    if (!elEstado.value) {
        elMunicipio.innerHTML = `<option value="">Seleccione el estado...</option>`;
        elLocalidad.innerHTML = `<option value="">Seleccione el estado...</option>`;
        elMunicipio.disabled = true;
        elLocalidad.disabled = true;
        return;
    }

    await cargarMunicipiosYLocalidades(elEstado.value);
});

elCp.addEventListener("blur", async () => {
    limpiarError("Cp");
    const cp = elCp.value.trim();

    if (!cp) {
        return;
    }

    const respuesta = await fetch(`${API_BASE}/codigo-postal/${cp}`);

    if (!respuesta.ok) {
        const error = await respuesta.json();
        mostrarError("Cp", error.error);
        return;
    }

    const resolucion = await respuesta.json();

    elEstado.value = resolucion.estado.clave;
    await cargarMunicipiosYLocalidades(resolucion.estado.clave);

    poblarSelect(elColonia, resolucion.colonias, "Seleccione...", "clave", "descripcion");

    if (resolucion.municipioSeleccionado) {
        elMunicipio.value = resolucion.municipioSeleccionado;
    }
    if (resolucion.localidadSeleccionada) {
        elLocalidad.value = resolucion.localidadSeleccionada;
    }
    if (resolucion.colonias.length === 1) {
        elColonia.value = resolucion.colonias[0].clave;
    }
});

elForm.addEventListener("submit", async (evento) => {
    evento.preventDefault();
    limpiarTodosLosErrores();
    elMensajeResultado.textContent = "";
    elMensajeResultado.className = "mensaje";
    elBtnContinuar.disabled = true;

    const body = {
        cp: elCp.value.trim(),
        estadoClave: elEstado.value,
        municipioClave: elMunicipio.value,
        localidadClave: elLocalidad.value,
        coloniaClave: elColonia.value,
        calleYNumero: elCalleYNumero.value.trim()
    };

    try {
        const respuesta = await fetch(`${API_BASE}/validar`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(body)
        });

        const resultado = await respuesta.json();

        if (respuesta.status === 400) {
            for (const [campo, mensaje] of Object.entries(resultado)) {
                const nombreCampo = campo.charAt(0).toUpperCase() + campo.slice(1);
                mostrarError(nombreCampo, mensaje);
            }
            return;
        }

        if (resultado.valido) {
            elMensajeResultado.textContent = resultado.mensaje;
            elMensajeResultado.classList.add("exito");
        } else {
            elMensajeResultado.textContent = resultado.mensaje;
            elMensajeResultado.classList.add("fracaso");
        }
    } finally {
        elBtnContinuar.disabled = false;
    }
});

cargarEstados();
