package io.github.ulisesblak.mxaddressresolver.controller;

import io.github.ulisesblak.mxaddressresolver.dto.*;
import io.github.ulisesblak.mxaddressresolver.service.DireccionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/direccion")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping("/estados")
    public List<EstadoDto> listarEstados() {
        return direccionService.listarEstados();
    }

    @GetMapping("/municipios")
    public List<MunicipioDto> listarMunicipios(@RequestParam String estado) {
        return direccionService.listarMunicipiosPorEstado(estado);
    }

    @GetMapping("/localidades")
    public List<LocalidadDto> listarLocalidades(@RequestParam String estado) {
        return direccionService.listarLocalidadesPorEstado(estado);
    }

    @GetMapping("/codigo-postal/{cp}")
    public ResolucionCpResponse resolverPorCodigoPostal(@PathVariable String cp) {
        return direccionService.resolverPorCodigoPostal(cp);
    }

    @PostMapping("/validar")
    public ValidacionResponse validarDireccion(@Valid @RequestBody DireccionValidacionRequest request) {
        return direccionService.validarDireccion(request);
    }
}