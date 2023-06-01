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
    private final BDHelper bdHelper;
    private SQLiteDatabase sqLiteDatabase;
    private final ArretDAO arretDAO;

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

    private static final String FIND_BY_ARRET_AND_HORAIRE = SELECT_ETOILE
            + WHERE + BDHelper.PASSAGE_ARRET
            + PARAMETRE + " AND "
            + BDHelper.PASSAGE_HORAIRE + PARAMETRE;


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

    public List<Passage> findByArretAndHoraireInferieur(Arret arret, String horaire) {
        return cursorToObjectList(sqLiteDatabase.rawQuery(
                FIND_BY_ARRET_AND_HORAIRE, new String[] { arret.getId().toString(), horaire }));
    }

    @Override
    public List<Passage> findAll() {
        return cursorToObjectList(cursorFindAll());
    }

    @Override
    public Passage save(Passage toSave) {
        if (toSave != null) {
            if (toSave.getArret() != null && toSave.getHoraire() != null) {
                if (toSave.horairesSuivantsCroissants()) {
                    if (findByArretAndHoraire(toSave.getArret(), toSave.getHoraire()) == null) {
                        if (toSave.getArret().getId() == null) {
                            if (toSave.getArret().getLibelle() != null) {
                                // Si l'arrêt n'a pas d'id mais un libelle alors on cherche un homonyme
                                Arret arretFound = arretDAO.findByLibelle(toSave.getArret().getLibelle());
                                if (arretFound == null ) {
                                    // Aucun homonyme trouvé
                                    return null;
                                }
                                toSave.setArret(arretFound);
                            } else {
                                // Arret initialisé mais vide
                                return null;
                            }
                        }
                        ContentValues enregistrement = objectToContentValues(toSave);
                        long id = sqLiteDatabase.insert(
                                BDHelper.PASSAGE_NOM_TABLE, null, enregistrement);

                        // On enregistre le passage suivant
                        if (toSave.getPassageSuivant() != null && toSave.getPassageSuivant().getId() == null) {
                            Passage passageSuivant = save(toSave.getPassageSuivant());

                            // On met à jour l'arrêt à enregistrer
                            toSave = findById(id);
                            toSave.setPassageSuivant(passageSuivant);
                            update(toSave);
                        }
                        return findById(id);
                    } else {
                        // Un arrêt existe déjà pour cet arrêt et cet horaire
                        return null;
                    }
                } else {
                    // Les horaires ne sont pas dans l'ordre croissant
                    return null;
                }
            } else {
                // Arret ou Horaire du passage null
                return null;
            }
        } else {
            // Argument invalide
            return null;
        }



//        if (findByArretAndHoraire(toSave.getArret(), toSave.getHoraire()) == null) {
//            if (toSave.getArret() != null && toSave.getArret().getId() == null) {
//                Arret arretFound = arretDAO.findByLibelle(toSave.getArret().getLibelle());
//                if (arretFound != null ) {
//                    toSave.setArret(arretFound);
//                } else {
//                    toSave.setArret(arretDAO.save(toSave.getArret()));
//                }
//            }
//            ContentValues enregistrement = objectToContentValues(toSave);
//            long id = sqLiteDatabase.insert(
//                    BDHelper.PASSAGE_NOM_TABLE, null, enregistrement);
//
//            // On enregistre le passage suivant
//            if (toSave.getPassageSuivant() != null && toSave.getPassageSuivant().getId() == null) {
//                Passage passageSuivant = save(toSave.getPassageSuivant());
//
//                // On met à jour l'arrêt à enregistrer
//                toSave = findById(id);
//                toSave.setPassageSuivant(passageSuivant);
//                update(toSave);
//            }
//
//            return findById(id);
//        } else {
//            // Un passage identique existe déjà
//            return null;
//        }
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
        if (toDelete != null && toDelete.getId() != null) {
            // On supprime les passages chaîné
            if (toDelete.getPassageSuivant() != null) {
                delete(toDelete.getPassageSuivant());
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

        if (toConvert.getArret() != null && toConvert.getArret().getId() != null) {
            enregistrement.put(BDHelper.PASSAGE_ARRET, toConvert.getArret().getId());
        } else {
            enregistrement.putNull(BDHelper.PASSAGE_ARRET);
        }

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

    public List<Passage> findByArret(Arret arret) {
        if (arret.getId() != null) {
            Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_ARRET, new String[] { arret.getId().toString() });
            return cursorToObjectList(cursor);
        } else {
            return new ArrayList<>();
        }
    }

    public Passage findByArretAndHoraire(Arret arret, LocalTime horaire) {
        // On vérifie que les arguments ne sont pas nuls
        if (arret != null && horaire != null) {
            // Si l'arrrêt n'a pas d'id on cherche un homonyme
            if (arret.getId() == null && arret.getLibelle() != null) {
                arret = arretDAO.findByLibelle(arret.getLibelle());
            }
            // On vérifie qu'il existe bien un homonyme
            if (arret != null) {
                Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_ARRET_AND_HORAIRE, new String[] {
                        arret.getId().toString(),
                        horaire.toString()
                });
                return cursorToObject(cursor);
            } else {
                // Aucun homonyme trouvé
                return null;
            }
        } else {
            // Les arguments sont null
            return null;
        }
    }

    public void clear() {
        sqLiteDatabase.delete(BDHelper.PASSAGE_NOM_TABLE, null, null);
    }

}
