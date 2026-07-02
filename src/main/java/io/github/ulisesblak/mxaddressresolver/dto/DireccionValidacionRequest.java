package io.github.ulisesblak.mxaddressresolver.dto;

import jakarta.validation.constraints.NotBlank;

public record DireccionValidacionRequest(

        @NotBlank(message = "El codigo postal es requerido")
        String cp,

        @NotBlank(message = "El estado es requerido")
        String estadoClave,

        @NotBlank(message = "El municipio es requerido")
        String municipioClave,

        @NotBlank(message = "La localidad es requerida")
        String localidadClave,

        @NotBlank(message = "La colonia es requerida")
        String coloniaClave,

        @NotBlank(message = "La calle y numero son requeridos")
        String calleYNumero
) {
}