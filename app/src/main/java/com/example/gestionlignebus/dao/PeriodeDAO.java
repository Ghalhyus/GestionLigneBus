package com.example.gestionlignebus.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestionlignebus.model.Periode;

import java.util.ArrayList;
import java.util.List;

public class PeriodeDAO implements ICommonDAO<Periode, Long> {
    ///////////////////////////////////////////////////
    /////////////////// Attributs /////////////////////
    ///////////////////////////////////////////////////
    private BDHelper bdHelper;
    private SQLiteDatabase sqLiteDatabase;

    public static final int COLONNE_NUM_CLE = 0;
    public static final int COLONNE_NUM_LIBELLE = 1;
    
    private static final String SELECT_ETOILE = "SELECT * FROM "
            + BDHelper.PERIODE_NOM_TABLE;

    //////////////////////////////////////////////////////////////////
    ///////////////////////// Requêtes ///////////////////////////////
    //////////////////////////////////////////////////////////////////
    private static final String FIND_ALL = SELECT_ETOILE;

    ///////////////////////////////////////////////////
    /////////////////// Prepared //////////////////////
    ///////////////////////////////////////////////////
    private static final String FIND_BY_ID = SELECT_ETOILE + WHERE + BDHelper.LIGNE_CLE
            + PARAMETRE;

    private static final String FIND_BY_LIBELLE = SELECT_ETOILE 
            + WHERE + BDHelper.PERIODE_LIBELLE + PARAMETRE;

    public PeriodeDAO(Context context) {
        bdHelper = new BDHelper(context, BDHelper.NOM_BD, null, BDHelper.VERSION);
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
    public Periode findById(Long aLong) {
        Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_ID,
                new String[] {String.valueOf(aLong)});
        return cursorToObject(cursor);
    }

    public Periode findByLibelle(String aLong) {
        Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_LIBELLE,
                new String[] {String.valueOf(aLong)});
        return cursorToObject(cursor);
    }

    @Override
    public List<Periode> findAll() {
        return cursorToObjectList(cursorFindAll());
    }

    @Override
    public Periode save(Periode toSave) {
        ContentValues enregistrement = objectToContentValues(toSave);
        long id = sqLiteDatabase.insert(BDHelper.PERIODE_NOM_TABLE, null, enregistrement);
        return findById(id);
    }

    @Override
    public List<Periode> saveAll(List<Periode> toSave) {
        List<Periode> retour = new ArrayList<>();
        for (Periode periode : toSave) {
            retour.add(save(periode));
        }
        return retour;
    }

    @Override
    public int delete(Periode toDelete) {
        return sqLiteDatabase.delete(
                BDHelper.PERIODE_NOM_TABLE,
                BDHelper.PERIODE_CLE + PARAMETRE,
                new String[] { String.valueOf(toDelete.getId()) });
    }

    @Override
    public int deleteAll(List<Periode> toDelete) {
        int nbDelete = 0;
        for (Periode periode : toDelete) {
            nbDelete += delete(periode);
        }
        return nbDelete;
    }

    @Override
    public Periode update(Periode toUpdate) {
        ContentValues enregistrement = objectToContentValues(toUpdate);
        sqLiteDatabase.update(BDHelper.PERIODE_NOM_TABLE,
                enregistrement,
                BDHelper.PERIODE_CLE + " = ?",
                new String[] {String.valueOf(toUpdate.getId())});
        return findById(toUpdate.getId());
    }

    @Override
    public Cursor cursorFindAll() {
        return sqLiteDatabase.rawQuery(FIND_ALL, null);
    }

    @Override
    public Periode cursorToObject(Cursor cursor) {
        Periode result;

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        // Si le curseur ne contient qu'un seul enregistrement, on l'initialise à cet enregistrement,
        // S'il en contient plus d'un, cela crée une boucle infinie avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
        }

        result = new Periode();
        result.setId(cursor.getLong(COLONNE_NUM_CLE));
        result.setLibelle(cursor.getString(COLONNE_NUM_LIBELLE));

        // On ferme le curseur s'il ne contient qu'un seul enregistrement,
        // Sinon on ne peut pas accéder aux autres avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.close();
        }

        return result;
    }

    @Override
    public List<Periode> cursorToObjectList(Cursor cursor) {
        List<Periode> lignes = new ArrayList<>();
        for ( cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            Periode periode = cursorToObject(cursor);
            lignes.add(periode);
        }
        // On ferme le curseur après la conversion
        cursor.close();
        return lignes;
    }

    @Override
    public ContentValues objectToContentValues(Periode toConvert) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(BDHelper.PERIODE_CLE, toConvert.getId());
        enregistrement.put(BDHelper.PERIODE_LIBELLE, toConvert.getLibelle());

        return enregistrement;
    }
}
