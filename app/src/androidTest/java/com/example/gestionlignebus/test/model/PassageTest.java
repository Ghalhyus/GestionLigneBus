package com.example.gestionlignebus.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.gestionlignebus.dao.BDHelper;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Periode;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PassageTest {
    private Passage passage1;
    private Passage passage2;
    private Passage passage3;
    private Passage passage4;
    private Passage passage1Bis;
    private Passage passage1HoraireDifferent;
    private List<Passage> passages;
    private Arret arret1;
    private Arret arret2;
    private Arret arret3;
    private Arret arret4;
    @Before
    public void setUp() {
        arret1 = new Arret("libelle 1", "position 1");
        arret1.setId(1L);

        arret2 = new Arret("libelle 2", "position 2");
        arret2.setId(2L);

        arret3 = new Arret("libelle 3", "position 3");
        arret3.setId(2L);

        arret4 = new Arret("libelle 4", "position 4");
        arret4.setId(2L);

        LocalTime localTime1 = LocalTime.now();
        LocalTime localTime2 = localTime1.plusMinutes(5);
        LocalTime localTime3 = localTime2.plusMinutes(5);
        LocalTime localTime4 = localTime3.plusMinutes(5);

        passage4 = new Passage(arret4, localTime4);
        passage4.setId(4L);

        passage3 = new Passage(arret3, localTime3);
        passage3.setId(3L);
        passage3.setPassageSuivant(passage4);

        passage2 = new Passage(arret2, localTime2);
        passage2.setId(2L);
        passage2.setPassageSuivant(passage3);

        passage1 = new Passage(arret1, localTime1);
        passage1.setId(1L);
        passage1.setPassageSuivant(passage2);

        passage1Bis = new Passage(arret1, localTime1);
        passage1Bis.setId(1L);
        passage1Bis.setPassageSuivant(passage2);

        passage1HoraireDifferent = new Passage(arret1, localTime2);
        passage1HoraireDifferent.setId(11L);
        passage1HoraireDifferent.setPassageSuivant(passage2);

        passages = new ArrayList<>();
        passages.add(passage1);
        passages.add(passage2);
        passages.add(passage3);
        passages.add(passage4);
    }

    @Test
    public void testEquals() {
        // Passages identiques
        assertTrue(passage1.equals(passage1Bis));

        // Passages arrêts identique mais horaire différent
        assertFalse(passage1.equals(passage1HoraireDifferent));

        // Passage arrêts différents
        assertFalse(passage1.equals(passage2));

        // test avec null
        assertFalse(passage1.equals(null));
    }

    @Test
    public void testArretIdentique() {
        // Même arrêt
        assertTrue(passage1.arretIdentique(passage1Bis));

        // Même arrêt, horaires différentes
        assertTrue(passage1.arretIdentique(passage1HoraireDifferent));

        // Arrêt différent
        assertFalse(passage1.arretIdentique(passage2));
    }

    @Test
    public void testHoraireIdentique() {
        // Même horaire
        assertTrue(passage1.horaireIdentique(passage1Bis));

        // Horaire différent
        assertFalse(passage1.horaireIdentique(passage1HoraireDifferent));

        // Arrêt différent ET horaire différent
        assertFalse(passage1.arretIdentique(passage2));



    }




    @Test
    public void testToJson() {
        JSONObject json = passage1.toJson();
        try {
            if (!json.isNull("_id")) {
                assertEquals(passage1.getId(), json.get("_id"));
            }
            if (!json.isNull("arret")) {
                assertEquals(passage1.getArret().toJson().toString(), json.get("arret").toString());
            }
            assertEquals(passage1.getHoraire().toString(), json.get("horaire"));
            if (!json.isNull("passage_suivant")) {
                assertEquals(passage1.getPassageSuivant().toJson().toString(), json.get("passage_suivant").toString());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testgetPassages() {
        assertEquals(passages, passage1.getPassages());
    }

    @Test
    public void testJsonObjectToPassage() {
        Passage convert = Passage.jsonObjectToPassage(passage1.toJson());
        // On vérifie que le chaînage est correct
        assertEquals(passage1, convert);
        assertEquals(passage2, convert.getPassageSuivant());
        assertEquals(passage3, convert.getPassageSuivant().getPassageSuivant());
        assertEquals(passage4, convert.getPassageSuivant().getPassageSuivant().getPassageSuivant());
        assertNull(passage4.getPassageSuivant());
    }
}
