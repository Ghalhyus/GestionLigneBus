package com.example.gestionlignebus.fragment;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.gestionlignebus.MainActivity;
import com.example.gestionlignebus.R;
import com.example.gestionlignebus.activity.ResultatRechercheItineraireActivity;
import com.example.gestionlignebus.adapter.GroupeSpinnerAdapter;
import com.example.gestionlignebus.adapter.ListViewAdapter;
import com.example.gestionlignebus.adapter.PeriodeSpinnerAdapter;
import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.GroupeDAO;
import com.example.gestionlignebus.dao.PeriodeDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;
import com.example.gestionlignebus.model.Periode;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FragmentItineraire extends Fragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public static final String CLE_ARRET_DEPART = "arret_depart";
    public static final String CLE_ARRET_ARRIVE= "arret_arrive";
    public static final String CLE_HORAIRE_DEPART = "horaire_depart";
    public static final String CLE_HORAIRE_ARRIVE= "horaire_arrive";
    public static final String CLE_AUTORISE_CORRESPONDANCE = "autorise_correspondance";
    public static final String CLE_PERIODE = "periode";

    private Button rechercher;
    private Button inverserItineraire;
    private CheckBox autoriserCorrespondance;
    private Periode periodeSelectionnee;
    private AutoCompleteTextView saisieArretDepart;
    private AutoCompleteTextView saisieArretArrive;
    private EditText saisieHoraireDepart;
    private EditText saisieHoraireArrive;
    private Spinner spinnerPeriode;
    private Spinner spinnerGroupe;
    private List<Periode> periodes;
    private List<Groupe> groupes;
    private Groupe groupeSelectionne;
    private ArretDAO arretDAO;

    /**
     * Retourne une instance de FragmentDepense
     * @return l'instance
     */
    public static FragmentItineraire newInstance() {
        return new FragmentItineraire();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragment = layoutInflater
                .inflate(R.layout.fragment_itineraire, container, false);

        initialiserDao(fragment);

        recuperationDesComposants(fragment);

        initialisationDesComposants(fragment);

        return fragment;
    }

    /**
     * Initialisation de tous les DAO utilisés dans le fragment.
     * @param fragment Fragment pour la recherche d'un itinéraire.
     */
    private void initialiserDao(View fragment) {
        arretDAO = new ArretDAO(fragment.getContext());
        arretDAO.open();
    }

    /**
     * Récupération des composants de la vue pour la recherche d'un itinéraire.
     * @param fragment Fragment pour la recherche d'un itinéraire.
     */
    private void recuperationDesComposants(View fragment) {
        // Récupération des composants pour la saisie
        saisieArretDepart = fragment.findViewById(R.id.itineraire_arret_depart);
        saisieArretArrive = fragment.findViewById(R.id.itineraire_arret_arrive);
        saisieHoraireDepart = fragment.findViewById(R.id.itineraire_horaire_depart);
        saisieHoraireArrive = fragment.findViewById(R.id.itineraire_horaire_arrive);

        //Récupération des boutons
        rechercher = fragment.findViewById(R.id.bouton_rechercher);
        inverserItineraire = fragment.findViewById(R.id.itineraire_inversion);

        // Récupération des spinners
        spinnerPeriode = fragment.findViewById(R.id.spinner_periode);
        spinnerGroupe = fragment.findViewById(R.id.itineraire_groupe_liste);

        // Récupération de la checkbox pour autoriser les correspondances
        autoriserCorrespondance = fragment.findViewById(R.id.autoriser_correspondances);
    }

    /**
     * Initialisation des composants du fragment itinéraire.
     * @param fragment Fragment pour la recherche d'un itinéraire.
     */
    private void initialisationDesComposants(View fragment) {
        // Initialisation des boutons
        rechercher.setOnClickListener(this::onClick);
        inverserItineraire.setOnClickListener(this::onClick);

        // Initialisation du spinner des périodes
        PeriodeDAO periodeDAO = new PeriodeDAO(fragment.getContext());
        periodeDAO.open();

        periodes = periodeDAO.findAll();
        if (!periodes.isEmpty()) {
            periodeSelectionnee = periodes.get(0);
        }

        PeriodeSpinnerAdapter periodeAdapter
                = new PeriodeSpinnerAdapter(fragment.getContext(), periodes);
        spinnerPeriode.setAdapter(periodeAdapter);
        spinnerPeriode.setOnItemSelectedListener(this);

        // Initialisation du spinner des groupes
        GroupeDAO groupeDAO = new GroupeDAO(fragment.getContext());
        groupeDAO.open();

        groupes = new ArrayList<>();
        groupes.add(new Groupe());
        groupes.addAll(groupeDAO.findAll());

        if (!groupes.isEmpty() ) {
            groupeSelectionne = groupes.get(0);
        }

        GroupeSpinnerAdapter groupeAdapter
                = new GroupeSpinnerAdapter(fragment.getContext(), groupes);
        spinnerGroupe.setAdapter(groupeAdapter);
        spinnerGroupe.setOnItemSelectedListener(this);

        // Initialisation de l'autocomplétion
        setAutocompletion(getContext());

        // Saisie horaire
        saisieHoraireDepart.setOnClickListener(this);
        saisieHoraireArrive.setOnClickListener(this);

    }

    private void showTimePicker(EditText view){
        int hour = LocalTime.now().getHour();
        int minute =LocalTime.now().getMinute();

        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getContext(),
                (timePicker, selectedHour, selectedMinute) -> {
            LocalTime localTime = LocalTime.of(selectedHour, selectedMinute);
            view.setText(localTime.toString());
        }, hour, minute, true);

        timePickerDialog.show();
    }

    @Override//item du spinner
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
        if (adapterView.getId() == spinnerPeriode.getId()) {
            periodeSelectionnee = periodes.get(index);
        } else if (adapterView.getId() == spinnerGroupe.getId()) {
            groupeSelectionne = groupes.get(index);
            setAutocompletion(getContext());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Empty body
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == rechercher.getId()) {
            LocalTime horaireDepart = LocalTime.parse(saisieHoraireDepart.getText().toString());
            LocalTime horaireArrive = LocalTime.parse(saisieHoraireArrive.getText().toString());
            if (horaireDepart.isBefore(horaireArrive)) {
                SharedPreferences.Editor editeur = null;
                try {
                    String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

                    editeur =  EncryptedSharedPreferences.create(
                            "secret",
                            masterKey,
                            getContext(),
                            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM).edit();
                } catch (GeneralSecurityException e) {
                    Log.e(MainActivity.CLE_LOG,
                            "Erreur de sécurité lors la génération de la master Keys");
                } catch (IOException e) {
                    Log.e(MainActivity.CLE_LOG,
                            "Erreur fichier introuvable pour la génération de la master Keys");
                }

                editeur.putString(CLE_ARRET_DEPART, saisieArretDepart.getText().toString());
                editeur.putString(CLE_ARRET_ARRIVE, saisieArretArrive.getText().toString());
                editeur.putString(CLE_HORAIRE_DEPART, saisieHoraireDepart.getText().toString());
                editeur.putString(CLE_HORAIRE_ARRIVE, saisieHoraireArrive.getText().toString());
                editeur.putLong(CLE_PERIODE, periodeSelectionnee.getId());
                editeur.putBoolean(CLE_AUTORISE_CORRESPONDANCE,
                        autoriserCorrespondance.isChecked());
                editeur.apply();

                startActivity(new Intent(getContext(), ResultatRechercheItineraireActivity.class));
            } else {
                Toast.makeText(
                        this.getContext(),
                        R.string.erreur_horaires_desordonne,
                        Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == inverserItineraire.getId()) {
            String tmp = saisieArretDepart.getText().toString();
            saisieArretDepart.setText(saisieArretArrive.getText().toString());
            saisieArretArrive.setText(tmp);
        } else if (view.getId() == R.id.itineraire_horaire_depart) {
            showTimePicker(saisieHoraireDepart);
        } else if (view.getId() == R.id.itineraire_horaire_arrive) {
            showTimePicker(saisieHoraireArrive);
        }
    }

    private void setAutocompletion(Context context) {
        List<String> libellesArrets = Arret.getLibellesArrets(
                arretDAO.findByGroupe(groupeSelectionne));
        ListViewAdapter listViewAdapter = new ListViewAdapter(context,
                android.R.layout.simple_list_item_1, libellesArrets);
        saisieArretDepart.setAdapter(listViewAdapter);
        saisieArretArrive.setAdapter(listViewAdapter);
    }
}