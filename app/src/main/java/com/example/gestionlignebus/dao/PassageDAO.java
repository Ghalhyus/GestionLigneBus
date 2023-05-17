package com.example.gestionlignebus.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Passage;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class PassageDAO implements ICommonDAO<Passage, Long> {
    ///////////////////////////////////////////////////
    /////////////////// Attributs /////////////////////
    ///////////////////////////////////////////////////
    private BDHelper bdHelper;
    private SQLiteDatabase sqLiteDatabase;
    private ArretDAO arretDAO;

    public static final int COLONNE_NUM_CLE = 0;
    public static final int COLONNE_NUM_ARRET = 1;
    public static final int COLONNE_NUM_HORAIRE = 2;
    public static final int COLONNE_NUM_PASSAGE_SUIVANT = 3;

    private static final String SELECT_ETOILE = "SELECT * FROM " + BDHelper.PASSAGE_NOM_TABLE;

    //////////////////////////////////////////////////////////////////
    ///////////////////////// Requêtes ///////////////////////////////
    //////////////////////////////////////////////////////////////////
    private static final String FIND_ALL = SELECT_ETOILE;

    ///////////////////////////////////////////////////
    /////////////////// Prepared //////////////////////
    ///////////////////////////////////////////////////
    private static final String FIND_BY_ID = SELECT_ETOILE + WHERE + BDHelper.PASSAGE_CLE
            + PARAMETRE;
    private static final String FIND_BY_PASSAGE_SUIVANT = SELECT_ETOILE
            + WHERE + BDHelper.PASSAGE_PASSAGE_SUIVANT
            + PARAMETRE;

    private static final String FIND_BY_ARRET = SELECT_ETOILE + WHERE + BDHelper.PASSAGE_ARRET
            + PARAMETRE;


    public PassageDAO(Context context) {
        bdHelper = new BDHelper(context, BDHelper.NOM_BD, null, BDHelper.VERSION);
        arretDAO = new ArretDAO(context);
    }
    @Override
    public void open() {
        arretDAO.open();
        sqLiteDatabase = bdHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        arretDAO.close();
        sqLiteDatabase.close();
        bdHelper.close();
    }

    @Override
    public Passage findById(Long aLong) {
        return cursorToObject(sqLiteDatabase.rawQuery(FIND_BY_ID, new String[] {aLong.toString()}));
    }

    @Override
    public List<Passage> findAll() {
        return cursorToObjectList(cursorFindAll());
    }

    @Override
    public Passage save(Passage toSave) {
        ContentValues enregistrement = objectToContentValues(toSave);
        long id = sqLiteDatabase.insert(BDHelper.PASSAGE_NOM_TABLE, null, enregistrement);
        return findById(id);
    }

    @Override
    public List<Passage> saveAll(List<Passage> toSave) {
        List<Passage> retour = new ArrayList<>();
        for (Passage passage : toSave) {
            retour.add(save(passage));
        }
        return retour;
    }

    @Override
    public int delete(Passage toDelete) {
        if (toDelete.getId() != null) {
            // On met passe à null le passage suivant de tous les passages ayant le passage supprimé
            // comme passage suivant
            List<Passage> passages = findByPassageSuivant(toDelete);
            if (passages != null) {
                for (Passage passage : passages) {
                    passage.setPassageSuivant(null);
                    update(passage);
                }
            }

            return sqLiteDatabase.delete(
                    BDHelper.PASSAGE_NOM_TABLE,
                    BDHelper.PASSAGE_CLE + PARAMETRE,
                    new String[] { String.valueOf(toDelete.getId()) });
        } else {
            return -1;
        }
    }

    @Override
    public int deleteAll(List<Passage> toDelete) {
        int result = 0;
        for (Passage passage : toDelete) {
            result += delete(passage);
        }
        return result;
    }

    @Override
    public Passage update(Passage toUpdate) {
        // On créer un nouvel enregistrement
        ContentValues enregistrement = objectToContentValues(toUpdate);

        // On modifie l'enregistrement
        sqLiteDatabase.update(
                BDHelper.PASSAGE_NOM_TABLE,
                enregistrement,
                BDHelper.PASSAGE_CLE + " = ?",
                new String[] { String.valueOf(toUpdate.getId()) }
        );

        // On renvoie la dépense une fois modifiée
        return findById(toUpdate.getId());
    }

    @Override
    public Cursor cursorFindAll() {
        return sqLiteDatabase.rawQuery(FIND_ALL, null);
    }

    @Override
    public Passage cursorToObject(Cursor cursor) {
        Passage result;

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        // Si le curseur ne contient qu'un seul enregistrement, on l'initialise à cet enregistrement,
        // S'il en contient plus d'un, cela crée une boucle infinie avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
        }

        result = new Passage();
        // On récupère l'ID
        result.setId(cursor.getLong(COLONNE_NUM_CLE));

        // On récupère l'arrêt
        Arret arret = arretDAO.findById(cursor.getLong(COLONNE_NUM_ARRET));
        result.setArret(arret);

        // On récupère l'horaire
        String horaireString = cursor.getString(COLONNE_NUM_HORAIRE);
        LocalTime horaire = LocalTime.parse(horaireString);
        result.setHoraire(horaire);

        result.setPassageSuivant(findById(cursor.getLong(COLONNE_NUM_PASSAGE_SUIVANT)));

        // On ferme le curseur s'il ne contient qu'un seul enregistrement,
        // Sinon on ne peut pas accéder aux autres avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.close();
        }

        return result;
    }

    @Override
    public List<Passage> cursorToObjectList(Cursor cursor) {
        List<Passage> passages = new ArrayList<>();
        for ( cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            Passage passage = cursorToObject(cursor);
            passages.add(passage);
        }
        // On ferme le curseur après la conversion
        cursor.close();
        return passages;
    }

    @Override
    public ContentValues objectToContentValues(Passage toConvert) {
        ContentValues enregistrement = new ContentValues();
        enregistrement.put(BDHelper.PASSAGE_CLE, toConvert.getId());
        enregistrement.put(BDHelper.PASSAGE_ARRET, toConvert.getArret().getId());
        enregistrement.put(BDHelper.PASSAGE_HORAIRE, toConvert.getHoraire().toString());
        if (toConvert.getPassageSuivant() != null && toConvert.getPassageSuivant().getId() != null) {
            enregistrement.put(BDHelper.PASSAGE_PASSAGE_SUIVANT, toConvert.getPassageSuivant().getId());
        } else {
            enregistrement.putNull(BDHelper.PASSAGE_PASSAGE_SUIVANT);
        }

        return enregistrement;
    }

    public List<Passage> findByPassageSuivant(Passage passage) {
        if (passage.getId() != null) {
            Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_PASSAGE_SUIVANT,
                    new String[] { passage.getId().toString() });
            return cursorToObjectList(cursor);
        } else {
            return new ArrayList<>();
        }
    }

    public Passage findByArret(Arret arret) {
        if (arret.getId() != null) {
            Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_ARRET, new String[] { arret.getId().toString() });
            return cursorToObject(cursor);
        } else {
            return null;
        }
    }
}
