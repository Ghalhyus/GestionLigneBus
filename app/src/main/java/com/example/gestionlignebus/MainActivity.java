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

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
//                intent.setType("*/*");
                String[] mimetypes = {"application/json"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);
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

    @Override
    public void onActivityResult(int codeRequete, int codeResultat, Intent intent)  {
        super.onActivityResult(codeRequete, codeResultat, intent);
        if (codeRequete == PICKFILE_REQUEST_CODE &&
                codeResultat ==  Activity.RESULT_OK) {
            arretDAO = new ArretDAO(this);
            arretDAO.open();
            periodeDAO = new PeriodeDAO(this);
            periodeDAO.open();
            ligneDAO = new LigneDAO(this);
            ligneDAO.open();
            Uri uri = Uri.parse(intent.getDataString());
            try {
                // On ouvre le fichier afin de pouvoir le lire
                InputStream inputStream = getContentResolver().openInputStream(uri);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                // On récupère le contenu du JSON
                String content = JSONUtils.readJSON(bufferedReader);

                // On enregistre les arrêts du fichier
                List<Arret> arretList = JSONUtils.jsonToArretList(content);
                arretDAO.saveAll(arretList);

                // On enregistre les périodes du fichier
                List<Periode> periodeList = JSONUtils.jsonToPeriodeList(content);
                periodeDAO.saveAll(periodeList);

                // On enregistre les lignes du fichier 
                List<Ligne> lignes = JSONUtils.jsonToLigneList(content);
                ligneDAO.saveAll(lignes);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } finally {
                arretDAO.close();
                periodeDAO.close();
                ligneDAO.close();
            }
        }
    }
}