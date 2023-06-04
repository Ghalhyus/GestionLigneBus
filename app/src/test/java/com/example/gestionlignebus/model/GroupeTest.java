package com.example.gestionlignebus.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(groupe, groupeBis);

        // Arrets non identiques
        assertNotEquals(groupe, groupeDifferent);

        // Arrets homonymes non identiques
        assertNotEquals(groupe, groupeHomonyme);

        // test avec null
        assertNotEquals(null, groupe);
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
    public void testGetLibellesArrets() {
        List<String> libellesArrets = Groupe.getLibellesGroupes(groupes);

        assertEquals(groupe.getLibelle(), libellesArrets.get(0));
        assertEquals(groupeDifferent.getLibelle(), libellesArrets.get(1));
    }
}
