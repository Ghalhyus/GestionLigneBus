package com.example.gestionlignebus.model;

import java.util.ArrayList;
import java.util.List;

public class Groupe {
    private Long id;
    private String libelle;
    private List<Arret> arrets;

    public Groupe() {
    }

    public Groupe(String libelle) {
        this.libelle = libelle;
    }

    public Groupe(String libelle, List<Arret> arrets) {
        this.libelle = libelle;
        this.arrets = arrets;
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

    public List<Arret> getArrets() {
        return arrets;
    }

    public void setArrets(List<Arret> arrets) {
        this.arrets = arrets;
    }

    public Groupe addArret(Arret arret) {
        if (arrets == null) {
            arrets = new ArrayList<>();
        }
        arrets.add(arret);
        return this;
    }

    /**
     * Récupère la liste des libellés d'une liste de groupes.
     * @param groupes La liste de groupes à traiter.
     * @return Le libellé de chaque groupe.
     */
    public static List<String> getLibellesGroupes(List<Groupe> groupes) {
        ArrayList<String> nomsGroupes = new ArrayList<>();

        for (Groupe groupe : groupes) {
            nomsGroupes.add(groupe.getLibelle());
        }

        return nomsGroupes;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Groupe
                && ((Groupe) obj).libelle.equals(this.libelle)
                && ((((Groupe) obj).arrets == null && this.arrets == null)
                    || ((((Groupe) obj).arrets != null && this.arrets != null)
                        && ((Groupe) obj).arrets.equals(this.arrets)));
    }
}
