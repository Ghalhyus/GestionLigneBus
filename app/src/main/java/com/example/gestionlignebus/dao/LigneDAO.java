package com.example.gestionlignebus.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Ligne;

import java.util.ArrayList;
import java.util.List;

public class LigneDAO implements ICommonDAO<Ligne, Long> {
    ///////////////////////////////////////////////////
    /////////////////// Attributs /////////////////////
    ///////////////////////////////////////////////////
    private final BDHelper bdHelper;
    private SQLiteDatabase sqLiteDatabase;
    private final ArretDAO arretDAO;

    public static final int COLONNE_NUM_CLE = 0;
    public static final int COLONNE_NUM_LIBELLE = 1;
    public static final int COLONNE_NUM_ARRET_ALLE = 2;
    public static final int COLONNE_NUM_ARRET_RETOUR = 3;

    //////////////////////////////////////////////////////////////////
    ///////////////////////// Requêtes ///////////////////////////////
    //////////////////////////////////////////////////////////////////
    private static final String FIND_ALL = "SELECT * FROM "
            + BDHelper.LIGNE_NOM_TABLE;

    ///////////////////////////////////////////////////
    /////////////////// Prepared //////////////////////
    ///////////////////////////////////////////////////
    private static final String FIND_BY_ID = "SELECT * FROM "
            + BDHelper.LIGNE_NOM_TABLE + " WHERE " + BDHelper.LIGNE_CLE
            + " = ? ";

    public LigneDAO(Context context) {
        arretDAO = new ArretDAO(context);

        bdHelper = new BDHelper(context, BDHelper.NOM_BD, null, BDHelper.VERSION);
    }

    @Override
    public void open() {
        sqLiteDatabase = bdHelper.getWritableDatabase();
        arretDAO.open();
    }

    @Override
    public void close() {
        sqLiteDatabase.close();
        bdHelper.close();
    }

    @Override
    public Ligne findById(Long aLong) {
        return cursorToObject(cursorFindById(aLong));
    }

    @Override
    public List<Ligne> findAll() {
        return cursorToObjectList(cursorFindAll());
    }

    @Override
    public Ligne save(Ligne toSave) {
        // On ajoute l'arrrêt de départ
        if (toSave.getArretDepart().getId() == null) {
            Arret arretDepartFound = arretDAO.findByLibelle(toSave.getArretDepart().getLibelle());
            if (arretDepartFound != null) {
                toSave.setArretDepart(arretDepartFound);
            } else {
                toSave.setArretDepart(arretDAO.save(toSave.getArretDepart()));
            }
        }
        // On ajoute l'arrrêt de retour
        if (toSave.getArretRetour().getId() == null) {
            Arret arretRetourFound = arretDAO.findByLibelle(toSave.getArretRetour().getLibelle());
            if (arretRetourFound != null) {
                toSave.setArretRetour(arretRetourFound);
            } else {
                toSave.setArretRetour(arretDAO.save(toSave.getArretRetour()));
            }
        }
        // On ajoute les arrêts à la table de jointures
        if (toSave.getArrets() != null) {
            for (Arret arret : toSave.getArrets()) {
                // S'il n'a pas d'identifiant
                if (arret.getId() == null) {
                    // On vérifie qu'il n'y ait pas d'arrêt homonyme dans la base
                    Arret arretFound = arretDAO.findByLibelle(arret.getLibelle());
                    if (arretFound != null) {
                        // On remplace l'arret de la liste par l'arrêt trouvé
                        toSave.getArrets().set(toSave.getArrets().indexOf(arret), arretFound);
                    } else {
                        // Sinon on enregistre l'arrêt
                        Arret nouvelArret  = arretDAO.save(arret);
                        toSave.getArrets().set(toSave.getArrets().indexOf(arret), nouvelArret);
                    }
                }
            }
        }
        ContentValues enregistrement = objectToContentValues(toSave);
        long id = sqLiteDatabase.insert(BDHelper.LIGNE_NOM_TABLE, null, enregistrement);
        if (id > 0 && toSave.getArrets() != null) {
            toSave.setId(id);
            for (Arret arret : toSave.getArrets()) {
                ajouterArret(toSave, arret);
            }
        }

        return findById(id);
    }

    @Override
    public List<Ligne> saveAll(List<Ligne> toSave) {
        List<Ligne> retour = new ArrayList<>();
        for (Ligne ligne : toSave) {
            retour.add(save(ligne));
        }
        return retour;
    }

