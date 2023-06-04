package com.example.gestionlignebus.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionlignebus.MainActivity;
import com.example.gestionlignebus.R;
import com.example.gestionlignebus.adapter.LigneSpinnerAdapter;
import com.example.gestionlignebus.dao.LigneDAO;
import com.example.gestionlignebus.dao.PassageDAO;
import com.example.gestionlignebus.dao.TrajetDAO;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Trajet;
import com.example.gestionlignebus.utils.JSONUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class ChangerFicheHoraireActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_FILE = 10;
    private Spinner spinnerLigne;
    private LigneDAO ligneDAO;
    private TrajetDAO trajetDAO;
    private PassageDAO passageDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changer_fiche_horaire);

        initialisationDao();
        initialisationIHM();

    }

    private void initialisationDao() {
        ligneDAO = new LigneDAO(this);
        ligneDAO.open();

        passageDAO = new PassageDAO(this);
        passageDAO.open();

        trajetDAO = new TrajetDAO(this);
        trajetDAO.open();
    }

    private void initialisationIHM() {
        spinnerLigne = findViewById(R.id.spinner_ligne);
        LigneSpinnerAdapter ligneSpinnerAdapter
                = new LigneSpinnerAdapter(this, ligneDAO.findAll());
        spinnerLigne.setAdapter(ligneSpinnerAdapter);

        Button btnImportFichier = findViewById(R.id.btn_import_variable);
        Button btnRetour = findViewById(R.id.btn_retour);

        btnRetour.setOnClickListener(this);
        btnImportFichier.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_retour) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        }
        if (view.getId() == R.id.btn_import_variable) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("file/*");
            String[] mimetypes = {"application/json"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
            startActivityForResult(intent, PICK_FILE);
        }
    }

    @Override
    public void onActivityResult(int codeRequete, int codeResultat, Intent intent)  {
        super.onActivityResult(codeRequete, codeResultat, intent);
        if (codeRequete == PICK_FILE &&
                codeResultat ==  Activity.RESULT_OK) {
            enregistrerDonneeVariable(intent);
            initialisationDao();
        }
    }

    public void enregistrerDonneeVariable(Intent intent) {
        Uri uri = Uri.parse(intent.getDataString());
        try {
            // On ouvre le fichier afin de pouvoir le lire
            InputStream inputStream = getContentResolver().openInputStream(uri);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // On récupère le contenu du JSON
            String content = JSONUtils.readJSON(bufferedReader);

            if (!Objects.equals(content, "")) {
                List<Trajet> trajets = JSONUtils.jsonToTrajets(content);


                if (trajets != null) {
                    // On récupère les trajets d'une ligne
                    List<Trajet> trajetsByLigne
                            = trajetDAO.findByLigne((Ligne) spinnerLigne.getSelectedItem());
                    if (trajetsByLigne != null) {
                        // On supprime les trajets d'une ligne
                        trajetDAO.deleteByLigne((Ligne) spinnerLigne.getSelectedItem());
                        for (Trajet trajet : trajetsByLigne) {
                            // Pour chaque trajet on supprime les passages chaînés
                            passageDAO.delete(trajet.getPremierPassage());
                        }
                    }

                    int nbErreurTrajet = enregistrerTrajet(trajets);
                    afficherResultat(nbErreurTrajet);

                } else {
                    // Erreur lecture fichier
                    Toast.makeText(this, getString(R.string.erreur_import_trajet),
                            Toast.LENGTH_LONG).show();
                }

            } else {
                // Erreur lecture fichier
                Toast.makeText(this, getString(R.string.erreur_fichier_vide),
                        Toast.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, getString(R.string.erreur_ouverture_fichier),
                    Toast.LENGTH_LONG).show();
        } finally {
            ligneDAO.close();
            passageDAO.close();
        }
    }

    /**
     * Enregistre en base de données les trajets récupérés depuis le fichier JSON.
     * @param trajets La liste des trajets à enregistrer.
     * @return Le nombre de trajet n'ayant pas été enregistré.
     */
    private int enregistrerTrajet(List<Trajet> trajets) {
        int nbErreurTrajet = 0;

        for (Trajet trajet : trajets) {
            Passage passageFound = passageDAO.findByArretAndHoraire(
                    trajet.getPremierPassage().getArret(),
                    trajet.getPremierPassage().getHoraire());

            if (passageFound != null) {
                trajet.setPremierPassage(passageFound);
            } else {
                Passage passageSaved = passageDAO.save(trajet.getPremierPassage());
                trajet.setPremierPassage(passageSaved);
            }

            // On s'assure que les trajets sont tous ajoutés pour cette ligne
            trajet.setLigne((Ligne) spinnerLigne.getSelectedItem());
            Trajet trajetSaved = trajetDAO.save(trajet);
            if (trajetSaved == null) {
                nbErreurTrajet++;
            }
        }

        return nbErreurTrajet;
    }

    /**
     * Affiche le résultat de l'importation des trajets.
     * @param nbErreurTrajet Le nombre de trajet en erreur.
     */
    private void afficherResultat(int nbErreurTrajet) {
        if (nbErreurTrajet > 0 ) {
            // Message erreur
            String message = String.format(getString(R.string.erreur_nb_trajet), nbErreurTrajet);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            String messageReussite = String.format(
                    getString(R.string.reussite_import_donnée_variable),
                    ((Ligne) spinnerLigne.getSelectedItem()).getLibelle());
            Toast.makeText(this, messageReussite, Toast.LENGTH_LONG).show();
        }
    }
}
