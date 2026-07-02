package io.github.ulisesblak.mxaddressresolver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Embeddable
public class LocalidadId implements Serializable {

    @Column(name = "clave", length = 2)
    @JdbcTypeCode(SqlTypes.CHAR)
    private String clave;


    @Column(name = "estado", length = 4)
    private String estado;

    public LocalidadId() {
    }

    public LocalidadId(String clave, String estado) {
        this.clave = clave;
        this.estado = estado;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LocalidadId)) return false;
        LocalidadId that = (LocalidadId) o;
        return Objects.equals(clave, that.clave) && Objects.equals(estado, that.estado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clave, estado);
    }
}