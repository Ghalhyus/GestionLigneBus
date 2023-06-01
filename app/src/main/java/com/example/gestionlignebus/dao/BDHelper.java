package com.example.gestionlignebus.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BDHelper extends SQLiteOpenHelper {
    public static final String NOM_BD = "gestion_bus.db";
    public static final Integer VERSION = 1;

    ///////////////////////////////////////////////////
    /////////////////////// Arret /////////////////////
    ///////////////////////////////////////////////////
    public static final String ARRET_NOM_TABLE = "arret";
    public static final String ARRET_CLE = "_id";
    public static final String ARRET_LIBELLE = "libelle";
    public static final String ARRET_POSITION = "position";

    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String FOREIGN_KEY = " FOREIGN KEY (";
    private static final String REFERENCES = ") REFERENCES ";
    private static final String DROP_TABLE = "DROP TABLE ";
    private static final String INTEGER = " INTEGER, ";
    private static final String INTEGER_NOT_NULL = " Integer NOT NULL, ";
    private static final String INTEGER_PRIMARY_KEY_CONSTRAINT
            = " Integer PRIMARY KEY AUTOINCREMENT NOT NULL,";

    private static final String ARRET_CREATION_TABLE = CREATE_TABLE + ARRET_NOM_TABLE + "( " +
            ARRET_CLE + INTEGER_PRIMARY_KEY_CONSTRAINT +
            ARRET_LIBELLE + " TEXT NOT NULL UNIQUE, " +
            ARRET_POSITION + " TEXT NOT NULL UNIQUE " +
            ");";
    private static final String ARRET_SUPPRESSION_TABLE = DROP_TABLE + ARRET_NOM_TABLE;
    ///////////////////////////////////////////////////
    /////////////////////// Ligne /////////////////////
    ///////////////////////////////////////////////////
    public static final String LIGNE_NOM_TABLE = "ligne";
    public static final String LIGNE_CLE = "_id";
    public static final String LIGNE_LIBELLE = "libelle";
    public static final String LIGNE_FK_ARRET_ALLE = "arret_alle";
    public static final String LIGNE_FK_ARRET_RETOUR = "arret_retour";
    public static final String LIGNE_CREATION_TABLE = CREATE_TABLE + LIGNE_NOM_TABLE + "( " +
            LIGNE_CLE + INTEGER_PRIMARY_KEY_CONSTRAINT +
            LIGNE_LIBELLE + " TEXT NOT NULL UNIQUE, " +
            LIGNE_FK_ARRET_ALLE + INTEGER +
            LIGNE_FK_ARRET_RETOUR + INTEGER +
            FOREIGN_KEY + LIGNE_FK_ARRET_ALLE + REFERENCES
            + ARRET_NOM_TABLE + "("+ ARRET_CLE + ")" +
            FOREIGN_KEY + LIGNE_FK_ARRET_RETOUR + REFERENCES + ARRET_NOM_TABLE
            + "("+ ARRET_CLE + ")" + ");";
    public static final String LIGNE_SUPPRESSION_TABLE = DROP_TABLE+ LIGNE_NOM_TABLE;

    ///////////////////////////////////////////////////
    /////////////////////// Groupe/////////////////////
    ///////////////////////////////////////////////////
    public static final String GROUPE_NOM_TABLE = "groupe";
    public static final String GROUPE_CLE = "_id";
    public static final String GROUPE_LIBELLE = "libelle";
    public static final String GROUPE_CREATION_TABLE = CREATE_TABLE + GROUPE_NOM_TABLE + "( " +
            GROUPE_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE," +
            GROUPE_LIBELLE + " TEXT UNIQUE NOT NULL " +
            ");";
    public static final String GROUPE_SUPPRESSION_TABLE = DROP_TABLE+ GROUPE_NOM_TABLE;

    ///////////////////////////////////////////////////
    ////////////////////// Période ////////////////////
    ///////////////////////////////////////////////////
    public static final String PERIODE_NOM_TABLE = "periode";
    public static final String PERIODE_CLE = "_id";
    public static final String PERIODE_LIBELLE = "libelle";
    public static final String PERIODE_CREATION_TABLE = CREATE_TABLE + PERIODE_NOM_TABLE + "( " +
            PERIODE_CLE + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            PERIODE_LIBELLE + " TEXT UNIQUE NOT NULL " +
            ");";
    public static final String PERIODE_SUPPRESSION_TABLE = DROP_TABLE+ PERIODE_NOM_TABLE;

    ///////////////////////////////////////////////////
    ////////////////////// Passage ////////////////////
    ///////////////////////////////////////////////////
    public static final String PASSAGE_NOM_TABLE = "passage";
    public static final String PASSAGE_CLE = "_id";
    public static final String PASSAGE_ARRET = "arret";
    public static final String PASSAGE_HORAIRE = "horaire";
    public static final String PASSAGE_PASSAGE_SUIVANT = "passage_suivant";
    public static final String PASSAGE_CREATION_TABLE = CREATE_TABLE + PASSAGE_NOM_TABLE + "( " +
            PASSAGE_CLE + INTEGER_PRIMARY_KEY_CONSTRAINT +
            PASSAGE_ARRET + INTEGER_NOT_NULL +
            PASSAGE_HORAIRE + " TEXT NOT NULL," +
            PASSAGE_PASSAGE_SUIVANT + INTEGER +
            FOREIGN_KEY + PASSAGE_PASSAGE_SUIVANT + REFERENCES + PASSAGE_NOM_TABLE
            + "("+ PASSAGE_CLE + ")," +
            FOREIGN_KEY + PASSAGE_ARRET + REFERENCES + ARRET_NOM_TABLE + "("+ ARRET_CLE + ")," +
            "UNIQUE(" + PASSAGE_ARRET + "," + PASSAGE_HORAIRE + " )" +
            ");";
    public static final String PASSAGE_SUPPRESSION_TABLE = DROP_TABLE+ PASSAGE_NOM_TABLE;

    ///////////////////////////////////////////////////
    ////////////////////// Trajet /////////////////////
    ///////////////////////////////////////////////////
    public static final String TRAJET_NOM_TABLE = "trajet";
    public static final String TRAJET_CLE = "_id";
    public static final String TRAJET_PERIODE = "periode";
    public static final String TRAJET_LIGNE = "ligne";
    public static final String TRAJET_PREMIER_PASSAGE = "premier_passage";
    public static final String TRAJET_CREATION_TABLE = CREATE_TABLE + TRAJET_NOM_TABLE + "( " +
            TRAJET_CLE + INTEGER_PRIMARY_KEY_CONSTRAINT +
            TRAJET_PERIODE + INTEGER +
            TRAJET_LIGNE + INTEGER +
            TRAJET_PREMIER_PASSAGE + INTEGER +
            FOREIGN_KEY + TRAJET_PERIODE + REFERENCES + PERIODE_NOM_TABLE
            + "("+ PERIODE_CLE + ")," +
            FOREIGN_KEY + TRAJET_LIGNE + REFERENCES + LIGNE_NOM_TABLE + "("+ LIGNE_CLE + ")," +
            FOREIGN_KEY + TRAJET_PREMIER_PASSAGE + REFERENCES + PASSAGE_NOM_TABLE
            + "("+ PASSAGE_CLE + ")" +
            ");";
    public static final String TRAJET_SUPPRESSION_TABLE = DROP_TABLE+ TRAJET_NOM_TABLE;

    ///////////////////////////////////////////////////
    ///////////// JOINTURE LIGNE/ARRET ////////////////
    ///////////////////////////////////////////////////
    public static final String LIGNE_ARRET_NOM_TABLE = "ligne_arret";
    public static final String LIGNE_ARRET_FK_LIGNE = "id_ligne";
    public static final String LIGNE_ARRET_FK_ARRET = "id_arret";
    public static final String LIGNE_ARRET_CREATION_TABLE = CREATE_TABLE
            + LIGNE_ARRET_NOM_TABLE + "( " +
            LIGNE_ARRET_FK_LIGNE + INTEGER_NOT_NULL +
            LIGNE_ARRET_FK_ARRET + " INTEGER NOT NULL, " +
            FOREIGN_KEY + LIGNE_ARRET_FK_LIGNE + REFERENCES + LIGNE_NOM_TABLE
            + "("+ LIGNE_CLE + ")," +
            FOREIGN_KEY + LIGNE_ARRET_FK_ARRET + REFERENCES + ARRET_NOM_TABLE
            + "("+ ARRET_CLE + ")," +
            "PRIMARY KEY(" + LIGNE_ARRET_FK_LIGNE + "," + LIGNE_ARRET_FK_ARRET
            + "));";
    public static final String LIGNE_ARRET_SUPPRESSION_TABLE = DROP_TABLE+ LIGNE_ARRET_NOM_TABLE;

    ///////////////////////////////////////////////////
    //////////// JOINTURE GROUPE/ARRET ////////////////
    ///////////////////////////////////////////////////
    public static final String GROUPE_ARRET_NOM_TABLE = "groupe_arret";
    public static final String GROUPE_ARRET_FK_GROUPE = "id_groupe";
    public static final String GROUPE_ARRET_FK_ARRET = "id_arret";
    public static final String GROUPE_ARRET_CREATION_TABLE = CREATE_TABLE
            + GROUPE_ARRET_NOM_TABLE + "( " +
            GROUPE_ARRET_FK_GROUPE + INTEGER_NOT_NULL +
            GROUPE_ARRET_FK_ARRET + " INTEGER NOT NULL, " +
            FOREIGN_KEY + GROUPE_ARRET_FK_GROUPE + REFERENCES + GROUPE_NOM_TABLE
            + "("+ GROUPE_CLE + ")," +
            FOREIGN_KEY + GROUPE_ARRET_FK_ARRET + REFERENCES + ARRET_NOM_TABLE
            + "("+ ARRET_CLE + ")," +
            "PRIMARY KEY(" + GROUPE_ARRET_FK_GROUPE + "," + GROUPE_ARRET_FK_ARRET
            + "));";
    public static final String GROUPE_ARRET_SUPPRESSION_TABLE = DROP_TABLE+ GROUPE_ARRET_NOM_TABLE;


    public BDHelper(@Nullable Context context, @Nullable String name,
                    @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(ARRET_CREATION_TABLE);
        sqLiteDatabase.execSQL(LIGNE_CREATION_TABLE);
        sqLiteDatabase.execSQL(GROUPE_CREATION_TABLE);
        sqLiteDatabase.execSQL(PERIODE_CREATION_TABLE);
        sqLiteDatabase.execSQL(PASSAGE_CREATION_TABLE);
        // Jointures
        sqLiteDatabase.execSQL(LIGNE_ARRET_CREATION_TABLE);
        sqLiteDatabase.execSQL(GROUPE_ARRET_CREATION_TABLE);
        sqLiteDatabase.execSQL(TRAJET_CREATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Jointures
        sqLiteDatabase.execSQL(LIGNE_ARRET_SUPPRESSION_TABLE);
        sqLiteDatabase.execSQL(GROUPE_ARRET_SUPPRESSION_TABLE);
        sqLiteDatabase.execSQL(TRAJET_SUPPRESSION_TABLE);

        sqLiteDatabase.execSQL(ARRET_SUPPRESSION_TABLE);
        sqLiteDatabase.execSQL(LIGNE_SUPPRESSION_TABLE);
        sqLiteDatabase.execSQL(GROUPE_SUPPRESSION_TABLE);
        sqLiteDatabase.execSQL(PERIODE_SUPPRESSION_TABLE);
        sqLiteDatabase.execSQL(PASSAGE_SUPPRESSION_TABLE);
    }
}