    @Override
    public int delete(Ligne toDelete) {
        return sqLiteDatabase.delete(
                BDHelper.LIGNE_NOM_TABLE,
                BDHelper.LIGNE_CLE + " = ? ",
                new String[] { String.valueOf(toDelete.getId()) });
    }

    @Override
    public int deleteAll(List<Ligne> toDelete) {
        if (toDelete != null) {
            int nbDelete = 0;
            for (Ligne ligne : toDelete) {
                nbDelete += delete(ligne);
            }
            return nbDelete;
        } else {
            return -1;
        }

    }

    @Override
    public Ligne update(Ligne toUpdate) {
        ContentValues enregistrement = objectToContentValues(toUpdate);
        sqLiteDatabase.update(BDHelper.LIGNE_NOM_TABLE,
                enregistrement,
                BDHelper.LIGNE_CLE + " = ?",
                new String[] {String.valueOf(toUpdate.getId())});
        return findById(toUpdate.getId());
    }

    /**
     * Retourne un curseur sur la ligne trouvée.
     * @param id Id du groupe à trouver.
     * @return Un curseur sur la ligne trouvée.
     */
    public Cursor cursorFindById(Long id) {
        return sqLiteDatabase.rawQuery(FIND_BY_ID,
                new String[] {String.valueOf(id)});
    }

    @Override
    public Cursor cursorFindAll() {
        return sqLiteDatabase.rawQuery(FIND_ALL, null);
    }

    public Ligne ajouterArret(Ligne ligne, Arret arret) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(BDHelper.LIGNE_ARRET_FK_LIGNE, ligne.getId());
        enregistrement.put(BDHelper.LIGNE_ARRET_FK_ARRET, arret.getId());

        sqLiteDatabase.insert(BDHelper.LIGNE_ARRET_NOM_TABLE, null, enregistrement);

        return findById(ligne.getId());
    }

    @Override
    public Ligne cursorToObject(Cursor cursor) {
        Ligne result;

        arretDAO.open();

        // Retourne null si aucune ligne de trouvée
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        // Si le curseur ne contient qu'un seul enregistrement, on l'initialise à cet enregistrement,
        // S'il en contient plus d'un, cela crée une boucle infinie avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
        }

        result = new Ligne();
        result.setId(cursor.getLong(COLONNE_NUM_CLE));
        result.setLibelle(cursor.getString(COLONNE_NUM_LIBELLE));
        result.setArretDepart(arretDAO.findById(cursor.getLong(COLONNE_NUM_ARRET_ALLE)));
        result.setArretRetour(arretDAO.findById(cursor.getLong(COLONNE_NUM_ARRET_RETOUR)));
        // Problème du n+1 select
        List<Arret> arrets = arretDAO.findByLigne(result);
        result.setArrets(arrets);

        // On ferme le curseur s'il ne contient qu'un seul enregistrement,
        // Sinon on ne peut pas accéder aux autres avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.close();
        }

        return result;
    }

    @Override
    public List<Ligne> cursorToObjectList(Cursor cursor) {
        List<Ligne> lignes = new ArrayList<>();

        // Retourne une liste vide si aucune ligne de trouvée
        if (cursor.getCount() == 0) {
            cursor.close();
            return lignes;
        }
        for ( cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            Ligne ligne = cursorToObject(cursor);
            lignes.add(ligne);
        }
        // On ferme le curseur après la conversion
        cursor.close();
        return lignes;
    }

    @Override
    public ContentValues objectToContentValues(Ligne toConvert) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(BDHelper.LIGNE_CLE, toConvert.getId());
        enregistrement.put(BDHelper.LIGNE_LIBELLE, toConvert.getLibelle());

        if (toConvert.getArretDepart() != null
            && toConvert.getArretDepart().getId() != null) {
            enregistrement.put(BDHelper.LIGNE_FK_ARRET_ALLE, toConvert.getArretDepart().getId());
        } else {
            enregistrement.putNull(BDHelper.LIGNE_FK_ARRET_ALLE);
        }

        if (toConvert.getArretRetour() != null
                && toConvert.getArretRetour().getId() != null) {
            enregistrement.put(BDHelper.LIGNE_FK_ARRET_RETOUR, toConvert.getArretRetour().getId());
        } else {
            enregistrement.putNull(BDHelper.LIGNE_FK_ARRET_RETOUR);
        }
        return enregistrement;
    }

    public void clear() {
        sqLiteDatabase.delete(BDHelper.LIGNE_NOM_TABLE, null, null);
    }
}
