package com.example.gestionlignebus.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.gestionlignebus.MainActivity;

import java.io.IOException;
import java.security.GeneralSecurityException;

/**
 * Gère les différentes méthodes liées à la sécurité.
 */
public class Preferences {

    private static Preferences instance;
    private SharedPreferences preferences;

    private Preferences(Context context) {
        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            preferences =  EncryptedSharedPreferences.create(
                    "secret",
                    masterKey,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException e) {
            Log.e(MainActivity.CLE_LOG,
                    "Erreur de sécurité lors la génération de la master Keys");
        } catch (IOException e) {
            Log.e(MainActivity.CLE_LOG,
                    "Erreur fichier introuvable pour la génération de la master Keys");
        }
    }

    public static Preferences getPreferences(Context context) {
        if (instance == null) {
            instance = new Preferences(context);
        }

        return instance;
    }

    public Long getLong(String cle) {
        return preferences != null ? preferences.getLong(cle, 1L) : 1L;
    }

    public boolean getBoolean(String cle) {
        return preferences != null ? preferences.getBoolean(cle, false) : false;
    }

    public String getString(String cle) {
        return preferences != null ? preferences.getString(cle, "") : "";
    }

    public SharedPreferences.Editor edit(){
        return preferences != null ? preferences.edit() : null;
    }
}
