package com.example.gestionlignebus.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import android.content.ContentValues;
import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;
import com.example.gestionlignebus.model.Ligne;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ArretDAOTest {
    private ArretDAO arretDAO;
    private GroupeDAO groupeDAO;
    private LigneDAO ligneDAO;
    private Arret arret1;
    private Arret arret2;
    private Arret arret3;
    private List<Arret> arrets;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        arretDAO = new ArretDAO(appContext);
        arretDAO.open();

        ligneDAO = new LigneDAO(appContext);
        ligneDAO.open();

        groupeDAO = new GroupeDAO(appContext);
        groupeDAO.open();

        arret1 = new Arret("libelle 1", "position 1");
        arret2 = new Arret("libelle 2", "position 2");
        arret3 = new Arret("libelle 3", "position 3");

        arrets = new ArrayList<>();
        arrets.add(arret1);
        arrets.add(arret2);
        arrets.add(arret3);
    }

    @After
    public void tearDown() {
        arretDAO.close();
        ligneDAO.close();
        groupeDAO.close();
    }


    @Test
    public void testSaveAndDelete() {
        // Etant donné un arret enregistré
        Arret arret = arretDAO.save(arret1);

        // Il n'est pas null
        assertNotNull(arret);

        // Son id n'est plus null
        assertNotNull(arret.getId());

        // Il est sauvegardé dans la base
        Arret sauv = arretDAO.findById(arret.getId());
        assertEquals(arret, sauv);

        int result = arretDAO.delete(arret);
        assertEquals(1, result);
    }

    @Test
    public void testSaveAndDeleteAll() {
        // Etant donné une liste d'arret enregistré
        List<Arret> arretsSauv = arretDAO.saveAll(arrets);

        // La liste n'est pas null
        assertNotNull(arretsSauv);

        // Tous les arrets sont sauvegardés et la liste n'est pas vide
        assertEquals(arrets.size(), arretsSauv.size());

        // Pour chaque arret :
        for (Arret arret : arretsSauv) {
            // l'id n'est pas null
            assertNotNull(arret.getId());

            // Il est sauvegardé dans la base
            Arret sauv = arretDAO.findById(arret.getId());
            assertEquals(arret, sauv);
        }

        // On supprime les nouveaux arrets
        int result = arretDAO.deleteAll(arretsSauv);
        assertEquals(arretsSauv.size(), result);
    }

    @Test
    public void testUpdate() {
        // On enregistre un arret
        Arret arret = arretDAO.save(arret1);

        // On change le libelle
        String libelleUpdated = "libelle 1 updated";
        arret.setLibelle(libelleUpdated);

        // On vérifie que le libelle a bien été mis à jour
        assertEquals(libelleUpdated, arret.getLibelle());

        Arret arretUpdated = arretDAO.update(arret);

        // On vérifie que l'objet renvoyé à bien été mis à jour
        assertEquals(arret, arretUpdated);

        // On supprime l'arret
        int result = arretDAO.delete(arret);

        assertEquals(1, result);
    }

    @Test
    public void testFindById() {
        // On sauvegarde un arret
        Arret arret = arretDAO.save(arret1);

        // L'arret trouve
        Arret arretFound = arretDAO.findById(arret.getId());

        // On vérifie si les arrets sont identiques
        assertEquals(arret,arretFound);

        // On supprime l'arret
        int result = arretDAO.delete(arret);

        assertEquals(1, result);
    }

    @Test
    public void testFindAll() {
        // On enregistre des arrets
        Arret arret1Sauv = arretDAO.save(arret1);
        Arret arret2Sauv = arretDAO.save(arret2);
        Arret arret3Sauv = arretDAO.save(arret3);

        List<Arret> arretExpected = new ArrayList<>();
        arretExpected.add(arret1Sauv);
        arretExpected.add(arret2Sauv);
        arretExpected.add(arret3Sauv);

        List<Arret> arretsFound = arretDAO.findAll();

        assertEquals(arretExpected, arretsFound);

        // On supprime l'arret
        int result = arretDAO.deleteAll(arretsFound);

        assertEquals(arretsFound.size(), result);
    }

    @Test
    public void testFindByGroupe() {
        Groupe groupe1 = new Groupe("libelle 1");

        // On crée un groupe
        groupe1 = groupeDAO.save(groupe1);
        assertNotNull(groupe1.getId());

        Arret arret = arretDAO.save(arret1);
        assertNotNull(arret.getId());

        // On lui ajoute un arret
        groupe1 = groupeDAO.ajouterArret(groupe1, arret);
        assertEquals(1, groupe1.getArrets().size());

        // On récupère les arrêts du groupe
        List<Arret> arretsGroupe = groupe1.getArrets();

        // On vérifie que celle-ci contient l'arret ajouté
        assertTrue(arretsGroupe.contains(arret));

        arretDAO.delete(arret);
        groupeDAO.delete(groupe1);
    }

    @Test
    public void testFindByLigne() {
        Ligne ligne = new Ligne("libelle 1");

        // On crée un groupe
        ligne = ligneDAO.save(ligne);
        assertNotNull(ligne.getId());

        Arret arret = arretDAO.save(arret1);
        assertNotNull(arret.getId());

        // On lui ajoute un arret
        ligne = ligneDAO.ajouterArret(ligne, arret);
        assertEquals(1, ligne.getArrets().size());

        // On récupère les arrêts du groupe
        List<Arret> arretsLigne = ligne.getArrets();

        // On vérifie que celle-ci contient l'arret ajouté
        assertTrue(arretsLigne.contains(arret));

        arretDAO.delete(arret);
        ligneDAO.delete(ligne);
    }

    @Test
    public void testCursorToObjectList() {
        // On enregistre des arrets
        Arret arret1Sauv = arretDAO.save(arret1);
        Arret arret2Sauv = arretDAO.save(arret2);
        Arret arret3Sauv = arretDAO.save(arret3);

        List<Arret> arretsExpected = new ArrayList<>();
        arretsExpected.add(arret1Sauv);
        arretsExpected.add(arret2Sauv);
        arretsExpected.add(arret3Sauv);

        List<Arret> arretList = arretDAO.cursorToObjectList(arretDAO.cursorFindAll());

        assertEquals(arretsExpected, arretList);

        int result = arretDAO.deleteAll(arretList);
        assertEquals(arretList.size(), result);
    }

    @Test
    public void testObjectToContentValues() {
        ContentValues enregistrement = arretDAO.objectToContentValues(arret1);

        assertTrue(enregistrement.containsKey(BDHelper.ARRET_CLE));
        assertTrue(enregistrement.containsKey(BDHelper.ARRET_LIBELLE));
        assertTrue(enregistrement.containsKey(BDHelper.ARRET_POSITION));

        assertEquals(arret1.getId(), enregistrement.get(BDHelper.ARRET_CLE));
        assertEquals(arret1.getLibelle(), enregistrement.get(BDHelper.ARRET_LIBELLE));
        assertEquals(arret1.getPosition(), enregistrement.get(BDHelper.ARRET_POSITION));
    }
}