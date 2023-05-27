package com.example.gestionlignebus.model;

import androidx.annotation.Nullable;

import com.example.gestionlignebus.dao.BDHelper;

import org.json.JSONException;
import org.json.JSONObject;

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
        return obj instanceof Periode
                && Objects.equals(((Periode) obj).id, this.id)
                && estHomonyme(obj);
    }

    /**
     * Détermine si la ligne en paramètre possède le même libelle
     * @param obj la ligne à comparer
     * @return true si vrai
     */
    public boolean estHomonyme(Object obj) {
        return obj instanceof Periode
                && Objects.equals(((Periode) obj).libelle, this.libelle);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + libelle.hashCode();
    }

    @Override
    public String toString() {
        return libelle;
    }

    /**
     * Convertit une période en JSONObject
     * @return le JSONObject après conversion
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(BDHelper.PERIODE_CLE, id);
            jsonObject.put(BDHelper.PERIODE_LIBELLE, libelle);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    /**
     * Récupère la liste des libelles d'une liste de période
     * @param periodes la liste de periode
     * @return la liste des libelles
     */
    public static List<String> getLibellesPeriodes(List<Periode> periodes) {
        ArrayList<String> nomPeriodes = new ArrayList<>();

        for (Periode periode : periodes) {
            nomPeriodes.add(periode.getLibelle());
        }

        return nomPeriodes;
    }


    /**
     * Convertit un JSONObject en Periode
     * @param jsonObject le JSON à convertir
     * @return la période après conversion
     */
    public static Periode jsonObjectToPeriode(JSONObject jsonObject) {
        Periode periode = new Periode();
        try {
            if (!jsonObject.isNull(BDHelper.PERIODE_CLE)) {
                periode.setId((Long) jsonObject.get(BDHelper.PERIODE_CLE));
            }
            if (!jsonObject.isNull(BDHelper.PERIODE_LIBELLE)) {
                periode.setLibelle(jsonObject.getString(BDHelper.PERIODE_LIBELLE));
            }
        } catch (JSONException e) {
            return null;
        }
        return periode;
    }
}
