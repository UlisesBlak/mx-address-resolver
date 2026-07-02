package io.github.ulisesblak.mxaddressresolver.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class ColoniaId implements Serializable {

    @Column(name = "clave", length = 4)
    private String clave;

    @Column(name = "cp", length = 6)
    private String cp;

    public ColoniaId() {
    }

    public ColoniaId(String clave, String cp) {
        this.clave = clave;
        this.cp = cp;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        this.cp = cp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColoniaId)) return false;
        ColoniaId that = (ColoniaId) o;
        return Objects.equals(clave, that.clave) && Objects.equals(cp, that.cp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clave, cp);
    }
}