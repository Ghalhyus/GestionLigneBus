package com.example.gestionlignebus;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.gestionlignebus.adapter.AdapterPage;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    public static final String CLE_LOG = "gestion_lignes_bus";

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