package com.example.gestionlignebus.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;

import java.util.ArrayList;
import java.util.List;

public class GroupeDAO implements ICommonDAO<Groupe, Long> {
    ///////////////////////////////////////////////////
    /////////////////// Attributs /////////////////////
    ///////////////////////////////////////////////////
    private BDHelper bdHelper;
    private SQLiteDatabase sqLiteDatabase;
    private ArretDAO arretDAO;

    public static final int COLONNE_NUM_CLE = 0;
    public static final int COLONNE_NUM_LIBELLE = 1;

    //////////////////////////////////////////////////////////////////
    ///////////////////////// Requêtes ///////////////////////////////
    //////////////////////////////////////////////////////////////////
    private static final String FIND_ALL = "SELECT * FROM "
            + BDHelper.GROUPE_NOM_TABLE;

    ///////////////////////////////////////////////////
    /////////////////// Prepared //////////////////////
    ///////////////////////////////////////////////////
    private static final String FIND_BY_ID = "SELECT * FROM "
            + BDHelper.GROUPE_NOM_TABLE + " WHERE " + BDHelper.GROUPE_CLE + PARAMETRE;

    private static final String FIND_BY_LIBELLE = "SELECT * FROM "
            + BDHelper.GROUPE_NOM_TABLE + " WHERE " + BDHelper.GROUPE_LIBELLE + PARAMETRE;


    public GroupeDAO(Context context) {
        bdHelper = new BDHelper(context, BDHelper.NOM_BD, null,BDHelper.VERSION);
        arretDAO =new ArretDAO(context);
        arretDAO.open();
    }

    @Override
    public void open() {
        sqLiteDatabase = bdHelper.getWritableDatabase();
    }

    @Override
    public void close() {
        sqLiteDatabase.close();
        bdHelper.close();
        arretDAO.close();
    }

    @Override
    public Groupe findById(Long aLong) {
        return cursorToObject(cursorFindById(aLong));
    }

    public Groupe findByLibelle(String libelle) {
        return cursorToObject(sqLiteDatabase.rawQuery(FIND_BY_LIBELLE, new String[] {libelle}));
    }

    @Override
    public List<Groupe> findAll() {
        return cursorToObjectList(cursorFindAll());
    }

    @Override
    public Groupe save(Groupe toSave) {
        if (findByLibelle(toSave.getLibelle()) == null) {
            ContentValues enregistrement = objectToContentValues(toSave);
            long id = sqLiteDatabase.insert(BDHelper.GROUPE_NOM_TABLE, null,
                    enregistrement);
            return findById(id);
        } else {
            // Un groupe existe déjà avec ce libelle
            return null;
        }

    }

    @Override
    public List<Groupe> saveAll(List<Groupe> toSave) {
        List<Groupe> retour = new ArrayList<>();
        for (Groupe groupe : toSave) {
            retour.add(save(groupe));
        }
        return retour;
    }

    @Override
    public int delete(Groupe toDelete) {
        // On supprime toute les lignes dans la ou les table(s) de jointure(s)
        sqLiteDatabase.delete(
                BDHelper.GROUPE_ARRET_NOM_TABLE,
                BDHelper.GROUPE_ARRET_FK_GROUPE + PARAMETRE,
                new String[] { String.valueOf(toDelete.getId()) });
        return sqLiteDatabase.delete(
                BDHelper.GROUPE_NOM_TABLE,
                BDHelper.GROUPE_CLE + PARAMETRE,
                new String[] { String.valueOf(toDelete.getId()) });
    }

    @Override
    public int deleteAll(List<Groupe> toDelete) {
        int nbDelete = 0;
        for (Groupe groupe : toDelete) {
            nbDelete += delete(groupe);
        }
        return nbDelete;
    }

    @Override
    public Groupe update(Groupe toUpdate) {
        ContentValues enregistrement = objectToContentValues(toUpdate);
        sqLiteDatabase.update(BDHelper.GROUPE_NOM_TABLE,
                enregistrement,
                BDHelper.GROUPE_CLE + " = ?",
                new String[] {String.valueOf(toUpdate.getId())});
        return findById(toUpdate.getId());
    }

    /**
     * Retourne un curseur sur le groupe trouvé.
     * @param id Id du groupe à trouver.
     * @return Un curseur sur le groupe trouvé.
     */
    public Cursor cursorFindById(Long id) {
        return sqLiteDatabase.rawQuery(FIND_BY_ID,
                new String[] {String.valueOf(id)});
    }

    @Override
    public Cursor cursorFindAll() {

        return sqLiteDatabase.rawQuery(FIND_ALL, null);
    }

    public Groupe ajouterArret(Groupe groupe, Arret arret) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(BDHelper.GROUPE_ARRET_FK_GROUPE, groupe.getId());
        enregistrement.put(BDHelper.GROUPE_ARRET_FK_ARRET, arret.getId());

        sqLiteDatabase.insert(BDHelper.GROUPE_ARRET_NOM_TABLE, null, enregistrement);

        return findById(groupe.getId());
    }

    public Groupe retirerArret(Groupe groupe, Arret arret) {
        // On supprime toute les lignes dans la ou les table(s) de jointure(s)
        sqLiteDatabase.delete(
                BDHelper.GROUPE_ARRET_NOM_TABLE,
                BDHelper.GROUPE_ARRET_FK_GROUPE + PARAMETRE +
                        " AND " + BDHelper.GROUPE_ARRET_FK_ARRET + PARAMETRE,
                new String[] { String.valueOf(groupe.getId()), String.valueOf(arret.getId()) });
        return findById(groupe.getId());
    }

    @Override
    public Groupe cursorToObject(Cursor cursor) {
        Groupe result;

        // Retourne null si aucun groupe trouvé
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        // Si le curseur ne contient qu'un seul enregistrement, on l'initialise à cet enregistrement,
        // S'il en contient plus d'un, cela crée une boucle infinie avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
        }

        result = new Groupe();
        result.setId(cursor.getLong(COLONNE_NUM_CLE));
        result.setLibelle(cursor.getString(COLONNE_NUM_LIBELLE));
        // Problème du n+1 select
        List<Arret> arrets = arretDAO.findByGroupe(result);
        result.setArrets(arrets);

        // On ferme le curseur s'il ne contient qu'un seul enregistrement,
        // Sinon on ne peut pas accéder aux autres avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.close();
        }

        return result;
    }

    @Override
    public List<Groupe> cursorToObjectList(Cursor cursor) {
        List<Groupe> groupes = new ArrayList<>();

        // Retourne une liste vide si aucun groupe de trouvé
        if (cursor.getCount() == 0) {
            cursor.close();
            return groupes;
        }

        for ( cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            Groupe groupe = cursorToObject(cursor);
            groupes.add(groupe);
        }
        // On ferme le curseur après la conversion
        cursor.close();
        return groupes;
    }

    @Override
    public ContentValues objectToContentValues(Groupe toConvert) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(BDHelper.GROUPE_CLE, toConvert.getId());
        enregistrement.put(BDHelper.GROUPE_LIBELLE, toConvert.getLibelle());

        return enregistrement;
    }
}
