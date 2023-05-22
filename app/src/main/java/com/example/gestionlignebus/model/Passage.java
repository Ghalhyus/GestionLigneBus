package com.example.gestionlignebus.model;

import com.example.gestionlignebus.dao.BDHelper;

import org.json.JSONException;
import org.json.JSONObject;

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
        return obj instanceof Passage
                && Objects.equals(((Passage) obj).id, this.id)
                && arretIdentique(obj)
                && horaireIdentique(obj);
    }

    public boolean arretIdentique(Object obj) {
        return obj instanceof Passage
                && Objects.equals(((Passage) obj).arret, this.arret);
    }

    public boolean horaireIdentique(Object obj) {
        return obj instanceof Passage
                && Objects.equals(((Passage) obj).horaire, this.horaire);
    }

    public JSONObject toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(BDHelper.PASSAGE_CLE, id);
            if (arret != null) {
                jsonObject.put(BDHelper.PASSAGE_ARRET, arret.toJson());
            }
            jsonObject.put(BDHelper.PASSAGE_HORAIRE, horaire.toString());
            if (passageSuivant != null) {
                jsonObject.put(BDHelper.PASSAGE_PASSAGE_SUIVANT, passageSuivant.toJson());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }


    public List<Passage> getListPassage(List<Passage> retour, Passage passage) {
        if (passage.getPassageSuivant() != null) {
            retour.add(passage.passageSuivant);
            getListPassage(retour, passage.passageSuivant);
        }

        return retour;
    }

    public List<Passage> getPassages() {
        List<Passage> passages = new ArrayList<>();
        passages.add(this);
        return getListPassage(passages, this);
    }

    /**
     * Renvoie la concaténation de l'arrêt suivi de l'horaire
     * @return la chaîen correspondante
     */
    @Override
    public String toString() {
        return arret.toString() + " " + horaire.toString();
    }

    /**
     * Convertit un objet json en objet java
     * @param jsonObject l'objet json à convertir
     * @return l'objet java converti
     */
    public static Passage jsonObjectToPassage(JSONObject jsonObject) {
        Passage passage = new Passage();
        try {
            if (!jsonObject.isNull(BDHelper.PASSAGE_CLE)) {
                passage.setId((Long) jsonObject.get(BDHelper.PASSAGE_CLE));
            }
            if (!jsonObject.isNull(BDHelper.PASSAGE_ARRET)) {
                passage.setArret(Arret.jsonObjectToArret(jsonObject.getJSONObject(BDHelper.PASSAGE_ARRET)));
            }
            if (!jsonObject.isNull(BDHelper.PASSAGE_HORAIRE)) {
                passage.setHoraire(LocalTime.parse((String) jsonObject.get(BDHelper.PASSAGE_HORAIRE)));
            }
            if (!jsonObject.isNull(BDHelper.PASSAGE_PASSAGE_SUIVANT)) {
                passage.setPassageSuivant(jsonObjectToPassage(jsonObject.getJSONObject(BDHelper.PASSAGE_PASSAGE_SUIVANT)));
            }
        } catch (JSONException e) {
            return null;
        }
        return passage;
    }

    public static List<String> getHorairesPassages(List<Passage> passages) {
        ArrayList<String> nomsPassages = new ArrayList<>();

        for (Passage passage : passages) {
            nomsPassages.add(passage.getHoraire().toString());
        }

        return nomsPassages;
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
