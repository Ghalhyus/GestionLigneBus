package com.example.gestionlignebus.utils;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONUtils {

    private static final String LISTE_ARRET_NAME = "arrets";
    private static final String LISTE_LIGNE_NAME = "lignes";
    private static final String LISTE_PERIODE_NAME = "periodes";
    private static final String LISTE_TRAJET_NAME = "trajets";

    public static String readJSON(BufferedReader bufferedReader) {
        StringBuilder result = new StringBuilder();
        if (bufferedReader != null) {
            String line;
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result.toString();
    }

    public static List<Arret> jsonToArretList(String json) {
        List<Arret> arrets = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            jsonObject.getJSONArray(LISTE_ARRET_NAME);
            JSONArray o = jsonObject.getJSONArray(LISTE_ARRET_NAME);
            for ( int i = 0 ; i < o.length() ; i++) {
                JSONObject arretJson = o.getJSONObject(i);
                Arret arret = Arret.jsonObjectToArret(arretJson);
                arrets.add(arret);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return arrets;
    }

    public static List<Ligne> jsonToLigneList(String json) {
        List<Ligne> lignes = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            JSONArray o = jsonObject.getJSONArray(LISTE_LIGNE_NAME);
            for ( int i = 0 ; i < o.length() ; i++) {
                Ligne ligne = Ligne.jsonObjectToLigne(o.getJSONObject(i));
                lignes.add(ligne);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return lignes;
    }

    public static List<Periode> jsonToPeriodeList(String json) {
        List<Periode> periodes = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            JSONArray o = jsonObject.getJSONArray(LISTE_PERIODE_NAME);
            for ( int i = 0 ; i < o.length() ; i++) {
                JSONObject periodeJson = o.getJSONObject(i);
                Periode periode = Periode.jsonObjectToPeriode(periodeJson);
                periodes.add(periode);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return periodes;
    }

    public static List<Trajet> jsonToTrajets(String json) {
        List<Trajet> trajets = new ArrayList<>();
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            JSONArray o = jsonObject.getJSONArray(LISTE_TRAJET_NAME);
            for (int i = 0; i < o.length() ; i++) {
                JSONObject trajetJSON = o.getJSONObject(i);
                Trajet trajet = Trajet.jsonObjectToTrajet(trajetJSON);
                trajets.add(trajet);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return trajets;
    }


}
