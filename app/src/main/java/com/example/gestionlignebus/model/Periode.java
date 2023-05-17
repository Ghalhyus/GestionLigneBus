package com.example.gestionlignebus.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Periode {

    private Long id;

    private String libelle;

    public Periode() {}

    public Periode(String libelle) {
        this.libelle = libelle;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj instanceof  Periode) {
            return Objects.equals(((Periode) obj).getId(), this.getId())
                    && Objects.equals(((Periode) obj).getLibelle(), this.getLibelle());
        } else {
            return false;
        }
    }

    public static List<String> getLibellesPeriode(List<Periode> periodes) {
        ArrayList<String> nomsPeriode = new ArrayList<>();

        for (Periode periode : periodes) {
            nomsPeriode.add(periode.getLibelle());
        }

        return nomsPeriode;
    }
}
