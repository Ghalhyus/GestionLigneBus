package com.example.gestionlignebus.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.adapter.ArretHoraireAdapteur;
import com.example.gestionlignebus.adapter.PeriodeSpinnerAdapter;
import com.example.gestionlignebus.dao.LigneDAO;
import com.example.gestionlignebus.dao.PeriodeDAO;
import com.example.gestionlignebus.dao.TrajetDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.ArretHoraire;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;
import com.example.gestionlignebus.utils.Preferences;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Gère l'affichage d'une fiche horaire pour une ligne de bus.
 */
public class LigneActivity extends AppCompatActivity
        implements View.OnClickListener, AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    /**
     * Clé permmettant de récupérer l'id de la ligne sélectionnée.
     */
    public static final String CLE_LIGNE = "cle_ligne";

    /**
     * Stocke la liste des passages pour le trajet courant.
     */
    private ArretHoraireAdapteur gestionnaireTrajet;

    /**
     * Index du trajet courant
     */
    private int indexTrajet;

    /**
     * Ligne sélectionnée.
     */
    private Ligne ligneCourante;

    /**
     * Fiche horaire de la ligne pour le trajet courant.
     */
    private RecyclerView ficheHoraire;

    /**
     * Trajets pour la ligne et et la période sélectionnées.
     */
    private List<Trajet> trajets;

    /**
     * Période sélectionnée depuis le spinner des périodes.
     */
    private Periode periodeSelectionnee;

    /**
     * True pour afficher dans le sens du retour, sinon false pour le sens par défaut.
     */
    private boolean retour;

    /**
     * Map pour afficher la liste des arrêts de la ligne.
     */
    private View map;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ligne);

        Preferences preferences = Preferences.getPreferences(this);
        ligneCourante = getLigneCourante(preferences.getLong(CLE_LIGNE));

        initialisationSpinner();
        initialisationBoutons();

        // Initialisation du recycler view
        ficheHoraire = findViewById(R.id.fiche_horaires);
        ficheHoraire.setLayoutManager(new LinearLayoutManager(this));
        gestionnaireTrajet = new ArretHoraireAdapteur(initialisationTrajets());
        ficheHoraire.setAdapter(gestionnaireTrajet);

        // initialisation de la map
        map = LayoutInflater.from(this) .inflate(R.layout.popup_map,null);
    }

    /**
     * Initialisation du spinner des périodes.
     */
    private void initialisationSpinner() {
        Spinner spinnerPeriodes = findViewById(R.id.spinner_periode);

        List<Periode> periodes = recupererPeriodes();
        PeriodeSpinnerAdapter periodeSpinnerAdapter
                = new PeriodeSpinnerAdapter(this, periodes);

        spinnerPeriodes.setAdapter(periodeSpinnerAdapter);
        spinnerPeriodes.setOnItemSelectedListener(this);

        periodeSelectionnee = (Periode) spinnerPeriodes.getSelectedItem();
    }

    /**
     * Initialisation des boutons de l'activité.
     */
    private void initialisationBoutons() {
        Button inverserSensPassage = findViewById(R.id.inverser_sens_ligne);
        inverserSensPassage.setOnClickListener(this);

        Button btnAfficherCarte = findViewById(R.id.afficher_carte);
        btnAfficherCarte.setOnClickListener(this);

        Button trajetPrecedentBouton = findViewById(R.id.trajet_precedent);
        trajetPrecedentBouton.setOnClickListener(this);

        Button trajetSuivantBouton = findViewById(R.id.trajet_suivant);
        trajetSuivantBouton.setOnClickListener(this);
    }

    /**
     * Initialisation de la liste des trajets par défauts.
     * @return La liste des passages pour le premier trajet.
     */
    private ArrayList<ArretHoraire> initialisationTrajets() {
        retour = false;
        indexTrajet = 0;
        recupererTrajet();

        return chargerTrajet(indexTrajet);
    }

    /**
     * Récupère la ligne sélectionnée en base de données.
     * @param id Id de la ligne sélectionnée.
     * @return La ligne récupérée en base de données.
     */
    private Ligne getLigneCourante(long id) {

        LigneDAO ligneDao = new LigneDAO(this);
        ligneDao.open();

        Ligne courante = ligneDao.findById(id);

        ligneDao.close();

        return courante;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
        periodeSelectionnee = (Periode) adapterView.getSelectedItem();

        gestionnaireTrajet = new ArretHoraireAdapteur(initialisationTrajets());
        ficheHoraire.setAdapter(gestionnaireTrajet);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Empty body
    }

    @Override
    public void onClick(View view) {

        if (view.getId() == R.id.afficher_carte) {
            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            if (map.getParent() != null) {
                ((ViewGroup)map.getParent()).removeView(map);
            }

            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            builder.setView(map);

            builder.setTitle(R.string.affichage_arret_carte);

            builder.setNegativeButton(R.string.btn_retour, null);

            builder.show();
        } else if (view.getId() == R.id.trajet_precedent
                && indexTrajet > 0) {
            changerTrajet(--indexTrajet);
        } else if (view.getId() == R.id.trajet_suivant
                && indexTrajet < trajets.size() - 1) {
            changerTrajet(++indexTrajet);
        } else if (view.getId() == R.id.inverser_sens_ligne) {
            inverserSensLigne();
        } else if (view.getId() == R.id.trajet_precedent
                || view.getId() == R.id.trajet_suivant) {
            Toast.makeText(this, R.string.erreur_trajet, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Récupère un trajet dans la liste des trajets selon sa position.
     * @param index Position du trajet à récupérer.
     * @return La liste des passages pour le trajet récupéré.
     */
    private ArrayList<ArretHoraire> chargerTrajet(int index) {
        List<String> listeHorairesPassages = new ArrayList<>();
        List<String> listeLibellesArrets = new ArrayList<>();

        if (!trajets.isEmpty()) {
            List<Passage> trajetCourant = trajets.get(index).getPremierPassage().getPassages();
            listeLibellesArrets = Passage.getArretsPassages(trajetCourant);
            listeHorairesPassages = Passage.getHorairesPassages(trajetCourant);
        }

        ArrayList<ArretHoraire> arretHoraires = new ArrayList<>();
        for (int i = 0; i < listeHorairesPassages.size(); i++) {
            arretHoraires.add(new ArretHoraire(listeLibellesArrets.get(i),
                    listeHorairesPassages.get(i)));
        }

        return arretHoraires;
    }

    /**
     * Met à jour la fiche horaire da ligne selon le trajet indiqué par sa position.
     * @param index Position du trajet à récupéré.
     */
    private void changerTrajet(int index) {
        gestionnaireTrajet = new ArretHoraireAdapteur(chargerTrajet(index));
        ficheHoraire.setAdapter(gestionnaireTrajet);
    }

    /**
     * Inverse le sens de passage de la fiche horaire.
     */
    private void inverserSensLigne() {
        retour = !retour;
        recupererTrajet();

        List<Trajet> tmpTrajets = new ArrayList<>();
        for (Trajet trajet : trajets) {
            if ((!retour && trajet.getPremierPassage().getArret()
                    .equals(ligneCourante.getArretDepart()))
                    || (retour && trajet.getPremierPassage().getArret()
                    .equals(ligneCourante.getArretRetour()))) {
                tmpTrajets.add(trajet);
            }
        }

        trajets = tmpTrajets;
        changerTrajet(0);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        List<Passage> trajetCourant = trajets.get(indexTrajet).getPremierPassage().getPassages();

        for (Passage passage : trajetCourant) {
            Arret arret = passage.getArret();
            double latitude = Double.parseDouble(arret.getPosition().split(":")[0]);
            double longitude = Double.parseDouble(arret.getPosition().split(":")[1]);
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(arret.getLibelle()));

        }

        double latitude = Double.parseDouble(
                trajetCourant.get(0).getArret().getPosition().split(":")[0]);
        double longitude = Double.parseDouble(
                trajetCourant.get(0).getArret().getPosition().split(":")[1]);

        googleMap.moveCamera(CameraUpdateFactory.newLatLng( new LatLng(latitude,longitude)));
        googleMap.setMinZoomPreference(15);
    }

    /**
     * Récupère la liste des trajets en base de données, en fonction d'une période et d'une ligne.
     */
    private void recupererTrajet() {
        TrajetDAO trajetDao = new TrajetDAO(this);
        trajetDao.open();

        trajets = trajetDao.findByPeriodeAndLigne(periodeSelectionnee, ligneCourante);

        trajetDao.close();
    }

    /**
     * Récupère la liste des périodes en base de données.
     * @return La liste des périodes récupérées.
     */
    private List<Periode> recupererPeriodes() {
        PeriodeDAO periodeDao = new PeriodeDAO(this);
        periodeDao.open();

        List<Periode> periodes = periodeDao.findAll();

        periodeDao.close();

        return periodes;
    }
}