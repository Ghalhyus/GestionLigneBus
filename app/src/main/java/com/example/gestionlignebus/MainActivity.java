package com.example.gestionlignebus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.gestionlignebus.adapter.AdapterPage;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

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
                getString(R.string.recherche_onglet),
                getString(R.string.groupe_onglet),
                getString(R.string.itineraire_onglet)
        };

        // On affecte les nouveaux noms récupérés
        new TabLayoutMediator(
                gestionnaireOnglet, gestionnairePagination,
                (tab, position) -> tab.setText(titreOnglet[position])).attach();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_optionnel_base, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.importer_lignes: //importation des informations stables
                // Gérer l'importation
                break;
            case R.id.importation_horaire://importation des information
                // Gérer l'importation
                break;
            case R.id.annuler:
                // Annuler l'opération en cours
                break;
            default:
                break;


        }
        return super.onOptionsItemSelected(item);
    }
}