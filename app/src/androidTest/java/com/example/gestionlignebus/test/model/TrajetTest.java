package com.example.gestionlignebus.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TrajetTest {
    private Trajet trajet;
    private Trajet trajetBis;
    private Trajet trajetDifferent;
    private List<Trajet> trajets;
    private Periode periode;
    private Periode periodeDifferente;
    private Ligne ligne;
    private Ligne ligneDifferente;
    private Arret arret;
    private Arret arretDifferent;
    private Passage passage;
    private Passage passageDifferent;

    @Before
    public void setUp() {
        arret = new Arret("libelle 1", "position 1");
        arret.setId(1L);

        arretDifferent = new Arret("libelle 2", "position 2");
        arretDifferent.setId(2L);

        LocalTime localTime1 = LocalTime.now();
        LocalTime localTime2 = localTime1.plusMinutes(5);

        passage = new Passage(arret, localTime1);
        passage.setId(1L);

        passageDifferent = new Passage(arretDifferent, localTime2);
        passageDifferent.setId(2L);

        periode = new Periode("Periode 1");
        periode.setId(1L);

        periodeDifferente = new Periode("Periode 2");
        periodeDifferente.setId(2L);

        ligne = new Ligne("Ligne 1");
        ligne.setId(1L);

        ligneDifferente = new Ligne("Ligne 2");
        ligneDifferente.setId(2L);

        trajet = new Trajet(periode, ligne, passage);
        trajet.setId(1L);

        trajetBis = new Trajet(periode, ligne, passage);
        trajetBis.setId(1L);

        trajetDifferent = new Trajet(periodeDifferente, ligneDifferente, passageDifferent);
        trajetDifferent.setId(2L);

        trajets = new ArrayList<>();
        trajets.add(trajet);
        trajets.add(trajetDifferent);
    }

    @Test
    public void testEquals() {
        // Arrets identiques
        assertEquals(trajet, trajetBis);

        // Arrets non identiques
        assertNotEquals(trajet, trajetDifferent);

        // test avec null
        assertNotEquals(null, trajet);
    }


    @Test
    public void testToJson() {
        JSONObject json = trajet.toJson();

        try {
            assertEquals(trajet.getId(), json.get("_id"));
            assertEquals(trajet.getPeriode().toJson().toString(), json.get("periode").toString());
            assertEquals(trajet.getLigne().toJson().toString(), json.get("ligne").toString());
            assertEquals(trajet.getPremierPassage().toJson().toString(), json.get("premier_passage").toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
