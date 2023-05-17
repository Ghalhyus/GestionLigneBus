package com.example.gestionlignebus.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Arret {
    private Long id;
    private String libelle;
    private String position;

    public Arret() {}

    public Arret(String libelle, String position){
        this.libelle = libelle;
        this.position = position;
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

    public String getPosition() {
        return position;
    }
    public void setPosition(String position) {
        this.position = position;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof  Arret && Objects.equals(((Arret) obj).getId(), this.getId())
                    && ((Arret) obj).getLibelle().equals(this.libelle)
                    && ((Arret) obj).getPosition().equals(this.position);
    }

    @Override
    public String toString() {
        return libelle;
    }
    
    public static List<String> getLibellesArrets(List<Arret> arrets) {
        ArrayList<String> nomsArrets = new ArrayList<>();

        for (Arret arret : arrets) {
            nomsArrets.add(arret.getLibelle());
        }

        return nomsArrets;
    }
}
