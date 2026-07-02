package io.github.ulisesblak.mxaddressresolver.service;

import io.github.ulisesblak.mxaddressresolver.dto.*;
import io.github.ulisesblak.mxaddressresolver.entity.*;
import io.github.ulisesblak.mxaddressresolver.exception.CodigoPostalNoEncontradoException;
import io.github.ulisesblak.mxaddressresolver.repository.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DireccionService {

    private final EstadoRepository estadoRepository;
    private final MunicipioRepository municipioRepository;
    private final LocalidadRepository localidadRepository;
    private final ColoniaRepository coloniaRepository;
    private final CodigoPostalRepository codigoPostalRepository;

    public DireccionService(
            EstadoRepository estadoRepository,
            MunicipioRepository municipioRepository,
            LocalidadRepository localidadRepository,
            ColoniaRepository coloniaRepository,
            CodigoPostalRepository codigoPostalRepository) {
        this.estadoRepository = estadoRepository;
        this.municipioRepository = municipioRepository;
        this.localidadRepository = localidadRepository;
        this.coloniaRepository = coloniaRepository;
        this.codigoPostalRepository = codigoPostalRepository;
    }

    public List<EstadoDto> listarEstados() {
        return estadoRepository.findAll().stream()
                .map(e -> new EstadoDto(e.getClave(), e.getNombreEstado()))
                .toList();
    }

    public List<MunicipioDto> listarMunicipiosPorEstado(String claveEstado) {
        return municipioRepository.findByEstado_Clave(claveEstado).stream()
                .map(m -> new MunicipioDto(m.getId().getClave(), m.getDescripcion()))
                .toList();
    }

    public List<LocalidadDto> listarLocalidadesPorEstado(String claveEstado) {
        return localidadRepository.findByEstado_Clave(claveEstado).stream()
                .map(l -> new LocalidadDto(l.getId().getClave(), l.getDescripcion()))
                .toList();
    }

    public ResolucionCpResponse resolverPorCodigoPostal(String cp) {
        CodigoPostal codigoPostal = codigoPostalRepository.findById(cp)
                .orElseThrow(() -> new CodigoPostalNoEncontradoException(cp));

        Estado estado = codigoPostal.getEstado();
        EstadoDto estadoDto = new EstadoDto(estado.getClave(), estado.getNombreEstado());

        List<MunicipioDto> municipios = listarMunicipiosPorEstado(estado.getClave());
        List<LocalidadDto> localidades = listarLocalidadesPorEstado(estado.getClave());

        List<ColoniaDto> colonias = coloniaRepository.findByCodigoPostal_Cp(cp).stream()
                .map(c -> new ColoniaDto(c.getId().getClave(), c.getDescripcion()))
                .toList();

        return new ResolucionCpResponse(
                cp,
                estadoDto,
                municipios,
                localidades,
                colonias,
                codigoPostal.getMunicipio(),
                codigoPostal.getLocalidad());
    }
    

    public ValidacionResponse validarDireccion(DireccionValidacionRequest request) {
        Optional<CodigoPostal> codigoPostalOpt = codigoPostalRepository.findById(request.cp());

        if (codigoPostalOpt.isEmpty()) {
            return new ValidacionResponse(false, "El codigo postal no existe en el catalogo");
        }

        CodigoPostal codigoPostal = codigoPostalOpt.get();

        if (!codigoPostal.getEstado().getClave().equals(request.estadoClave())) {
            return new ValidacionResponse(false, "El estado no corresponde al codigo postal");
        }

        boolean municipioValido = municipioRepository
                .findByEstado_Clave(request.estadoClave()).stream()
                .anyMatch(m -> m.getId().getClave().equals(request.municipioClave()));

        if (!municipioValido) {
            return new ValidacionResponse(false, "El municipio no corresponde al estado seleccionado");
        }

        boolean localidadValida = localidadRepository
                .findByEstado_Clave(request.estadoClave()).stream()
                .anyMatch(l -> l.getId().getClave().equals(request.localidadClave()));

        if (!localidadValida) {
            return new ValidacionResponse(false, "La localidad no corresponde al estado seleccionado");
        }

        boolean coloniaValida = coloniaRepository
                .findByCodigoPostal_Cp(request.cp()).stream()
                .anyMatch(c -> c.getId().getClave().equals(request.coloniaClave()));

        if (!coloniaValida) {
            return new ValidacionResponse(false, "La colonia no corresponde al codigo postal");
        }

        return new ValidacionResponse(true, "Direccion valida");
    }
}