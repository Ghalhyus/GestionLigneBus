package com.example.gestionlignebus.model;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Passage {
    private Long id;
    private Arret arret;
    private LocalTime horaire;
    private Passage passageSuivant;

    public Passage() {}
    public Passage(Arret arret, LocalTime horaire) {
        this.arret = arret;
        this.horaire = horaire;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Passage getPassageSuivant() {
        return passageSuivant;
    }

    public void setPassageSuivant(Passage passageSuivant) {
        this.passageSuivant = passageSuivant;
    }

    public Arret getArret() {
        return arret;
    }

    public void setArret(Arret arret) {
        this.arret = arret;
    }

    public LocalTime getHoraire() {
        return horaire;
    }

    public void setHoraire(LocalTime horaire) {
        this.horaire = horaire;
    }

    public boolean equals(Object obj) {
        if (obj instanceof  Passage) {
            if (Objects.equals(((Passage) obj).getId(), this.getId()) &&
                    ((Passage) obj).getHoraire().equals(this.getHoraire()) &&
                    ((Passage) obj).getArret().equals(this.getArret())
            ) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static List<String> getHorairesPassages(List<Passage> passages) {
        ArrayList<String> nomsPassages = new ArrayList<>();

        for (Passage passage : passages) {
            nomsPassages.add(passage.getHoraire().toString());
        }

        return nomsPassages;
    }

    public ArrayList<Passage> getPassages() {
        return getPassages(new ArrayList());
    }

    private ArrayList<Passage> getPassages(ArrayList listePassage) {
        Passage passage = this.passageSuivant;

        if (passage == null) {
            listePassage.add(this);
        } else {
            passage.getPassages(listePassage).add(this);
        }

        return listePassage;
    }

    public static List<String> getArretsPassages(List<Passage> passages) {
        ArrayList<String> nomsPassages = new ArrayList<>();

        for (Passage passage : passages) {
            nomsPassages.add(passage.getArret().getLibelle());
        }

        return nomsPassages;
    }
}
