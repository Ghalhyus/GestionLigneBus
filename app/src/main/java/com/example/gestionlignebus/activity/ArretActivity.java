package com.example.gestionlignebus.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.gestionlignebus.MainActivity;
import com.example.gestionlignebus.model.ArretHoraire;
import com.example.gestionlignebus.adapter.ArretHoraireAdapteur;
import com.example.gestionlignebus.R;
import com.example.gestionlignebus.adapter.ListViewAdapter;
import com.example.gestionlignebus.adapter.PeriodeSpinnerAdapter;
import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.PassageDAO;
import com.example.gestionlignebus.dao.PeriodeDAO;
import com.example.gestionlignebus.dao.TrajetDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArretActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public final static  String CLE_ID = "id_arret";

    private ListViewAdapter adapter;
    private ArrayList<ArretHoraire> listArretHoraire;


    private PeriodeDAO periodeDao;

    private Periode periodeSelected;

    private Spinner spin;

    private List<Ligne> lignes;

    private List<LocalTime> horaire;

    private RecyclerView recyclerView;

    private List<Trajet> trajets;
    private TrajetDAO trajetDao;
    private Arret arret;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_arret);

        periodeDao = new PeriodeDAO(this);
        periodeDao.open();

        PassageDAO passageDao=new PassageDAO(this);
        passageDao.open();

        trajetDao=new TrajetDAO(this);
        trajetDao.open();

        ArretDAO arretDao=new ArretDAO(this);
        arretDao.open();

        recyclerView=findViewById(R.id.liste_arret);
        listArretHoraire = new ArrayList<>();

        horaire=new ArrayList<>();
        lignes=new ArrayList<>();
        initialisationSpinner();

        String libellePeriode=spin.getSelectedItem().toString();
        periodeSelected = periodeDao.findByLibelle(libellePeriode);

        LinearLayoutManager gestionnaireLineaire = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(gestionnaireLineaire);

        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            SharedPreferences preferences =  EncryptedSharedPreferences.create(
                    "secret",
                    masterKey,
                    this,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);

            arret = arretDao.findById(preferences.getLong(CLE_ID, 1));
        } catch (GeneralSecurityException e) {
            Log.e(MainActivity.CLE_LOG,
                    "Erreur de sécurité lors la génération de la master Keys");
        } catch (IOException e) {
            Log.e(MainActivity.CLE_LOG,
                    "Erreur fichier introuvable pour la génération de la master Keys");
        }

        ArretHoraireAdapteur adaptateur = new ArretHoraireAdapteur(listArretHoraire);
        recyclerView.setAdapter(adaptateur);

        periodeSelected = (Periode) spin.getSelectedItem();

        trajets=trajetDao.findByPeriodeAndArret(periodeSelected,arret);
        trouvelistligne(trajets);
        initialiseListeLigne();
    }

    private void trouvelistligne(List<Trajet> trajets) {
        for (int i = 0; i< trajets.size(); i++){
            horaire.add(trajets.get(i).getPremierPassage().getHoraire());
        }
        for (int i = 0; i< trajets.size(); i++){
            lignes.add(trajets.get(i).getLigne());
        }


    }

    private void initialisationSpinner() {

        spin = findViewById(R.id.spinner_periode);

        List<Periode> periodes = periodeDao.findAll();
        PeriodeSpinnerAdapter periodeSpinnerAdapter
                = new PeriodeSpinnerAdapter(this, periodes);

        spin.setAdapter(periodeSpinnerAdapter);
        spin.setOnItemSelectedListener(this);
    }

    private void initialiseListeLigne() {
            Map<LocalTime, Ligne> map = new HashMap<>();
            for (int i = 0; i < horaire.size(); i++) {
                map.put(horaire.get(i), lignes.get(i));
            }

            Collections.sort(horaire);

            for (LocalTime t : horaire) {
                Ligne a = map.get(t);
                listArretHoraire.add(new ArretHoraire(a.getLibelle(), t.toString()));
            }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Periode anciennePeriode=periodeSelected;
        periodeSelected = (Periode) adapterView.getSelectedItem();
        if (!anciennePeriode.equals(periodeSelected))  {
            trajets = trajetDao.findByPeriodeAndArret(periodeSelected, arret);
            listArretHoraire.clear();
            horaire.clear();
            lignes.clear();
            listArretHoraire=new ArrayList<>();
            ArretHoraireAdapteur adaptateur = new ArretHoraireAdapteur(listArretHoraire);
            recyclerView.setAdapter(adaptateur);
            trouvelistligne(trajets);

            initialiseListeLigne();

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // non utilisé
    }
}
