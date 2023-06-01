package com.example.gestionlignebus.test.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.ContentValues;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.BDHelper;
import com.example.gestionlignebus.dao.LigneDAO;
import com.example.gestionlignebus.dao.PassageDAO;
import com.example.gestionlignebus.dao.PeriodeDAO;
import com.example.gestionlignebus.dao.TrajetDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.annotation.Target;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrajetDAOTest {
    private TrajetDAO trajetDAO;
    private PeriodeDAO periodeDAO;
    private LigneDAO ligneDAO;
    private ArretDAO arretDAO;
    private PassageDAO passageDAO;

    private Periode periode1;
    private Periode periode2;

    private Ligne ligne1;
    private Ligne ligne2;

    private Passage passage1;
    private Passage passage2;

    private Arret arret1;
    private Arret arret2;

    private Trajet trajet1;
    private Trajet trajet2;
    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        trajetDAO = new TrajetDAO(appContext);
        trajetDAO.open();

        periodeDAO = new PeriodeDAO(appContext);
        periodeDAO.open();

        ligneDAO = new LigneDAO(appContext);
        ligneDAO.open();

        arretDAO = new ArretDAO(appContext);
        arretDAO.open();

        passageDAO = new PassageDAO(appContext);
        passageDAO.open();

        // Periode
        periode1 = new Periode("Test Vacances scolaire");
        Periode periodeFound = periodeDAO.findByLibelle(periode1.getLibelle());
        if (periodeFound != null ) {
            periode1 = periodeFound;
        } else {
            periode1 = periodeDAO.save(periode1);
        }

        periode2 = new Periode("Test Vacances été");
        periodeFound = periodeDAO.findByLibelle(periode2.getLibelle());
        if (periodeFound != null ) {
            periode2 = periodeFound;
        } else {
            periode2 = periodeDAO.save(periode2);
        }

        // Arret
        arret1 = new Arret("Test Arret 1", "test position 1");
        Arret arretFound = arretDAO.findByLibelle(arret1.getLibelle());
        if (arretFound != null ) {
            arret1 = arretFound;
        } else {
            arret1 = arretDAO.save(arret1);
        }

        arret2 = new Arret("Test Arret 2", "test position 2");
        arretFound = arretDAO.findByLibelle(arret2.getLibelle());
        if (arretFound != null ) {
            arret2 = arretFound;
        } else {
            arret2 = arretDAO.save(arret2);
        }

        // Ligne
        ligne1 = new Ligne("Ligne A test");
        Ligne ligneFound = ligneDAO.findByLibelle(ligne1.getLibelle());
        if (ligneFound != null ) {
            ligne1 = ligneFound;
        } else {
            ligne1.setArrets(Arrays.asList(arret1, arret2));
            ligne1.setArretDepart(arret1);
            ligne1.setArretRetour(arret2);
            ligne1 = ligneDAO.save(ligne1);
        }

        ligne2 = new Ligne("Ligne B test");
        ligneFound = ligneDAO.findByLibelle(ligne2.getLibelle());
        if (ligneFound != null ) {
            ligne2 = ligneFound;
        } else {
            ligne2.setArrets(Arrays.asList(arret1, arret2));
            ligne2.setArretDepart(arret2);
            ligne2.setArretRetour(arret1);
            ligne2 = ligneDAO.save(ligne2);
        }

        // Passage
        passage1 = new Passage(arret1, LocalTime.now());
        Passage passageFound = passageDAO.findByArretAndHoraire(
                passage1.getArret(),
                passage1.getHoraire());
        if (passageFound != null) {
            passage1 = passageFound;
        } else {
            passage1 = passageDAO.save(passage1);
        }

        passage2 = new Passage(arret2, LocalTime.now());
        passageFound = passageDAO.findByArretAndHoraire(
                passage2.getArret(),
                passage2.getHoraire());
        if (passageFound != null) {
            passage2 = passageFound;
        } else {
            passage2 = passageDAO.save(passage2);
        }

        trajet1 = trajetDAO.save(new Trajet(periode1, ligne1, passage1));
        trajet2 = trajetDAO.save(new Trajet(periode2, ligne2, passage2));

    }

    @After
    public void tearDown() {
        passageDAO.delete(passage1);
        passageDAO.delete(passage2);
        ligneDAO.delete(ligne1);
        ligneDAO.delete(ligne2);
        periodeDAO.delete(periode1);
        periodeDAO.delete(periode2);
        arretDAO.delete(arret1);
        arretDAO.delete(arret2);
        trajetDAO.delete(trajet1);
        trajetDAO.delete(trajet2);
    }

    @Test
    public void testSave() {
        Trajet trajet3 = trajetDAO.save(
                new Trajet(
                        periode1,
                        ligne2,
                        passage1
                        )
                );

        assertNotNull(trajet3);

        int res = trajetDAO.delete(trajet3);

        assertEquals(1, res);
    }

    @Test
    public void testDelete() {
        int result = trajetDAO.delete(trajet1);
        assertEquals(1, result);

        Trajet trajetFound = trajetDAO.findById(trajet1.getId());
        assertNull(trajetFound);
    }

    @Test
    public void testDeleteAll() {
        List<Trajet> trajets = new ArrayList<>();
        trajets.add(trajet1);
        trajets.add(trajet2);

        int result = trajetDAO.deleteAll(trajets);
        assertEquals(trajets.size(), result);
    }

    @Test
    public void testUpdate() {

        // On le modifie
        trajet1.setLigne(ligne2);
        trajet1.setPremierPassage(passage2);

        // On le met à jour en BD
        trajet1 = trajetDAO.update(trajet1);

        Trajet trajetFound = trajetDAO.findById(trajet1.getId());
        // On vérifie que le libelle a bien été mis à jour
        assertEquals(ligne2, trajetFound.getLigne());
        assertEquals(passage2, trajetFound.getPremierPassage());

        // On vérifie que l'objet renvoyé à bien été mis à jour
        assertEquals(trajet1, trajetFound);
    }

    @Test
    public void testFinfById() {
        assertNotNull(trajet1.getId());

        Trajet trajetFound = trajetDAO.findById(trajet1.getId());

        // On vérifie que les deux passages sont égaux
        assertEquals(trajet1, trajetFound);
    }

    @Test
    public void testFindAll() {
        List<Trajet> trajets = new ArrayList<>();
        trajets.add(trajet1);
        trajets.add(trajet2);

        List<Trajet> trajetFound = trajetDAO.findAll();

        assertEquals(
                trajets,
                trajetFound);
    }

    @Test
    public void testCursorToObjectList() {

        List<Trajet> trajetsExpected = new ArrayList<>();
        trajetsExpected.add(trajet1);
        trajetsExpected.add(trajet2);

        List<Trajet> trajetList = trajetDAO.cursorToObjectList(trajetDAO.cursorFindAll());

        assertTrue(trajetList.contains(trajetsExpected.get(0)));
        assertTrue(trajetList.contains(trajetsExpected.get(1)));
    }

    @Test
    public void testObjectToContentValues() {
        ContentValues enregistrement = trajetDAO.objectToContentValues(trajet1);

        assertTrue(enregistrement.containsKey(BDHelper.TRAJET_CLE));
        assertTrue(enregistrement.containsKey(BDHelper.TRAJET_PERIODE));
        assertTrue(enregistrement.containsKey(BDHelper.TRAJET_LIGNE));
        assertTrue(enregistrement.containsKey(BDHelper.TRAJET_PREMIER_PASSAGE));

        assertEquals(trajet1.getId(), enregistrement.get(BDHelper.TRAJET_CLE));
        assertEquals(trajet1.getPeriode().getId(), enregistrement.get(BDHelper.TRAJET_PERIODE));
        assertEquals(trajet1.getLigne().getId(),  enregistrement.get(BDHelper.TRAJET_LIGNE));
        assertEquals(trajet1.getPremierPassage().getId(), enregistrement.get(BDHelper.TRAJET_PREMIER_PASSAGE));
    }

    @Test
    public  void testFindByPeriodeAndArret(){
        List<Trajet> trajetFound = trajetDAO.findByPeriodeAndArret(periode1, arret1);

        assertTrue(
                trajetFound.contains(trajet1));
    }
}
