package com.example.gestionlignebus.model;

import com.example.gestionlignebus.dao.BDHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                && Objects.equals(((Groupe) obj).id, this.id)
                && estHomonyme(obj);
    }

    public boolean estHomonyme(Object obj) {
        return Objects.equals(((Groupe) obj).libelle, this.libelle)
                && Objects.equals(((Groupe) obj).arrets, this.arrets);
    }

    @Override
    public String toString() {
        return libelle;
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(BDHelper.GROUPE_CLE, id);
            jsonObject.put(BDHelper.GROUPE_LIBELLE, libelle);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }
}
