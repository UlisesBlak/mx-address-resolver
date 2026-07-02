package io.github.ulisesblak.mxaddressresolver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "codigo_postal")
public class CodigoPostal {

    @Id
    @Column(name = "cp", length = 5)
    private String cp;

    @ManyToOne
    @JoinColumn(name = "estado")
    private Estado estado;

    @Column(name = "municipio", length = 3)
    private String municipio;

    @Column(name = "localidad", length = 2)
    private String localidad;

    public CodigoPostal() {
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getLocalidad() {
        return localidad;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }
}