package io.github.ulisesblak.mxaddressresolver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "colonia")
public class Colonia {

    @EmbeddedId
    private ColoniaId id;

    @ManyToOne
    @MapsId("cp")
    @JoinColumn(name = "cp")
    private CodigoPostal codigoPostal;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    public Colonia() {
    }

    public ColoniaId getId() {
        return id;
    }

    public void setId(ColoniaId id) {
        this.id = id;
    }

    public CodigoPostal getCodigoPostal() {
        return codigoPostal;
    }

    public void setCodigoPostal(CodigoPostal codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}