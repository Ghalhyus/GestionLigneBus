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

    public static final String LISTE_ARRET_NAME = "arrets";
    public static final String LISTE_LIGNE_NAME = "lignes";
    public static final String LISTE_PERIODE_NAME = "periodes";
    public static final String LISTE_TRAJET_NAME = "trajets";

    /**
     * Constructeur privé pour cacher le constructeur implicite
     */
    private JSONUtils() {
        // empty body
    }

    /**
     * Lis un fichier et renvoi un String de son contenu
     * @param bufferedReader
     * @return
     */
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

    /**
     * Extrait une JSONArray du contenu d'un fichier JSON et renvoi une liste de ses arrets
     * @param json contenu du fichier des données stables
     * @return la liste des arrêts
     */
    public static List<Arret> jsonToArretList(String json) {
        List<Arret> arrets = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            if (!jsonObject.isNull(LISTE_ARRET_NAME)) {
                jsonObject.getJSONArray(LISTE_ARRET_NAME);
                JSONArray o = jsonObject.getJSONArray(LISTE_ARRET_NAME);
                arrets = new ArrayList<>();
                for ( int i = 0 ; i < o.length() ; i++) {
                    JSONObject arretJson = o.getJSONObject(i);
                    Arret arret = Arret.jsonObjectToArret(arretJson);
                    arrets.add(arret);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return arrets;
    }

    /**
     * Extrait une JSONArray du contenu d'un fichier JSON et renvoi une liste de ses lignes
     * @param json contenu du fichier des données stables
     * @return la liste des lignes
     */
    public static List<Ligne> jsonToLigneList(String json) {
        List<Ligne> lignes = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            if (!jsonObject.isNull(LISTE_LIGNE_NAME)) {
                lignes = new ArrayList<>();
                JSONArray o = jsonObject.getJSONArray(LISTE_LIGNE_NAME);
                for ( int i = 0 ; i < o.length() ; i++) {
                    Ligne ligne = Ligne.jsonObjectToLigne(o.getJSONObject(i));
                    lignes.add(ligne);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return lignes;
    }

    /**
     * Extrait une liste de période du JSON contenant l'ensemble des données
     * @param json du fichier
     * @return la liste des périodes
     */
    public static List<Periode> jsonToPeriodeList(String json) {
        List<Periode> periodes = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            if ( !jsonObject.isNull(LISTE_PERIODE_NAME)) {
                JSONArray o = jsonObject.getJSONArray(LISTE_PERIODE_NAME);
                periodes = new ArrayList<>();
                for ( int i = 0 ; i < o.length() ; i++) {
                    JSONObject periodeJson = o.getJSONObject(i);
                    Periode periode = Periode.jsonObjectToPeriode(periodeJson);
                    periodes.add(periode);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return periodes;
    }

    /**
     * Extrait une liste de trajets du JSON contenant l'ensemble des données
     * @param json contenu du fichier
     * @return la liste des trajets
     */
    public static List<Trajet> jsonToTrajets(String json) {
        List<Trajet> trajets = null;
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(json);
            if (!jsonObject.isNull(LISTE_TRAJET_NAME)) {
                trajets = new ArrayList<>();
                JSONArray o = jsonObject.getJSONArray(LISTE_TRAJET_NAME);
                for (int i = 0; i < o.length() ; i++) {
                    JSONObject trajetJSON = o.getJSONObject(i);
                    Trajet trajet = Trajet.jsonObjectToTrajet(trajetJSON);
                    trajets.add(trajet);
                }
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return trajets;
    }


}
