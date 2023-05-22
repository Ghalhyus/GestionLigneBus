package com.example.gestionlignebus.model;

import java.util.ArrayList;
import java.util.List;
import com.example.gestionlignebus.dao.BDHelper;

import org.json.JSONException;
import org.json.JSONObject;

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

    /**
     * Détermine si tous les attributs d'un arrêt sont égaux
     * @param obj à comparer
     * @return vrai s'ils sont égaux
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof  Arret
                && Objects.equals(((Arret) obj).id, this.id)
                && estHomonyme(obj);
    }

    /**
     * Détermine si tous les attributs exceptés les id sont identiques
     * @param obj à comparer
     * @return true s'ils sont homonymes
     */
    public boolean estHomonyme(Object obj) {
        return  Objects.equals(((Arret) obj).libelle, this.libelle)
                && Objects.equals(((Arret) obj).position, this.position);
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

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(BDHelper.ARRET_CLE, id);
            jsonObject.put(BDHelper.ARRET_LIBELLE, libelle);
            jsonObject.put(BDHelper.ARRET_POSITION, position);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    public static Arret jsonObjectToArret(JSONObject jsonObject) {
        Arret arret = new Arret();
        try {
            if (!jsonObject.isNull(BDHelper.ARRET_CLE)) {
                arret.setId((Long) jsonObject.get(BDHelper.ARRET_CLE));
            }
            if (!jsonObject.isNull(BDHelper.ARRET_LIBELLE)) {
                arret.setLibelle((String) jsonObject.get(BDHelper.ARRET_LIBELLE));
            }
            if (!jsonObject.isNull(BDHelper.ARRET_POSITION)) {
                arret.setPosition((String) jsonObject.get(BDHelper.ARRET_POSITION));
            }
        } catch (JSONException e) {
            return null;
        }
        return arret;
    }
}
