package com.example.gestionlignebus.test.dao;

import static org.junit.Assert.assertEquals;

import android.content.ContentValues;

import androidx.test.platform.app.InstrumentationRegistry;

import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.BDHelper;
import com.example.gestionlignebus.dao.LigneDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Ligne;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LigneDAOTest {

    private Ligne ligneTest;
    private List<Ligne> lignesTest;
    private  ArrayList<Arret> arretsTest;
    private LigneDAO ligneDao;
    private ArretDAO arretDao;
    private Arret arret1 = new Arret("Arrêt 1", "1");
    private Arret arret2 = new Arret("Arrêt 2", "2");
    private Arret arret3 = new Arret("Arrêt 3", "3");
    private Arret arret1Bis = new Arret("Arrêt 1", "1");
    @Before
    public void setUp() throws Exception {
        ligneDao = new LigneDAO(InstrumentationRegistry.getInstrumentation().getTargetContext());
        ligneDao.open();

        arretDao = new ArretDAO(InstrumentationRegistry.getInstrumentation().getTargetContext());
        arretDao.open();


        arretsTest = new ArrayList<>();
        arretsTest.add(arret1);
        arretsTest.add(arret2);
        arretsTest.add(arret3);

        ligneTest = new Ligne();
        ligneTest.setLibelle("LigneTest");
        ligneTest.setArrets(arretsTest);
        ligneTest.setArretDepart(arretsTest.get(0));
        ligneTest.setArretRetour(arretsTest.get(2));
        ligneTest = ligneDao.save(ligneTest);

        lignesTest = new ArrayList<>();
        lignesTest.add(new Ligne());
        lignesTest.get(0).setLibelle("LigneTest 1");
        lignesTest.get(0).setArrets(arretsTest);
        lignesTest.get(0).setArretDepart(arretsTest.get(0));
        lignesTest.get(0).setArretRetour(arretsTest.get(2));

        lignesTest.add(new Ligne());
        lignesTest.get(1).setLibelle("LigneTest 2");
        lignesTest.get(1).setArrets(arretsTest);
        lignesTest.get(1).setArretDepart(arretsTest.get(0));
        lignesTest.get(1).setArretRetour(arretsTest.get(2));

        lignesTest.add(new Ligne());
        lignesTest.get(2).setLibelle("LigneTest 3");
        lignesTest.get(2).setArrets(arretsTest);
        lignesTest.get(2).setArretDepart(arretsTest.get(0));
        lignesTest.get(2).setArretRetour(arretsTest.get(2));
    }

    @After
    public void tearDown() throws Exception {
        ligneDao.delete(ligneTest);
        int result = ligneDao.deleteAll(lignesTest);

        ligneDao.close();
        arretDao.close();
    }

    @Test
    public void findById() {
        Ligne ligneTrouvee = ligneDao.findById(ligneTest.getId());

        Assert.assertEquals(ligneTest, ligneTrouvee);
    }

    @Test
    public void findAll() {
        lignesTest = ligneDao.saveAll(lignesTest);
        List<Ligne> lignesTrouves = ligneDao.findAll();

        Assert.assertEquals(lignesTest, ligneTest);
    }
    @Test
    public void save() {
        Assert.assertNotNull("La ligne n'a pas été enregistrée en base de données",
                ligneTest);
    }

    @Test
    public void saveAll() {
        int lignesSauvegardees = 0;
        int attendu = 3;

        lignesTest = ligneDao.saveAll(lignesTest);

        List<Ligne> lignesTrouvees = ligneDao.findAll();
        for (Ligne ligne : lignesTrouvees) {
            lignesSauvegardees += ligne.equals(lignesTest.get(0)) ? 1 : 0;
            lignesSauvegardees += ligne.equals(lignesTest.get(1)) ? 1 : 0;
            lignesSauvegardees += ligne.equals(lignesTest.get(2)) ? 1 : 0;
        }

        Assert.assertEquals(attendu, lignesSauvegardees);
    }

    @Test
    public void delete() {
        ligneDao.delete(ligneTest);

        Assert.assertNull(ligneDao.findById(ligneTest.getId()));
    }

    @Test
    public void deleteAll() {
        lignesTest = ligneDao.saveAll(lignesTest);
        ligneDao.deleteAll(lignesTest);

        List<Ligne> lignesTrouves = ligneDao.findAll();
        for (Ligne ligne : lignesTrouves) {
            Assert.assertFalse(ligne.equals(lignesTest.get(0)));
            Assert.assertFalse(ligne.equals(lignesTest.get(1)));
            Assert.assertFalse(ligne.equals(lignesTest.get(2)));
        }
    }

    @Test
    public void update() {
        String libelle = "Nouveau libellé";

        ligneTest.setLibelle(libelle);
        ligneTest = ligneDao.update(ligneTest);

    }

    @Test
    public void cursorToObject() {
        Ligne conversion = ligneDao.cursorToObject(ligneDao.cursorFindById(ligneTest.getId()));

        assertEquals(ligneTest, conversion);
    }

    // TODO corriger ce test
    @Test
    public void cursorToObjectList() {
        List<Ligne> conversions = ligneDao.cursorToObjectList(ligneDao.cursorFindAll());

        assertEquals(lignesTest, conversions);
    }

    @Test
    public void objectToContentValues() {
        ContentValues test = ligneDao.objectToContentValues(ligneTest);

        assertEquals(ligneTest.getId(), test.get(BDHelper.LIGNE_CLE));
        assertEquals(ligneTest.getLibelle(), test.get(BDHelper.LIGNE_LIBELLE));
        assertEquals(ligneTest.getArretDepart().getId(),
                test.get(BDHelper.LIGNE_FK_ARRET_ALLE));
        assertEquals(ligneTest.getArretRetour().getId(),
                test.get(BDHelper.LIGNE_FK_ARRET_RETOUR));
    }
}