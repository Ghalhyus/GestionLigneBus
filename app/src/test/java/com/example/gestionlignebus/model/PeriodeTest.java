package com.example.gestionlignebus.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class PeriodeTest {
    private Periode periode;
    private String periodeLib = "libelle 1";
    private Periode periodeBis;
    private Periode periodeHomonyme;
    private Periode periodeDifferente;
    private List<Periode> periodes;
    @Before
    public void setUp() {
        periode = new Periode(periodeLib);
        periode.setId(1L);

        periodeBis = new Periode(periodeLib);
        periodeBis.setId(1L);

        periodeHomonyme = new Periode(periodeLib);
        periodeHomonyme.setId(10L);

        periodeDifferente = new Periode("libelle 2");
        periodeDifferente.setId(2L);

        periodes = new ArrayList<>();
        periodes.add(periode);
        periodes.add(periodeDifferente);
    }

    @Test
    public void testEquals() {
        // Arrets identiques
        assertEquals(periode, periodeBis);

        // Arrets non identiques
        assertNotEquals(periode, periodeDifferente);

        // Arrets homonymes non identiques
        assertNotEquals(periode, periodeHomonyme);

        // test avec null
        assertNotEquals(null, periode);

        assertNotEquals(periode, new Groupe());
    }


    @Test
    public void testEstHomonyme() {
        // On essaie avec des ids différents
        assertTrue(periode.estHomonyme(periodeHomonyme));

        // On essaie avec des id identiques
        assertTrue(periode.estHomonyme(periodeBis));

        // On essaie avec des arrets différents
        assertFalse(periode.estHomonyme(periodeDifferente));
    }

    @Test
    public void testToString() {
        assertEquals(periodeLib, periode.toString());
    }

    @Test
    public void testGetLibellesPeriodes() {
        List<String> libellesLignes = Periode.getLibellesPeriodes(periodes);

        assertEquals(periode.getLibelle(), libellesLignes.get(0));
        assertEquals(periodeDifferente.getLibelle(), libellesLignes.get(1));
    }
}
