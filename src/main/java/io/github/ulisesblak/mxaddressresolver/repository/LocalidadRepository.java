package io.github.ulisesblak.mxaddressresolver.repository;

import io.github.ulisesblak.mxaddressresolver.entity.Localidad;
import io.github.ulisesblak.mxaddressresolver.entity.LocalidadId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalidadRepository extends JpaRepository<Localidad, LocalidadId> {

    List<Localidad> findByEstado_Clave(String claveEstado);
}