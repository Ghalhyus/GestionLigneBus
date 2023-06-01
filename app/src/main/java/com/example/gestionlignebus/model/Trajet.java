package com.example.gestionlignebus.model;

import static com.example.gestionlignebus.MainActivity.CLE_LOG;

import android.util.Log;

import com.example.gestionlignebus.dao.BDHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

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
        return obj instanceof Trajet
                && Objects.equals(((Trajet) obj).id, this.id)
                && Objects.equals(((Trajet) obj).periode, this.periode)
                && Objects.equals(((Trajet) obj).ligne, this.ligne)
                && Objects.equals(((Trajet) obj).premierPassage, this.premierPassage);
    }

    @Override
    public int hashCode() {
        return id.hashCode() + ligne.hashCode() + periode.hashCode() + premierPassage.hashCode();
    }

    /**
     * Convertit un trajet en JSONObject
     * @return le JSONObject après converison
     */
    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(BDHelper.TRAJET_CLE, id);
            if (periode != null) {
                jsonObject.put(BDHelper.TRAJET_PERIODE, periode.toJson());
            }
            if (ligne != null) {
                jsonObject.put(BDHelper.TRAJET_LIGNE, ligne.toJson());
            }
            if (premierPassage != null) {
                jsonObject.put(BDHelper.TRAJET_PREMIER_PASSAGE, premierPassage.toJson());
            }
        } catch (JSONException e) {
            Log.e(CLE_LOG, String.format(
                    "Erreur lors de la transformation du trajet %s %s en objet JSON.",
                    ligne, periode));
        }
        return jsonObject;
    }

    /**
     * Convertit un JSONObject en Trajet
     * @param jsonObject à convertir
     * @return le trajet après conversion
     */
    public static Trajet jsonObjectToTrajet(JSONObject jsonObject) {
        Trajet trajet = new Trajet();
        try {
            if (!jsonObject.isNull(BDHelper.TRAJET_CLE)) {
                trajet.setId((Long) jsonObject.get(BDHelper.TRAJET_CLE));
            }
            if (!jsonObject.isNull(BDHelper.TRAJET_PERIODE)) {
                trajet.setPeriode(Periode.jsonObjectToPeriode(jsonObject.getJSONObject(BDHelper.TRAJET_PERIODE)));
            }
            if (!jsonObject.isNull(BDHelper.TRAJET_LIGNE)) {
                trajet.setLigne(Ligne.jsonObjectToLigne(jsonObject.getJSONObject(BDHelper.TRAJET_LIGNE)));
            }
            if (!jsonObject.isNull(BDHelper.TRAJET_PREMIER_PASSAGE)) {
                trajet.setPremierPassage(Passage.jsonObjectToPassage(jsonObject.getJSONObject(BDHelper.TRAJET_PREMIER_PASSAGE)));
            }
        } catch (JSONException e) {
            return null;
        }
        return trajet;
    }
}
