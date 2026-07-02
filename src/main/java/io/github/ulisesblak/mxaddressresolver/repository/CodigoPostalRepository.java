package io.github.ulisesblak.mxaddressresolver.repository;

import io.github.ulisesblak.mxaddressresolver.entity.CodigoPostal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CodigoPostalRepository extends JpaRepository<CodigoPostal, String> {
}