package com.example.gestionlignebus.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;

import java.util.ArrayList;
import java.util.List;

public class TrajetDAO implements ICommonDAO<Trajet, Long> {
    ///////////////////////////////////////////////////
    /////////////////// Attributs /////////////////////
    ///////////////////////////////////////////////////
    private final BDHelper bdHelper;
    private SQLiteDatabase sqLiteDatabase;

    private final LigneDAO ligneDAO;
    private final PeriodeDAO periodeDAO;
    private final PassageDAO passageDAO;

    public static final int COLONNE_NUM_CLE = 0;
    public static final int COLONNE_NUM_PERIODE = 1;
    public static final int COLONNE_NUM_LIGNE = 2;
    public static final int COLONNE_NUM_PREMIER_PASSAGE = 3;

    private static final String SELECT_ETOILE = ICommonDAO.SELECT_ETOILE
            + BDHelper.TRAJET_NOM_TABLE;

    //////////////////////////////////////////////////////////////////
    ///////////////////////// Requêtes ///////////////////////////////
    //////////////////////////////////////////////////////////////////
    /**
     * Requête SQL permettant de retrouver l'ensemble des trajets en base
     */
    private static final String FIND_ALL = SELECT_ETOILE;

    ///////////////////////////////////////////////////
    /////////////////// Prepared //////////////////////
    ///////////////////////////////////////////////////
    /**
     * Requête SQL permettant de retrouver des trajets par identifiant
     */
    private static final String FIND_BY_ID = SELECT_ETOILE + WHERE + BDHelper.TRAJET_CLE
            + PARAMETRE;
    /**
     * Requête SQL permettant de retrouver des trajets par arret
     */
    private static final String FIND_BY_PASSAGE = SELECT_ETOILE
            + WHERE + BDHelper.TRAJET_PREMIER_PASSAGE + PARAMETRE;

    /**
     * Requête SQL permettant de retrouver des trajets par ligne
     */
    private static final String FIND_BY_LIGNE = SELECT_ETOILE + WHERE + BDHelper.TRAJET_LIGNE
            + PARAMETRE;

    private static final String FIND_BY_PERIODE = SELECT_ETOILE + WHERE + BDHelper.TRAJET_PERIODE
            + PARAMETRE;

    /**
     * Requête SQL permettant de retrouver des trajets par periode et arret
     */
    private static final String FIND_BY_PERIODE_AND_ARRET = "SELECT * FROM "
            + BDHelper.TRAJET_NOM_TABLE
            + " JOIN " + BDHelper.PASSAGE_NOM_TABLE+ " ON "
            + BDHelper.TRAJET_PREMIER_PASSAGE
            + " = " + BDHelper.PASSAGE_NOM_TABLE + "." + BDHelper.PASSAGE_CLE
            + WHERE + BDHelper.TRAJET_PERIODE
            + PARAMETRE + " AND "
            + BDHelper.PASSAGE_ARRET + PARAMETRE;

    /**
     * Requête SQL permettant de retrouver des trajets par periode et ligne
     */
    private static final String FIND_BY_PERIODE_AND_LIGNE = "SELECT * FROM "
            + BDHelper.TRAJET_NOM_TABLE
            + WHERE + BDHelper.TRAJET_PERIODE
            + PARAMETRE + " AND "
            + BDHelper.TRAJET_LIGNE + PARAMETRE;

    /**
     * Constructeur du DAO pour les transactions liées aux trajets
     * @param context
     */
    public TrajetDAO(Context context) {
        bdHelper = new BDHelper(context,
                BDHelper.NOM_BD,
                null,
                BDHelper.VERSION);
        ligneDAO = new LigneDAO(context);
        periodeDAO = new PeriodeDAO(context);
        passageDAO = new PassageDAO(context);
    }
    @Override
    public void open() {
        sqLiteDatabase = bdHelper.getWritableDatabase();
        ligneDAO.open();
        periodeDAO.open();
        passageDAO.open();
    }

    @Override
    public void close() {
        sqLiteDatabase.close();
        bdHelper.close();
        passageDAO.close();
        ligneDAO.close();
        periodeDAO.close();
    }

