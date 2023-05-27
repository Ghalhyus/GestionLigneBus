package com.example.gestionlignebus.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;
import com.example.gestionlignebus.model.Ligne;

import java.util.ArrayList;
import java.util.List;

/**
 * Dao de la classe arret, cette classe gère toute les transactions
 * des objets arrêts
 */
public class ArretDAO implements ICommonDAO<Arret, Long> {
    ///////////////////////////////////////////////////
    /////////////////// Attributs /////////////////////
    ///////////////////////////////////////////////////
    private final BDHelper bdHelper;
    private SQLiteDatabase sqLiteDatabase;

    public static final int COLONNE_NUM_CLE = 0;
    public static final int COLONNE_NUM_LIBELLE = 1;
    public static final int COLONNE_NUM_POSITION = 2;

    private static final String SELECT_ETOILE = "SELECT * FROM " + BDHelper.ARRET_NOM_TABLE;

    //////////////////////////////////////////////////////////////////
    ///////////////////////// Requêtes ///////////////////////////////
    //////////////////////////////////////////////////////////////////
    private static final String FIND_ALL = SELECT_ETOILE;

    ///////////////////////////////////////////////////
    /////////////////// Prepared //////////////////////
    ///////////////////////////////////////////////////
    private static final String FIND_BY_ID = SELECT_ETOILE + WHERE + BDHelper.ARRET_CLE
            + PARAMETRE;
    private static final String FIND_BY_LIBELLE = SELECT_ETOILE + WHERE + BDHelper.ARRET_LIBELLE
            + PARAMETRE;
    private static final String FIND_BY_GROUPE = SELECT_ETOILE
            + " JOIN " + BDHelper.GROUPE_ARRET_NOM_TABLE
            + " ON " + BDHelper.GROUPE_ARRET_FK_ARRET + " = " + BDHelper.ARRET_CLE
            + WHERE + BDHelper.GROUPE_ARRET_FK_GROUPE + PARAMETRE;

    private static final String FIND_BY_LIGNE = SELECT_ETOILE
            + " JOIN " + BDHelper.LIGNE_ARRET_NOM_TABLE
            + " ON " + BDHelper.LIGNE_ARRET_FK_ARRET + " = " + BDHelper.ARRET_CLE
            + WHERE + BDHelper.LIGNE_ARRET_FK_LIGNE + PARAMETRE;
    public ArretDAO(Context context) {
        bdHelper = new BDHelper(
                context,
                BDHelper.NOM_BD,
                null,
                BDHelper.VERSION
                );
    }


    @Override
    public void open() {
        sqLiteDatabase = bdHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        sqLiteDatabase.close();
        bdHelper.close();
    }

    @Override
    public Arret findById(Long aLong) {
        Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_ID,
                new String[] {String.valueOf(aLong)});
        return cursorToObject(cursor);
    }

    @Override
    public List<Arret> findAll() {
        return cursorToObjectList(cursorFindAll());
    }

    @Override
    public Arret save(Arret toSave) {
        ContentValues enregistrement = objectToContentValues(toSave);
        long id = sqLiteDatabase.insert(BDHelper.ARRET_NOM_TABLE, null, enregistrement);
        return findById(id);
    }

    @Override
    public List<Arret> saveAll(List<Arret> toSave) {
        List<Arret> retour = new ArrayList<>();
        for (Arret arret : toSave) {
            retour.add(save(arret));
        }
        return retour;
    }

    @Override
    public int delete(Arret toDelete) {
        // On supprime toute les lignes dans la ou les table(s) de jointure(s)
        sqLiteDatabase.delete(
                BDHelper.GROUPE_ARRET_NOM_TABLE,
                BDHelper.GROUPE_ARRET_FK_ARRET + PARAMETRE,
                new String[] { String.valueOf(toDelete.getId()) });
        sqLiteDatabase.delete(
                BDHelper.LIGNE_ARRET_NOM_TABLE,
                BDHelper.LIGNE_ARRET_FK_ARRET + PARAMETRE,
                new String[] { String.valueOf(toDelete.getId()) });
        return sqLiteDatabase.delete(
                BDHelper.ARRET_NOM_TABLE,
                BDHelper.ARRET_CLE + PARAMETRE,
                new String[] { String.valueOf(toDelete.getId()) });
    }

    @Override
    public int deleteAll(List<Arret> toDelete) {
        int nbSupprime = 0;
        for (Arret arret : toDelete) {
            nbSupprime += delete(arret);
        }
        return nbSupprime;
    }

    @Override
    public Arret update(Arret toUpdate) {
        // On créer un nouvel enregistrement
        ContentValues enregistrement = objectToContentValues(toUpdate);

        // On modifie l'enregistrement
        sqLiteDatabase.update(
                BDHelper.ARRET_NOM_TABLE,
                enregistrement,
                BDHelper.ARRET_CLE + " = ?",
                new String[] { String.valueOf(toUpdate.getId()) }
        );

        // On renvoie la dépense une fois modifiée
        return findById(toUpdate.getId());
    }

    @Override
    public Cursor cursorFindAll() {
        return sqLiteDatabase.rawQuery(FIND_ALL, null);
    }

    public List<Arret> findByGroupe(Groupe groupe) {
        Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_GROUPE,
                new String[] {String.valueOf(groupe.getId())});
        return cursorToObjectList(cursor);
    }

    public List<Arret> findByLigne(Ligne ligne) {
        Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_LIGNE, new String[] {String.valueOf(ligne.getId())});
        return cursorToObjectList(cursor);
    }

    public Arret findByLibelle(String libelle) {
        Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_LIBELLE, new String[]{libelle});
        return cursorToObject(cursor);
    }

    @Override
    public Arret cursorToObject(Cursor cursor) {
        Arret result;

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        // Si le curseur ne contient qu'un seul enregistrement, on l'initialise à cet enregistrement,
        // S'il en contient plus d'un, cela crée une boucle infinie avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
        }

        result = new Arret();
        result.setId(cursor.getLong(COLONNE_NUM_CLE));
        result.setLibelle(cursor.getString(COLONNE_NUM_LIBELLE));
        result.setPosition(cursor.getString(COLONNE_NUM_POSITION));

        // On ferme le curseur s'il ne contient qu'un seul enregistrement,
        // Sinon on ne peut pas accéder aux autres avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.close();
        }

        return result;
    }

    @Override
    public List<Arret> cursorToObjectList(Cursor cursor) {
        List<Arret> arrets = new ArrayList<>();
        for ( cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            Arret arret = cursorToObject(cursor);
            arrets.add(arret);
        }
        // On ferme le curseur après la conversion
        cursor.close();
        return arrets;
    }

    @Override
    public ContentValues objectToContentValues(Arret toConvert) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(BDHelper.ARRET_CLE, toConvert.getId());
        enregistrement.put(BDHelper.ARRET_LIBELLE, toConvert.getLibelle());
        enregistrement.put(BDHelper.ARRET_POSITION, toConvert.getPosition());

        return enregistrement;
    }

    public void clear() {
        sqLiteDatabase.delete(BDHelper.ARRET_NOM_TABLE, null, null);
    }
}