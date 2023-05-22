package com.example.gestionlignebus.activity;

import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.ArretHoraire;
import com.example.gestionlignebus.ArretHoraireAdapteur;
import com.example.gestionlignebus.R;
import com.example.gestionlignebus.adapter.PeriodeSpinnerAdapter;
import com.example.gestionlignebus.dao.LigneDAO;
import com.example.gestionlignebus.dao.PassageDAO;
import com.example.gestionlignebus.dao.PeriodeDAO;
import com.example.gestionlignebus.dao.TrajetDAO;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;

import java.util.ArrayList;
import java.util.List;

public class LigneActivity extends AppCompatActivity implements View.OnClickListener,
         AdapterView.OnItemSelectedListener {
    public static final String CLE_LIGNE = "cle_ligne";
    private ArretHoraireAdapteur adaptateur;
    private ArrayList<ArretHoraire> arretHoraires;
    Dialog dialog;
    private int indexTrajet;
    private Ligne ligneCourante;
    private List<String> listeHorairesPassages;
    private List<String> listeLibellesArrets;
    private List<Passage> passages;
    private PeriodeDAO periodeDao;
    private Periode periodeSelected;
    private Spinner spin;
    PopupWindow popup;
    private RecyclerView recyclerView;
    private TrajetDAO trajetDao;
    private Button trajetPrecedentBouton;
    private Button trajetSuivantBouton;

    private List<Trajet> trajets;
    private boolean retour;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_ligne);
        dialog = new Dialog(this);

        retour = false;

        SharedPreferences preferences
                = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        trajetDao = new TrajetDAO(this);
        trajetDao.open();

        periodeDao = new PeriodeDAO(this);
        periodeDao.open();


        LigneDAO ligneDao = new LigneDAO(this);
        ligneDao.open();

        ligneCourante = ligneDao.findById(Long.valueOf(preferences.getLong(CLE_LIGNE, 1L)));
        ligneDao.close();


        initialisationSpinner();

        periodeSelected = (Periode) spin.getSelectedItem();

        listeLibellesArrets=new ArrayList<>();

        trajets = trajetDao.findByPeriodeAndLigne(periodeSelected,ligneCourante);

        Log.d("trajet", String.valueOf(trajets.size()));
        if(trajets.size()>0){
            for(int i =0;i<trajets.size();i++) {
                passages = trajets.get(i).getPremierPassage().getPassages();
                listeLibellesArrets = Passage.getArretsPassages(passages);
                listeHorairesPassages = Passage.getHorairesPassages(passages);
            }

        } else {
            listeHorairesPassages = new ArrayList<>();
        }

        arretHoraires = new ArrayList<>();
        for (int i = 0; i < listeHorairesPassages.size(); i++) {
            arretHoraires.add(new ArretHoraire(listeLibellesArrets.get(i),
                    listeHorairesPassages.get(i)));
        }

        Log.d("periode",periodeSelected.getLibelle());
        Log.d("liste arret", String.valueOf(listeLibellesArrets.size()));
        Log.d("liste horaire", String.valueOf(listeLibellesArrets.size()));



        LinearLayoutManager gestionnaireLineaire = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.fiche_horaires);
        recyclerView.setLayoutManager(gestionnaireLineaire);

        ArretHoraireAdapteur arretHoraireAdapteur = new ArretHoraireAdapteur(arretHoraires);
        adaptateur = arretHoraireAdapteur;
        recyclerView.setAdapter(arretHoraireAdapteur);

        trajetPrecedentBouton = findViewById(R.id.trajet_precedent);
        trajetPrecedentBouton.setOnClickListener(this);

        trajetSuivantBouton = findViewById(R.id.trajet_suivant);
        trajetSuivantBouton.setOnClickListener(this);
      
        Button inverserSensPassage = findViewById(R.id.inverser_sens_ligne);
        inverserSensPassage.setOnClickListener(this);

        indexTrajet = 0;

    }

    private void initialisationSpinner() {

        spin = findViewById(R.id.spinner_periode);

        List<Periode> periodes = periodeDao.findAll();
        PeriodeSpinnerAdapter periodeSpinnerAdapter
                = new PeriodeSpinnerAdapter(this, periodes);

        spin.setAdapter(periodeSpinnerAdapter);
        spin.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
        Periode anciennePeriode = periodeSelected;

        periodeSelected = (Periode) adapterView.getSelectedItem();

        if (!anciennePeriode.equals(periodeSelected)) {
            trajets = trajetDao.findByPeriodeAndLigne(periodeSelected, ligneCourante);
            listeHorairesPassages.clear();
            listeLibellesArrets.clear();
            arretHoraires.clear();

            arretHoraires=new ArrayList<>();

            Log.d("trajet", String.valueOf(trajets.size()));
            if(trajets.size()>0){
                for(int i =0;i<trajets.size();i++) {
                    passages = trajets.get(0).getPremierPassage().getPassages();
                    listeLibellesArrets = Passage.getArretsPassages(passages);
                    listeHorairesPassages = Passage.getHorairesPassages(passages);
                }
            } else {
                listeHorairesPassages = new ArrayList();
            }

            arretHoraires = new ArrayList<>();
            for (int i = 0; i < listeHorairesPassages.size(); i++) {
                arretHoraires.add(new ArretHoraire(listeLibellesArrets.get(i),
                        listeHorairesPassages.get(i)));
            }
            ArretHoraireAdapteur arretHoraireAdapteur = new ArretHoraireAdapteur(arretHoraires);
            recyclerView.setAdapter(arretHoraireAdapteur);



        }
    }
        @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    @Override
    public void onClick(View view) {

        dialog.setContentView(R.layout.popup_carte_ligne);
        if (view.getId() == R.id.afficher_carte) {
            popup = new PopupWindow(this);

            // Inflater le contenu de la popup à partir du fichier XML
            View popupView = getLayoutInflater().inflate(R.layout.popup_carte_ligne, null);

            // Obtenir une référence à l'ImageView
            ImageView popupImage = popupView.findViewById(R.id.carte_ligne);

            // Définir l'image de l'ImageView
            popupImage.setImageResource(R.drawable.exemple_carte_ligne);

            // Ajouter le contenu de la popup à la PopupWindow
            popup.setContentView(popupView);

            // Définir les dimensions de la popup
            popup.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
            popup.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

            // Activer le focusable pour la popup
            popup.setFocusable(true);

            // Afficher la popup
            popup.showAtLocation(view, Gravity.CENTER, 0, 0);
        }else if (view.getId() == R.id.annuler){
            popup.dismiss();
        } else if (view.getId() == trajetPrecedentBouton.getId()
                && indexTrajet > 0) {
            changerTrajet(--indexTrajet);
        } else if (view.getId() == trajetSuivantBouton.getId()
                && indexTrajet < trajets.size() - 1) {
            changerTrajet(++indexTrajet);
        } else if (view.getId() == R.id.inverser_sens_ligne) {
            retour = !retour;
            trajets = trajetDao.findByLigne(ligneCourante.getId());
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
        } else if (view.getId() == trajetPrecedentBouton.getId()
                || view.getId() == trajetSuivantBouton.getId()) {
            Toast.makeText(this, R.string.message_erreur_trajet, Toast.LENGTH_SHORT).show();
        }
    }

    private void changerTrajet(int index) {
        passages = trajets.get(index).getPremierPassage().getPassages();
        listeLibellesArrets = Passage.getArretsPassages(passages);
        listeHorairesPassages = Passage.getHorairesPassages(passages);

        arretHoraires = new ArrayList<>();
        for (int i = 0; i < listeHorairesPassages.size(); i++) {
            arretHoraires.add(new ArretHoraire(listeLibellesArrets.get(i),
                    listeHorairesPassages.get(i)));
        }

        adaptateur = new ArretHoraireAdapteur(arretHoraires);
        recyclerView.setAdapter(adaptateur);
    }
}
