package com.example.gestionlignebus.dao;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.List;

public interface ICommonDAO<T, Id> {

    public static final String SELECT_ETOILE = "SELECT * FROM ";
    public static final String WHERE = " WHERE ";
    public static final String PARAMETRE = " = ? ";

    // BDD

    /**
     * Ouvre la connection avec la base de donnée
     */
    void open();

    /**
     * Ferme la connection avec la base de donnée
     */
    void close();

    // CRUD

    /**
     * Cherche un objet de type {@link T} en fonction de son identifiant
     * de type {@link Id}
     * @param id l'identifiant de l'objet recherché
     * @return l'objet recherché
     */
    T findById(Id id);

    /**
     * Cherche tous les objets {@link T} de sa table
     * @return la liste des {@link T}
     */
    List<T> findAll();

    /**
     * Enregistre un objet dans sa table correspondante
     * @param toSave l'objet {@link T} à créer dans la base
     * @return l'objet s'il a été enregistré sinon null
     */
    T save(T toSave);

    /**
     * Enregistre une liste d'objet dans la table correspondante
     * @param toSave la liste d'objet {@link T} à créer dans la base
     * @return l'objet s'il a été enregistré sinon null
     */
    List<T> saveAll(List<T> toSave);

    /**
     * Supprime un objet de sa table correspondante
     * @param toDelete L'objet de type {@link T} à supprimer de la base
     * @return lenombre d'objet supprimé
     */
    int delete(T toDelete);
    /**
     * Supprime une liste d'objet de la table correspondante
     * @param toDelete La liste d'objet de type {@link T} à supprimer de la base
     * @return lenombre d'objet supprimé
     */
    int deleteAll(List<T> toDelete);

    /**
     * Modifie l'objet de la table à partir de son identifiant
     * @param toUpdate l'objet à modifier
     * @return l'objet modifié
     */
    T update(T toUpdate);

    // Cursor

    /**
     * Renvoie un curseur de l'ensemble des données
     * de la table correspondante
     * @return Le curseur
     */
    Cursor cursorFindAll();

    /**
     * Convertit un Cursor en objet de type {@link T}
     * @param cursor le curseur à convertir
     * @return l'objet converti
     */
    T cursorToObject(Cursor cursor);

    /**
     * Convertit un Cursor en liste d'objet de type {@link T}
     * @param cursor la liste de curseur à convertir
     * @return la liste d'objet converti
     */
    List<T> cursorToObjectList(Cursor cursor);


    // ContentValues

    /**
     * Transforme un objet en ContentValues
     * @param toConvert l'objet à convertir
     * @return l'enregistrement
     */
    ContentValues objectToContentValues(T toConvert);


}
