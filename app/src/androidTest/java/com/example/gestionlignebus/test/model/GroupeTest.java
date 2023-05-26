package com.example.gestionlignebus.test.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GroupeTest {
    private Groupe groupe;
    private String groupeLib = "libelle 1";
    private Groupe groupeBis;
    private Groupe groupeHomonyme;
    private Groupe groupeDifferent;
    private List<Groupe> groupes;
    @Before
    public void setUp() {
        groupe = new Groupe(groupeLib);
        groupe.setId(1L);

        groupeBis = new Groupe(groupeLib);
        groupeBis.setId(1L);

        groupeHomonyme = new Groupe(groupeLib);
        groupeHomonyme.setId(10L);

        groupeDifferent = new Groupe("libelle 2");
        groupeDifferent.setId(2L);

        groupes = new ArrayList<>();
        groupes.add(groupe);
        groupes.add(groupeDifferent);
    }

    @Test
    public void testEquals() {
        // Arrets identiques
        assertTrue(groupe.equals(groupeBis));

        // Arrets non identiques
        assertFalse(groupe.equals(groupeDifferent));

        // Arrets homonymes non identiques
        assertFalse(groupe.equals(groupeHomonyme));

        // test avec null
        assertFalse(groupe.equals(null));
    }


    @Test
    public void testEstHomonyme() {
        // On essaie avec des ids différents
        assertTrue(groupe.estHomonyme(groupeHomonyme));

        // On essaie avec des id identiques
        assertTrue(groupe.estHomonyme(groupeBis));

        // On essaie avec des arrets différents
        assertFalse(groupe.estHomonyme(groupeDifferent));
    }

    @Test
    public void testToString() {
        assertEquals(groupeLib, groupe.toString());
    }

    @Test
    public void testToJson() {
        JSONObject json = groupe.toJson();

        try {
            assertEquals(groupe.getId(), json.get("_id"));
            assertEquals(groupe.getLibelle(), json.get("libelle"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        groupe.setId(null);
        json = groupe.toJson();

        try {
            assertTrue(json.isNull("_id"));
            assertEquals(groupe.getLibelle(), json.get("libelle"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testGetLibellesArrets() {
        List<String> libellesArrets = Groupe.getLibellesGroupes(groupes);

        assertEquals(groupe.getLibelle(), libellesArrets.get(0));
        assertEquals(groupeDifferent.getLibelle(), libellesArrets.get(1));
    }
}
