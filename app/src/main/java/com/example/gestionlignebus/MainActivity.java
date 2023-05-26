package com.example.gestionlignebus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.gestionlignebus.adapter.AdapterPage;
import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.LigneDAO;
import com.example.gestionlignebus.dao.PeriodeDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.utils.JSONUtils;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int PICKFILE_REQUEST_CODE = 1;

    private ArretDAO arretDAO;
    private PeriodeDAO periodeDAO;
    private LigneDAO ligneDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // On initialise l'adaptateur
        ViewPager2 gestionnairePagination = findViewById(R.id.activity_main_viewpager);

        // On initialise le gestionnaire d'onglets
        TabLayout gestionnaireOnglet = findViewById(R.id.activity_main_tablayout);

        gestionnairePagination.setAdapter(new AdapterPage(this));

        // On récupère les titres d'onglets (dans l'ordre)
        String[] titreOnglet = {
                getString(R.string.consultation_onglet),
                getString(R.string.groupe_onglet),
                getString(R.string.itineraire_onglet)
        };

        // On affecte les nouveaux noms récupérés
        new TabLayoutMediator(
                gestionnaireOnglet, gestionnairePagination,
                (tab, position) -> tab.setText(titreOnglet[position])).attach();
    }
}