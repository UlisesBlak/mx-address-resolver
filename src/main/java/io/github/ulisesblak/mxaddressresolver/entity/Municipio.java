package io.github.ulisesblak.mxaddressresolver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "municipio")
public class Municipio {

    @EmbeddedId
    private MunicipioId id;

    @ManyToOne
    @MapsId("estado")
    @JoinColumn(name = "estado")
    private Estado estado;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    public Municipio() {
    }

    public MunicipioId getId() {
        return id;
    }

    public void setId(MunicipioId id) {
        this.id = id;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}