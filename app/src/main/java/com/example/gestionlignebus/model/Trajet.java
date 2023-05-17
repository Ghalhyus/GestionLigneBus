package com.example.gestionlignebus.model;

import androidx.annotation.Nullable;

public class Trajet {
    private Long id;
    private Ligne ligne;
    private Periode periode;
    private Passage premierPassage;

    public Trajet() {}
    public Trajet(Periode periode, Ligne ligne, Passage passage) {
        this.periode = periode;
        this.ligne = ligne;
        this.premierPassage = passage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Ligne getLigne() {
        return ligne;
    }

    public void setLigne(Ligne ligne) {
        this.ligne = ligne;
    }

    public Periode getPeriode() {
        return periode;
    }

    public void setPeriode(Periode periode) {
        this.periode = periode;
    }

    public Passage getPremierPassage() {
        return premierPassage;
    }

    public void setPremierPassage(Passage premierPassage) {
        this.premierPassage = premierPassage;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof  Trajet) {
            if (((Trajet) obj).getId().equals(this.getId()) &&
                    ((Trajet) obj).getLigne().equals(this.getLigne()) &&
                    ((Trajet) obj).getPremierPassage().equals(this.getPremierPassage()) &&
                    ((Trajet) obj).getPeriode().equals(this.getPeriode())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
