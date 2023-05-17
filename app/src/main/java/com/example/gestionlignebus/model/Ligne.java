package com.example.gestionlignebus.model;

import java.util.ArrayList;
import java.util.List;

public class Ligne {
    private Long id;
    private String libelle;
    private Arret arretDepartAllee;
    private Arret arretDepartRetour;
    private List<Arret> arrets;

    public Ligne() {}
    public Ligne(String ligne) {
        this.libelle = ligne;
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

    public Arret getArretDepartAllee() {
        return arretDepartAllee;
    }

    public void setArretDepartAllee(Arret arretDepartAllee) {
        this.arretDepartAllee = arretDepartAllee;
    }

    public Arret getArretDepartRetour() {
        return arretDepartRetour;
    }

    public void setArretDepartRetour(Arret arretDepartRetour) {
        this.arretDepartRetour = arretDepartRetour;
    }

    public List<Arret> getArrets() {
        return arrets;
    }

    public void setArrets(List<Arret> arrets) {
        this.arrets = arrets;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Ligne
                // On compare le libellé
                && ((Ligne) obj).libelle.equals(this.libelle)
                // On compare les arrêts
                && ((((Ligne) obj).arrets == null && this.arrets == null)
                || ((((Ligne) obj).arrets != null && this.arrets != null)
                && ((Ligne) obj).arrets.equals(this.arrets)))
                // On compare l'arrêt de départ
                && ((((Ligne) obj).arretDepartAllee == null && this.arretDepartAllee == null)
                || ((((Ligne) obj).arretDepartAllee != null && this.arretDepartAllee != null)
                && ((Ligne) obj).arretDepartAllee.equals(this.arretDepartAllee)))
                // On compare l'arrêt de d'arrivé
                && ((((Ligne) obj).arretDepartRetour == null && this.arretDepartRetour == null)
                || ((((Ligne) obj).arretDepartRetour != null && this.arretDepartRetour != null)
                && ((Ligne) obj).arretDepartRetour.equals(this.arretDepartRetour)));
    }

    public static List<String> getLibellesLignes(List<Ligne> lignes) {
        ArrayList<String> nomsLignes = new ArrayList<>();

        for (Ligne ligne : lignes) {
            nomsLignes.add(ligne.getLibelle());
        }

        return nomsLignes;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
