package com.example.gestionlignebus.test.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;
import com.example.gestionlignebus.utils.JSONUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JSONUtilsTest {
    private JSONObject jsonStable;
    private JSONObject jsonChangeant;
    private JSONArray arretsJson;
    private JSONArray periodesJson;
    private JSONArray lignesJson;
    private JSONArray trajetsJson;

    // Arrets
    private Arret arretA = new Arret("Arret A", "Position A");
    private Arret arretB = new Arret("Arret B", "Position B");
    private Arret arretC = new Arret("Arret C", "Position C");
    private Arret arretD = new Arret("Arret D", "Position D");

    // Periode
    private Periode periode1 = new Periode("Periode 1");
    private Periode periode2 = new Periode("Periode 2");
    private Periode periode3 = new Periode("Periode 3");

    // Lignes
    private JSONObject ligne1 = new JSONObject();
    private JSONObject ligne2 = new JSONObject();
    private JSONObject ligne3 = new JSONObject();

    @Before
    public void setUp() {
        jsonStable = new JSONObject();
        jsonChangeant = new JSONObject();
        arretsJson = new JSONArray();
        lignesJson = new JSONArray();
        periodesJson = new JSONArray();
        trajetsJson = new JSONArray();
        try {
            // Arret
            JSONObject arret = new JSONObject();
            arret.put("libelle", "Arret A");
            arret.put("position", "Position A");
            arretsJson.put(arret);

            arret = new JSONObject();
            arret.put("libelle", "Arret B");
            arret.put("position", "Position B");
            arretsJson.put(arret);

            arret = new JSONObject();
            arret.put("libelle", "Arret C");
            arret.put("position", "Position C");
            arretsJson.put(arret);

            arret = new JSONObject();
            arret.put("libelle", "Arret D");
            arret.put("position", "Position D");
            arretsJson.put(arret);

            // Periode
            JSONObject periode = new JSONObject();
            periode.put("libelle", "Periode 1");
            periodesJson.put(periode);

            periode = new JSONObject();
            periode.put("libelle", "Periode 2");
            periodesJson.put(periode);

            periode = new JSONObject();
            periode.put("libelle", "Periode 3");
            periodesJson.put(periode);

            // Ligne
            ligne1.put("libelle", "Ligne 1");
            ligne1.put("arretDepart", "Arret A");
            ligne1.put("arretRetour", "Arret D");
            lignesJson.put(ligne1);


            ligne2.put("libelle", "Ligne 2");
            ligne2.put("arretDepart", "Arret A");
            ligne2.put("arretRetour", "Arret C");
            lignesJson.put(ligne2);

            ligne3 = new JSONObject();
            ligne3.put("libelle", "Ligne 3");
            ligne3.put("arretDepart", "Arret B");
            ligne3.put("arretRetour", "Arret D");
            lignesJson.put(ligne3);

            JSONObject passage4 = new JSONObject();
            passage4.put("arret", arretD.toJson());
            passage4.put("horaire", "08:20");

            JSONObject passage3 = new JSONObject();
            passage3.put("arret", arretC.toJson());
            passage3.put("horaire", "08:15");
            passage3.put("passage_suivant", passage4);

            JSONObject passage2 = new JSONObject();
            passage2.put("arret", arretB.toJson());
            passage2.put("horaire", "08:10");
            passage2.put("passage_suivant", passage3);

            JSONObject passage1 = new JSONObject();
            passage1.put("arret", arretA.toJson());
            passage1.put("horaire", "08:05");
            passage1.put("passage_suivant", passage2);


            JSONObject trajet = new JSONObject();
            trajet.put("periode", periode1.toJson());
            trajet.put("ligne", ligne1.toString());
            trajet.put("premier_passage", passage1);

            trajetsJson.put(trajet);

            jsonStable.put("arrets", arretsJson);
            jsonStable.put("periodes", periodesJson);
            jsonStable.put("lignes", lignesJson);

            jsonChangeant.put("trajets", trajetsJson);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testArretList() {
        List<Arret> arrets = JSONUtils.jsonToArretList(jsonStable.toString());

        assertTrue(arrets.contains(arretA));
        assertTrue(arrets.contains(arretB));
        assertTrue(arrets.contains(arretC));
        assertTrue(arrets.contains(arretD));
    }

    @Test
    public void testPeriodeList() {
        List<Periode> periodes = JSONUtils.jsonToPeriodeList(jsonStable.toString());

        assertTrue(periodes.contains(periode1));
        assertTrue(periodes.contains(periode2));
        assertTrue(periodes.contains(periode3));
    }

    @Test
    public void testLigneList() {
        List<Ligne> lignes = JSONUtils.jsonToLigneList(jsonStable.toString());

        assertEquals(ligne1, lignes.get(0));
        assertEquals(ligne2, lignes.get(1));
        assertEquals(ligne3, lignes.get(2));

    }

    @Test
    public void test() {
        List<Trajet> trajets = JSONUtils.jsonToTrajets(jsonChangeant.toString());
    }

}
