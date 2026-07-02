package io.github.ulisesblak.mxaddressresolver.repository;

import io.github.ulisesblak.mxaddressresolver.entity.Municipio;
import io.github.ulisesblak.mxaddressresolver.entity.MunicipioId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MunicipioRepository extends JpaRepository<Municipio, MunicipioId> {

    List<Municipio> findByEstado_Clave(String claveEstado);
}