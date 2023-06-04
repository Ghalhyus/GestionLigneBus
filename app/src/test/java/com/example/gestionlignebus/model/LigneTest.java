package com.example.gestionlignebus.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.example.gestionlignebus.dao.BDHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LigneTest {
    private Ligne ligne;
    private String ligneLib = "libelle 1";
    private Ligne ligneBis;
    private Ligne ligneHomonyme;
    private Ligne ligneDifferent;
    private List<Ligne> lignes;

    private Arret arret1;
    private Arret arret2;
    @Before
    public void setUp() {
        arret1 = new Arret("libelle 1", "position 1");
        arret1.setId(1L);

        arret2 = new Arret("libelle 2", "position 2");
        arret2.setId(2L);

        ligne = new Ligne(ligneLib, arret1, arret2);
        ligne.setId(1L);

        ligneBis = new Ligne(ligneLib, arret1, arret2);
        ligneBis.setId(1L);

        ligneHomonyme = new Ligne(ligneLib, arret1, arret2);
        ligneHomonyme.setId(10L);

        ligneDifferent = new Ligne("libelle 2", arret2, arret1);
        ligneDifferent.setId(2L);

        lignes = new ArrayList<>();
        lignes.add(ligne);
        lignes.add(ligneDifferent);
    }

    @Test
    public void testEquals() {
        // Arrets identiques
        assertEquals(ligne, ligneBis);

        // Arrets non identiques
        assertNotEquals(ligne, ligneDifferent);

        // Arrets homonymes non identiques
        assertNotEquals(ligne, ligneHomonyme);

        // test avec null
        assertNotEquals(null, ligne);
    }


    @Test
    public void testEstHomonyme() {
        // On essaie avec des ids différents
        assertTrue(ligne.estHomonyme(ligneHomonyme));

        // On essaie avec des id identiques
        assertTrue(ligne.estHomonyme(ligneBis));

        // On essaie avec des arrets différents
        assertFalse(ligne.estHomonyme(ligneDifferent));
    }

    @Test
    public void testToString() {
        assertEquals(ligneLib, ligne.toString());
    }

    @Test
    public void testGetLibellesLignes() {
        List<String> libellesLignes = Ligne.getLibellesLignes(lignes);

        assertEquals(ligne.getLibelle(), libellesLignes.get(0));
        assertEquals(ligneDifferent.getLibelle(), libellesLignes.get(1));
    }
}
