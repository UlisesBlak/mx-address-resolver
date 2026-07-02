package io.github.ulisesblak.mxaddressresolver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "localidad")
public class Localidad {

    @EmbeddedId
    private LocalidadId id;

    @ManyToOne
    @MapsId("estado")
    @JoinColumn(name = "estado")
    private Estado estado;

    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    public Localidad() {
    }

    public LocalidadId getId() {
        return id;
    }

    public void setId(LocalidadId id) {
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