package io.github.ulisesblak.mxaddressresolver.repository;

import io.github.ulisesblak.mxaddressresolver.entity.Colonia;
import io.github.ulisesblak.mxaddressresolver.entity.ColoniaId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ColoniaRepository extends JpaRepository<Colonia, ColoniaId> {

    List<Colonia> findByCodigoPostal_Cp(String cp);
}