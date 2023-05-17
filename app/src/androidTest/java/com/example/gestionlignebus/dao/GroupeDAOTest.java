package com.example.gestionlignebus.dao;

import static org.junit.Assert.assertEquals;

import android.content.ContentValues;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class GroupeDAOTest {

    private GroupeDAO groupeDao;
    private ArretDAO arretDao;
    private Groupe groupeTest;
    private List<Groupe> groupesTest;
    private List<Arret> arretsTest;

    @Before
    public void setUp() throws Exception {
        groupeDao = new GroupeDAO(InstrumentationRegistry.getInstrumentation().getTargetContext());
        groupeDao.open();

        arretDao = new ArretDAO(InstrumentationRegistry.getInstrumentation().getTargetContext());
        arretDao.open();

        arretsTest = new ArrayList<>();
        arretsTest.add(new Arret("Arrêt 1", "1"));
        arretsTest.add(new Arret("Arrêt 2", "2"));
        arretsTest.add(new Arret("Arrêt 3", "3"));
        arretsTest = arretDao.saveAll(arretsTest);

        groupeTest = new Groupe("Test groupe");
        groupeTest = groupeDao.save(groupeTest);
        groupeTest = groupeDao.ajouterArret(groupeTest, arretsTest.get(0));
        groupeTest = groupeDao.ajouterArret(groupeTest, arretsTest.get(1));
        groupeTest = groupeDao.ajouterArret(groupeTest, arretsTest.get(2));

        groupesTest = new ArrayList<>();
        groupesTest.add(new Groupe("Test groupe1"));
        groupesTest.get(0).setArrets(arretsTest);
        groupesTest.add(new Groupe("Test groupe2"));
        groupesTest.get(1).setArrets(arretsTest);
        groupesTest.add(new Groupe("Test groupe3"));
        groupesTest.get(2).setArrets(arretsTest);
    }

    @After
    public void tearDown() throws Exception {
        groupeDao.delete(groupeTest);
        groupeDao.deleteAll(groupesTest);
        groupeDao.close();

        arretDao.deleteAll(arretsTest);
        arretDao.close();
    }

    @Test
    public void findById() {
        Groupe groupeTrouve = groupeDao.findById(groupeTest.getId());

        Assert.assertEquals(groupeTest, groupeTrouve);
    }

    // TODO Corriger ce test.
    @Test
    public void findAll() {
        groupesTest = groupeDao.saveAll(groupesTest);
        List<Groupe> groupesTrouves = groupeDao.findAll();

        Assert.assertEquals(groupesTest, groupesTrouves);
    }

    @Test
    public void save() {
        Assert.assertNotNull(groupeTest);
    }

    @Test
    public void saveAll() {
        int groupesSauvegardes = 0;
        int attendu = 3;

        groupesTest = groupeDao.saveAll(groupesTest);

        List<Groupe> groupesTrouves = groupeDao.findAll();
        for (Groupe groupe : groupesTrouves) {
            groupesSauvegardes += groupe.equals(groupesTest.get(0)) ? 1 : 0;
            groupesSauvegardes += groupe.equals(groupesTest.get(1)) ? 1 : 0;
            groupesSauvegardes += groupe.equals(groupesTest.get(2)) ? 1 : 0;
        }

        Assert.assertEquals(attendu, groupesSauvegardes);
    }

    @Test
    public void delete() {
        groupeDao.delete(groupeTest);

        Assert.assertNull(groupeDao.findById(groupeTest.getId()));
    }

    @Test
    public void deleteAll() {
        groupesTest = groupeDao.saveAll(groupesTest);
        groupeDao.deleteAll(groupesTest);

        List<Groupe> groupesTrouves = groupeDao.findAll();
        for (Groupe groupe : groupesTrouves) {
            Assert.assertNotEquals(groupe,groupesTest.get(0));
            Assert.assertNotEquals(groupe,groupesTest.get(1));
            Assert.assertNotEquals(groupe,groupesTest.get(2));
        }
    }

    @Test
    public void update() {
        String libelle = "Nouveau libellé";

        groupeTest.setLibelle(libelle);
        groupeTest = groupeDao.update(groupeTest);

        Assert.assertEquals(libelle, groupeTest.getLibelle());
    }

    @Test
    public void ajouterArret() {
        Arret attendu = arretDao.save(new Arret("Arrêt 4", "4"));
        groupeTest  = groupeDao.ajouterArret(groupeTest, attendu);

        Assert.assertTrue(groupeTest.getArrets().contains(attendu));
    }

    @Test
    public void retirerArret() {
        Arret supprime = groupeTest.getArrets().get(2);
        groupeTest  = groupeDao.retirerArret(groupeTest, supprime);

        Assert.assertFalse(groupeTest.getArrets().contains(supprime));
    }

    @Test
    public void cursorToObject() {
        Groupe conversion = groupeDao.cursorToObject(groupeDao.cursorFindById(groupeTest.getId()));

        assertEquals(groupeTest, conversion);
    }

    // TODO corriger ce test
    @Test
    public void cursorToObjectList() {
        List<Groupe> conversions = groupeDao.cursorToObjectList(groupeDao.cursorFindAll());

        assertEquals(groupesTest, conversions);
    }

    @Test
    public void objectToContentValues() {
        ContentValues test = groupeDao.objectToContentValues(groupeTest);

        assertEquals(groupeTest.getId(), test.get(BDHelper.GROUPE_CLE));
        assertEquals(groupeTest.getLibelle(), test.get(BDHelper.GROUPE_LIBELLE));
    }
}