    @Override
    public Trajet findById(Long aLong) {
        Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_ID, new String[]{aLong.toString()});
        return cursorToObject(cursor);
    }

    public List<Trajet> findByPeriode(Periode periode) {
        Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_PERIODE,
                new String[]{periode.getId().toString()});
        return cursorToObjectList(cursor);
    }

    /**
     * Récupère les trajets par leur premier passage
     * @param passage
     * @return
     */
    public List<Trajet> findByPassage(Passage passage) {
        if (passage.getId() != null) {
            Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_PASSAGE, new String[] { passage.getId().toString() });
            return cursorToObjectList(cursor);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Récupère les trajets correspondant à la ligne en paramètre
     * @param ligne l'élément correspondant
     * @return la liste d'éléments correspondants
     */
    public List<Trajet> findByLigne(Ligne ligne) {
        Cursor cursor = this.sqLiteDatabase.rawQuery(FIND_BY_LIGNE, new String[]{ligne.toString()});
        return cursorToObjectList(cursor);
    }

    /**
     * Récupère les trajets correspondant à l'arret et la période en paramètre
     * @param periode la periode correspondant
     * @param arret l'arret correspondant
     * @return la liste d'éléments correspondants
     */
    public List<Trajet> findByPeriodeAndArret(Periode periode,Arret arret) {
        if (periode.getId() != null) {
            Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_PERIODE_AND_ARRET,
                    new String[] { periode.getId().toString() ,arret.getId().toString()});
            return cursorToObjectList(cursor);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Récupère les trajets correspondant à la période et la ligne en paramètre
     * @param periode correspondante
     * @param ligne correspondante
     * @return la liste d'éléments
     */
    public List<Trajet> findByPeriodeAndLigne(Periode periode,Ligne ligne) {
        if (periode.getId() != null) {
            Cursor cursor = sqLiteDatabase.rawQuery(FIND_BY_PERIODE_AND_LIGNE,
                    new String[] { periode.getId().toString() ,ligne.getId().toString()});
            return cursorToObjectList(cursor);
        } else {
            return new ArrayList<>();
        }
    }
    @Override
    public List<Trajet> findAll() {
        return cursorToObjectList(cursorFindAll());
    }

    @Override
    public Trajet save(Trajet toSave) {
        if (toSave != null) {
            toSave = initPeriode(toSave);
            if (toSave != null) {
                toSave = initLigne(toSave);
                if (toSave != null) {
                    toSave = initPremierPassage(toSave);
                    if (toSave != null) {
                        ContentValues enregistrement = objectToContentValues(toSave);
                        long id = sqLiteDatabase.insert(BDHelper.TRAJET_NOM_TABLE, null, enregistrement);
                        return findById(id);
                    } else {
                        // Aucun premier passage correspondant
                        return null;
                    }
                } else {
                    // Aucune ligne trouvée ou ligne invalide
                    return null;
                }
            } else {
                // Aucune période trouvé ou période invalide
                return null;
            }
        } else {
            // Argument null
            return null;
        }




        // On vérifie que le passage ne soit pas null
//        if (toSave.getPremierPassage().getId() == null
//            && (toSave.getPremierPassage().getArret() != null
//            && toSave.getPremierPassage().getHoraire() != null)) {
//            // On ne vérifie si un passage similaire n'existe pas déjà
//            Passage passageFound = passageDAO.findByArretAndHoraire(
//                    toSave.getPremierPassage().getArret(),
//                    toSave.getPremierPassage().getHoraire());
//            if (passageFound != null) {
//                // Si oui, on l'utilise
//                toSave.setPremierPassage(passageFound);
//            } else {
//                // Si non, on renvoie null
//                return null;
//            }
//        }
        // On vérifie que la période ne soit pas nulle
//        if (toSave.getPeriode().getId() == null
//                && (toSave.getPeriode().getLibelle() != null)) {
//            // On cherche si une période identique n'existe pas déjà
//            Periode periodeFound = periodeDAO.findByLibelle(toSave.getPeriode().getLibelle());
//            if ( periodeFound != null) {
//                // Si oui, on l'utilise
//                toSave.setPeriode(periodeFound);
//            } else {
//                // Si non, on enregistre la nouvelle période
//                toSave.setPeriode(periodeDAO.save(toSave.getPeriode()));
//            }
//        }
    }

    private Trajet initPeriode(Trajet toSave) {
        if (toSave.getPeriode() != null) {
            if (toSave.getPeriode().getId() != null) {
                // Inutile d'aller plus loin
                return toSave;
            } else {
                if (toSave.getPeriode().getLibelle() != null) {
                    Periode periodeFound = periodeDAO.findByLibelle(toSave.getPeriode().getLibelle());
                    if (periodeFound != null) {
                        toSave.setPeriode(periodeFound);
                        return toSave;
                    } else {
                        return null;
                    }
                } else {
                    // La période n'a pas de libelle
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private Trajet initLigne(Trajet toSave) {
        if (toSave.getLigne() != null) {
            if (toSave.getLigne().getId() != null) {
                // Inutile d'aller plus loin
                return toSave;
            } else {
                if (toSave.getLigne().getLibelle() != null) {
                    Ligne ligneFound = ligneDAO.findByLibelle(toSave.getLigne().getLibelle());
                    if (ligneFound != null) {
                        toSave.setLigne(ligneFound);
                        return toSave;
                    } else {
                        return null;
                    }
                } else {
                    // La ligne n'a pas de libelle
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    private Trajet initPremierPassage(Trajet toSave) {
        if (toSave.getPremierPassage() != null) {
            if (toSave.getPremierPassage().getId() != null) {
                // Inutile d'aller plus loin
                return toSave;
            } else {
                if (toSave.getPremierPassage().getArret() != null
                        && toSave.getPremierPassage().getHoraire() != null) {
                    Passage passageFound = passageDAO.findByArretAndHoraire(
                            toSave.getPremierPassage().getArret(),
                            toSave.getPremierPassage().getHoraire());
                    if (passageFound != null) {
                        toSave.setPremierPassage(passageFound);
                        return toSave;
                    } else {
                        return null;
                    }
                } else {
                    // Le passage n'a pas d'arrêt ou d'horaire
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public List<Trajet> saveAll(List<Trajet> toSave) {
        List<Trajet> trajets = new ArrayList<>();
        for (Trajet trajet : toSave) {
            trajets.add(save(trajet));
        }
        return trajets;
    }

    @Override
    public int delete(Trajet toDelete) {
        return sqLiteDatabase.delete(BDHelper.TRAJET_NOM_TABLE,
                BDHelper.TRAJET_CLE + PARAMETRE,
                new String[]{toDelete.getId().toString()}
                );
    }

    @Override
    public int deleteAll(List<Trajet> toDelete) {
        int nbSupprime = 0;
        for (Trajet trajet : toDelete) {
            nbSupprime += delete(trajet);
        }
        return nbSupprime;
    }

    @Override
    public Trajet update(Trajet toUpdate) {
        // On créer un nouvel enregistrement
        ContentValues enregistrement = objectToContentValues(toUpdate);

        // On modifie l'enregistrement
        sqLiteDatabase.update(
                BDHelper.TRAJET_NOM_TABLE,
                enregistrement,
                BDHelper.TRAJET_CLE + " = ?",
                new String[] { String.valueOf(toUpdate.getId()) }
        );

        // On renvoie l'entite une fois modifiée
        return findById(toUpdate.getId());
    }

    @Override
    public Cursor cursorFindAll() {
        return sqLiteDatabase.rawQuery(FIND_ALL, null);
    }

    @Override
    public Trajet cursorToObject(Cursor cursor) {
        Trajet result;

        if (cursor.getCount() <= 0) {
            cursor.close();
            return null;
        }

        // Si le curseur ne contient qu'un seul enregistrement, on l'initialise à cet enregistrement,
        // S'il en contient plus d'un, cela crée une boucle infinie avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
        }

        result = new Trajet();
        result.setId(cursor.getLong(COLONNE_NUM_CLE));

        Periode periode = periodeDAO.findById(cursor.getLong(COLONNE_NUM_PERIODE));
        result.setPeriode(periode);

        Ligne ligne = ligneDAO.findById(cursor.getLong(COLONNE_NUM_LIGNE));
        result.setLigne(ligne);

        Passage passage = passageDAO.findById(cursor.getLong(COLONNE_NUM_PREMIER_PASSAGE));
        result.setPremierPassage(passage);

        // On ferme le curseur s'il ne contient qu'un seul enregistrement,
        // Sinon on ne peut pas accéder aux autres avec la méthode CursorToObjectList
        if (cursor.getCount() == 1) {
            cursor.close();
        }

        return result;
    }

    @Override
    public List<Trajet> cursorToObjectList(Cursor cursor) {
        List<Trajet> trajets = new ArrayList<>();

        for ( cursor.moveToFirst() ; !cursor.isAfterLast() ; cursor.moveToNext()) {
            Trajet trajet = cursorToObject(cursor);
            trajets.add(trajet);
        }

        // On ferme le curseur après la conversion
        cursor.close();
        return trajets;
    }

    @Override
    public ContentValues objectToContentValues(Trajet toConvert) {
        ContentValues enregistrement = new ContentValues();

        enregistrement.put(BDHelper.TRAJET_CLE, toConvert.getId());

        if (toConvert.getPeriode() != null && toConvert.getPeriode().getId() != null) {
            enregistrement.put(BDHelper.TRAJET_PERIODE, toConvert.getPeriode().getId());
        } else {
            enregistrement.putNull(BDHelper.TRAJET_PERIODE);
        }

        enregistrement.put(BDHelper.TRAJET_LIGNE, toConvert.getLigne().getId());
        enregistrement.put(BDHelper.TRAJET_PREMIER_PASSAGE, toConvert.getPremierPassage().getId());

        return enregistrement;
    }

    /**
     *
     * @param ligne
     * @return
     */
    public int deleteByLigne(Ligne ligne) {
        return sqLiteDatabase.delete(
                BDHelper.TRAJET_NOM_TABLE,
                BDHelper.TRAJET_LIGNE + PARAMETRE,
                new String[] {
                    ligne.getId().toString()
                }
                );
    }

    public void clear() {
        sqLiteDatabase.delete(BDHelper.TRAJET_NOM_TABLE, null, null);
    }
}
