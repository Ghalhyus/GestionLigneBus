package com.example.gestionlignebus.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.gestionlignebus.model.Arret;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class ArretTest {

    private Arret arret1;
    private String arret1Libelle = "libelle 1";
    private Arret arret1bis;
    private Arret arret1homonyme;
    private Arret arret2;
    private Arret arret3;
    private List<Arret> arrets;
    @Before
    public void setUp() {
        arret1 = new Arret(arret1Libelle, "position 1");
        arret1.setId(1L);

        arret1bis = new Arret(arret1Libelle, "position 1");
        arret1bis.setId(1L);

        arret1homonyme = new Arret(arret1Libelle, "position 1");
        arret1homonyme.setId(10L);

        arret2 = new Arret("libelle 2", "position 2");
        arret2.setId(2L);

        arret3 = new Arret("libelle 3", "position 3");
        arret3.setId(3L);

        arrets = new ArrayList<>();
        arrets.add(arret1);
        arrets.add(arret2);
        arrets.add(arret3);
    }

    @Test
    public void testEquals() {
        // Arrets identiques
        assertEquals(arret1, arret1bis);

        // Arrets non identiques
        assertNotEquals(arret1, arret2);

        // Arrets homonymes non identiques
        assertNotEquals(arret1, arret1homonyme);

        // test avec null
        assertNotEquals(arret1, null);
    }


    @Test
    public void testEstHomonyme() {
        // On essaie avec des ids différents
        assertTrue(arret1.estHomonyme(arret1homonyme));

        // On essaie avec des id identiques
        assertTrue(arret1.estHomonyme(arret1bis));

        // On essaie avec des arrets différents
        assertFalse(arret1.estHomonyme(arret2));
    }

    @Test
    public void testToString() {
        assertEquals(arret1Libelle, arret1.toString());
    }

    @Test
    public void testToJson() {
        JSONObject json = arret1.toJson();

        try {
            assertEquals(arret1.getId(), json.get("_id"));
            assertEquals(arret1.getLibelle(), json.get("libelle"));
            assertEquals(arret1.getPosition(), json.get("position"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        arret1.setId(null);
        json = arret1.toJson();

        try {
            assertTrue(json.isNull("_id"));
            assertEquals(arret1.getLibelle(), json.get("libelle"));
            assertEquals(arret1.getPosition(), json.get("position"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetLibellesArrets() {
        List<String> libellesArrets = Arret.getLibellesArrets(arrets);

        assertEquals(arret1.getLibelle(), libellesArrets.get(0));
        assertEquals(arret2.getLibelle(), libellesArrets.get(1));
        assertEquals(arret3.getLibelle(), libellesArrets.get(2));
    }
}
