package com.example.gestionlignebus.dao;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.content.ContentValues;
import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Passage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PassageDAOTest {
    private PassageDAO passageDAO;
    private ArretDAO arretDAO;

    private Passage passage1;
    private Passage passage2;
    private Passage passage3;

    private Arret arret1;
    private Arret arret2;
    private Arret arret3;

    List<Passage> passages;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        passageDAO = new PassageDAO(appContext);
        passageDAO.open();

        arretDAO = new ArretDAO(appContext);
        arretDAO.open();

        arret1 = new Arret("libelle 1", "position 1");
        arret1 = arretDAO.save(arret1);
        assertNotNull(arret1.getId());

        arret2 = new Arret("libelle 2", "position 2");
        arret2 = arretDAO.save(arret2);
        assertNotNull(arret2.getId());

        arret3 = new Arret("libelle 3", "position 3");
        arret3 = arretDAO.save(arret3);
        assertNotNull(arret3.getId());


        passage1 = new Passage(arret1, LocalTime.now());
        passage2 = new Passage(arret2, LocalTime.now());
        passage3 = new Passage(arret3, LocalTime.now());
    }

    @After
    public void tearDown() {

        passageDAO.delete(passage1);
        passageDAO.delete(passage2);
        passageDAO.delete(passage3);

        arretDAO.delete(arret1);
        arretDAO.delete(arret2);
        arretDAO.delete(arret3);

        arretDAO.close();
        passageDAO.close();
    }

    @Test
    public void testSave() {
        passage1 = passageDAO.save(passage1);
        // Son id n'est pas null
        assertNotNull(passage1.getId());

        assertEquals(passage1, passageDAO.findById(passage1.getId()));
    }
    @Test
    public void testSaveAll() {

    }

    @Test
    public void testDelete() {
        Passage passage = new Passage(arret1, LocalTime.now());
        passage = passageDAO.save(passage);
        int result = passageDAO.delete(passage);
        assertEquals(1, result);

        Passage passageFound = passageDAO.findById(passage.getId());
        assertNull(passageFound);
    }
    @Test
    public void testDeleteAll() {
        passage1 = passageDAO.save(passage1);
        assertNotNull(passage1.getId());

        passage2 = passageDAO.save(passage2);
        assertNotNull(passage2.getId());

        passage3 = passageDAO.save(passage3);
        assertNotNull(passage3.getId());

        List<Passage> passages = new ArrayList<>();
        passages.add(passage1);
        passages.add(passage2);
        passages.add(passage3);

        int result = passageDAO.deleteAll(passages);
        assertEquals(passages.size(), result);
    }

    @Test
    public void testUpdate() {
        // On enregistre un passage
        passage1 = passageDAO.save(passage1);

        // On le modifie
        LocalTime timeUpdated = LocalTime.now();
        passage1.setHoraire(timeUpdated);
        passage1.setPassageSuivant(passage2);
        passage1 = passageDAO.update(passage1);


        Passage passageFound = passageDAO.findById(passage1.getId());
        // On vérifie que le libelle a bien été mis à jour
        assertEquals(timeUpdated, passageFound.getHoraire());

        // On vérifie que l'objet renvoyé à bien été mis à jour
        assertEquals(passage1, passageFound);
    }

    @Test
    public void testFinfById() {
        passage1 = passageDAO.save(passage1);
        assertNotNull(passage1.getId());

        Passage passageFound = passageDAO.findById(passage1.getId());

        // On vérifie que les deux passages sont égaux
        assertEquals(passage1, passageFound);
    }

    @Test
    public void testFindAll() {
        List<Passage> passageFound = passageDAO.findAll();

        assertEquals(
                passageDAO.cursorToObjectList(passageDAO.cursorFindAll()),
                passageFound);
    }

    @Test
    public void testCursorToObjectList() {
        // On enregistre des passages
        passage1 = passageDAO.save(passage1);
        // Son id n'est pas null
        assertNotNull(passage1.getId());

        passage2 = passageDAO.save(passage2);
        // Son id n'est pas null
        assertNotNull(passage2.getId());

        passage3 = passageDAO.save(passage3);
        // Son id n'est pas null
        assertNotNull(passage3.getId());

        List<Passage> passagesExpected = new ArrayList<>();
        passagesExpected.add(passage1);
        passagesExpected.add(passage2);
        passagesExpected.add(passage3);

        List<Passage> passageList = passageDAO.cursorToObjectList(passageDAO.cursorFindAll());

        assertTrue(passageList.contains(passagesExpected.get(0)));
        assertTrue(passageList.contains(passagesExpected.get(1)));
        assertTrue(passageList.contains(passagesExpected.get(2)));
    }

    @Test
    public void testObjectToContentValues() {
        passage2 = passageDAO.save(passage2);
        passage1.setPassageSuivant(passage2);
        passage1 = passageDAO.save(passage1);
        ContentValues enregistrement = passageDAO.objectToContentValues(passage1);

        assertTrue(enregistrement.containsKey(BDHelper.PASSAGE_CLE));
        assertTrue(enregistrement.containsKey(BDHelper.PASSAGE_ARRET));
        assertTrue(enregistrement.containsKey(BDHelper.PASSAGE_HORAIRE));
        assertTrue(enregistrement.containsKey(BDHelper.PASSAGE_PASSAGE_SUIVANT));

        assertEquals(passage1.getId(), enregistrement.get(BDHelper.ARRET_CLE));
        assertEquals(passage1.getArret().getId(), enregistrement.get(BDHelper.PASSAGE_ARRET));
        assertEquals(passage1.getHoraire().toString(),  enregistrement.get(BDHelper.PASSAGE_HORAIRE));
        assertEquals(passage1.getPassageSuivant().getId(), enregistrement.get(BDHelper.PASSAGE_PASSAGE_SUIVANT));
    }
}
