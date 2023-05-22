package com.example.gestionlignebus.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
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

    public boolean estHomonyme(Object obj) {
        return obj instanceof Periode
                && Objects.equals(((Periode) obj).libelle, this.libelle);
    }

    @Override
    public String toString() {
        return libelle;
    }

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

    public static List<String> getLibellesPeriodes(List<Periode> periodes) {
        ArrayList<String> nomPeriodes = new ArrayList<>();

        for (Periode periode : periodes) {
            nomPeriodes.add(periode.getLibelle());
        }

        return nomPeriodes;
    }


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
