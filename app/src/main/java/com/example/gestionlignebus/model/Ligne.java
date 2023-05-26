package com.example.gestionlignebus.model;

import com.example.gestionlignebus.dao.BDHelper;
import com.example.gestionlignebus.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Ligne {
    private Long id;
    private String libelle;
    private Arret arretDepart;
    private Arret arretRetour;
    private List<Arret> arrets;

    public Ligne() {}
    public Ligne(String ligne) {
        this.libelle = ligne;
    }
    public Ligne(String ligne, Arret arretDepart, Arret arretRetour) {
        this.libelle = ligne;
        this.arretDepart = arretDepart;
        this.arretRetour = arretRetour;
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

    public Arret getArretDepart() {
        return arretDepart;
    }

    public void setArretDepart(Arret arretDepart) {
        this.arretDepart = arretDepart;
    }

    public Arret getArretRetour() {
        return arretRetour;
    }

    public void setArretRetour(Arret arretRetour) {
        this.arretRetour = arretRetour;
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
                // On compare les id
                && Objects.equals(((Ligne) obj).id, this.id)
                && estHomonyme(obj);
    }

    /**
     * Renvoie la liste des libelle d'une liste de ligne
     * @param lignes la liste de ligne
     * @return la liste des libelles
     */
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

    /**
     * Vérifie si deux lignes ne sont pas homonymes
     * Renvoie true si elles possèdent le même :
     * libelle
     * arretDepart
     * arretRetour
     * @param obj
     * @return
     */
    public boolean estHomonyme(Object obj) {
        return obj instanceof Ligne
                && Objects.equals(((Ligne) obj).libelle, this.libelle)
                && Objects.equals(((Ligne) obj).arrets, this.arrets)
                && Objects.equals(((Ligne) obj).arretDepart, this.arretDepart)
                && Objects.equals(((Ligne) obj).arretRetour, this.arretRetour);
    }

    /**
     * Convertit une ligne en objet JSONObject
     * @return le JSONObject
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(BDHelper.LIGNE_CLE, id);
            jsonObject.put(BDHelper.LIGNE_LIBELLE, libelle);
            if (arretDepart != null) {
                jsonObject.put(BDHelper.LIGNE_FK_ARRET_ALLE, arretDepart.getLibelle());
            }
            if (arretRetour != null) {
                jsonObject.put(BDHelper.LIGNE_FK_ARRET_RETOUR, arretRetour.getLibelle());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    /**
     * Convertit un JSONObject en Ligne
     * @param jsonObject à convertir
     * @return la ligne après conversion
     */
    public static Ligne jsonObjectToLigne(JSONObject jsonObject) {
        Ligne ligne = new Ligne();
        try {
            if (!jsonObject.isNull(BDHelper.LIGNE_CLE)) {
                ligne.setId((Long) jsonObject.get(BDHelper.LIGNE_CLE));
            }
            if (!jsonObject.isNull(BDHelper.LIGNE_LIBELLE)) {
                ligne.setLibelle(jsonObject.getString(BDHelper.LIGNE_LIBELLE));
            }
            if (!jsonObject.isNull(BDHelper.LIGNE_FK_ARRET_ALLE)) {
                ligne.setArretDepart(Arret.jsonObjectToArret(jsonObject.getJSONObject(BDHelper.LIGNE_FK_ARRET_ALLE)));
            }
            if (!jsonObject.isNull(BDHelper.LIGNE_FK_ARRET_RETOUR)) {
                ligne.setArretRetour(Arret.jsonObjectToArret(jsonObject.getJSONObject(BDHelper.LIGNE_FK_ARRET_RETOUR)));
            }
            if (!jsonObject.isNull(JSONUtils.LISTE_ARRET_NAME)) {
                JSONArray arrets = jsonObject.getJSONArray(JSONUtils.LISTE_ARRET_NAME);
                for (int i = 0 ; i < arrets.length() ; i++) {
                    Arret arret = Arret.jsonObjectToArret(arrets.getJSONObject(i));
                    if (arret != null) {
                        ligne.ajouterArret(arret);
                    }
                }
            }
        } catch (JSONException e) {
            return null;
        }
        return ligne;
    }

    /**
     * Ajoute un arrêt à la liste des arrêts d'une ligne
     * @param arret à ajouter
     */
    private void ajouterArret(Arret arret) {
        if (arrets == null) {
            arrets = new ArrayList<>();
        }
        arrets.add(arret);
    }
}
