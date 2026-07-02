package io.github.ulisesblak.mxaddressresolver.dto;

import java.util.List;

public record ResolucionCpResponse(
        String cp,
        EstadoDto estado,
        List<MunicipioDto> municipios,
        List<LocalidadDto> localidades,
        List<ColoniaDto> colonias,
        String municipioSeleccionado,
        String localidadSeleccionada
) {
}
