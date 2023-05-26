package com.example.gestionlignebus.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.adapter.GroupeSpinnerAdapter;
import com.example.gestionlignebus.adapter.ItineraireAdapter;
import com.example.gestionlignebus.adapter.ListViewAdapter;
import com.example.gestionlignebus.adapter.PeriodeSpinnerAdapter;
import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.GroupeDAO;
import com.example.gestionlignebus.dao.LigneDAO;
import com.example.gestionlignebus.dao.PassageDAO;
import com.example.gestionlignebus.dao.PeriodeDAO;
import com.example.gestionlignebus.dao.TrajetDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FragmentItineraire extends Fragment
        implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Button rechercher;
    private Button inverserItineraire;
    private CheckBox autoriserCorrespondance;
    private AutoCompleteTextView saisieArretDepart;
    private AutoCompleteTextView saisieArretArrive;
    private EditText saisieHoraireDepart;
    private EditText saisieHoraireArrive;
    private RecyclerView itineraires;
    private ItineraireAdapter adapter;
    private Spinner spinnerPeriode;
    private Spinner spinnerGroupe;
    private List<Periode> periodes;
    private List<Groupe> groupes;
    private List<List<Passage>> passages;
    private List<List<String>> lignes;
    private Periode periodeSelectionnee;
    private Groupe groupeSelectionne;
    private PassageDAO passageDAO;
    private ArretDAO arretDAO;
    private LigneDAO ligneDAO;
    private TrajetDAO trajetDAO;

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

        passageDAO = new PassageDAO(fragment.getContext());
        passageDAO.open();

        arretDAO = new ArretDAO(fragment.getContext());
        arretDAO.open();

        ligneDAO = new LigneDAO(fragment.getContext());
        ligneDAO.open();

        trajetDAO =new TrajetDAO(fragment.getContext());
        trajetDAO.open();
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

        // Récupération de la liste des itinéraires
        itineraires = fragment.findViewById(R.id.itineraires);

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

        // Initialisation de la liste des itinéraires
        passages = new ArrayList<>();
        itineraires.setLayoutManager(new LinearLayoutManager(fragment.getContext()));
        adapter = new ItineraireAdapter(passages, new ArrayList<>());
        itineraires.setAdapter(adapter);

        // Initialisation du spinner des périodes
        PeriodeDAO periodeDAO = new PeriodeDAO(fragment.getContext());
        periodeDAO.open();

        periodes = periodeDAO.findAll();
        periodeSelectionnee = periodes.get(0);

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
        groupeSelectionne = groupes.get(0);

        GroupeSpinnerAdapter groupeAdapter
                = new GroupeSpinnerAdapter(fragment.getContext(), groupes);
        spinnerGroupe.setAdapter(groupeAdapter);
        spinnerGroupe.setOnItemSelectedListener(this);

        // Initialisation de l'autocomplétion
        setAutocompletion(getContext());
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
            Passage passageDepart = creerPassage(saisieArretDepart.getText().toString(),
                    saisieHoraireDepart.getText().toString());
            Passage passageArrive = creerPassage(saisieArretArrive.getText().toString(),
                    saisieHoraireArrive.getText().toString());

            rechercheItineraire(passageDepart, passageArrive);

            adapter = new ItineraireAdapter(passages, lignes);
            itineraires.setAdapter(adapter);
        } else if (view.getId() == inverserItineraire.getId()) {
            String tmp = saisieArretDepart.getText().toString();
            saisieArretDepart.setText(saisieArretArrive.getText().toString());
            saisieArretArrive.setText(tmp);
        }
    }

    private Passage creerPassage(String libelleArret, String horaire) {
        Passage passage = new Passage();

        passage.setArret(arretDAO.findByLibelle(libelleArret));
        passage.setHoraire(LocalTime.parse(horaire));

        return passage;
    }

    private void rechercheItineraire(Passage depart, Passage arrive) {
        passages = new ArrayList<>();
        lignes = new ArrayList<>();
        List<Trajet> trajets = trajetDAO.findByPeriode(periodeSelectionnee);

        for (Trajet trajet : trajets) {
            List<Passage> listePassages = trajet.getPremierPassage().getPassages();

            for (Passage passage : listePassages) {
                List<Passage> itineraire = new ArrayList<>();

                arrive = Passage.getPassageByArret(listePassages, arrive.getArret());

                if (passage.arretIdentique(depart) && arrive != null
                    && passage.getHoraire().compareTo(depart.getHoraire()) <= 0) {
                    arrive = listePassages.get(listePassages.indexOf(arrive));

                    itineraire.add(passage);
                    itineraire.add(arrive);
                    passages.add(itineraire);

                    List<String> ligneItineraire = new ArrayList<>();
                    ligneItineraire.add(trajet.getLigne().getLibelle());
                    ligneItineraire.add(trajet.getLigne().getLibelle());
                    lignes.add(ligneItineraire);
                } else if (passage.arretIdentique(depart)
                        && passage.getHoraire().compareTo(depart.getHoraire()) <= 0
                        && autoriserCorrespondance.isChecked()) {
                    Trajet trajetArrive = rechercherTrajetArrive(arrive);

                    if (trajetArrive != null) {
                        rechercherCorrespondance(passage, arrive, itineraire, trajet, trajetArrive);
                    }
                }
            }
        }
    }

    private void rechercherCorrespondance(Passage depart, Passage arrive, List<Passage> itineraire,
                                          Trajet trajet, Trajet trajetArrive) {
        itineraire.add(depart);

        List<String> ligneItineraire = new ArrayList<>();
        ligneItineraire.add(trajet.getLigne().getLibelle());

        List<Passage> passagesArrive
                = trajetArrive.getPremierPassage().getPassages();
        for (Passage passage : passagesArrive) {
            if (passage.arretIdentique(arrive)
                    && passage.getHoraire().compareTo(arrive.getHoraire()) <= 0) {
                arrive = passage;
            }
        }

        List<Trajet> trajets = trajetDAO.findAll();
        trajets.remove(trajets.indexOf(trajet));

        itineraire = rechercherCorrespondance(depart, itineraire, ligneItineraire, arrive,
                trajet, trajets);

        if (itineraire.isEmpty()) {
            lignes = new ArrayList<>();
        }

        passages.add(itineraire);
        lignes.add(ligneItineraire);
    }

    private List<Passage> rechercherCorrespondance(Passage depart, List<Passage> correspondances,
                                                   List<String> lignes, Passage arrive,
                                                   Trajet trajetDepart, List<Trajet> trajets) {
        Passage correspondanceArrive;
        Passage correspondanceDepart = null;

        if (trajets.isEmpty() || depart == null) {
            return new ArrayList<>();
        }

        for (int i = 0 ; i < trajets.size() && correspondanceDepart == null ; i++) {
            List<Passage> listePassages = trajets.get(i).getPremierPassage().getPassages();

            if (trajets.get(i).getPeriode().equals(periodeSelectionnee)) {
                correspondanceArrive = depart.getCorrespondanceArrivee(listePassages);

                if (correspondanceArrive != null) {
                    correspondanceDepart = correspondanceArrive.getCorrespondanceDepart(
                            trajets.get(i).getPremierPassage().getPassages());

                    correspondances.add(correspondanceArrive);
                    correspondances.add(correspondanceDepart);

                    lignes.add(trajetDepart.getLigne().getLibelle());
                    lignes.add(trajets.get(i).getLigne().getLibelle());

                    trajetDepart = trajets.get(i);
                    trajets.remove(i);
                }

                if (correspondanceDepart != null
                    && correspondanceDepart.getPassages().contains(arrive)) {
                    correspondances.add(arrive);
                    lignes.add(trajetDepart.getLigne().getLibelle());

                    return  correspondances;
                }
            }
        }

        return rechercherCorrespondance(correspondanceDepart, correspondances, lignes, arrive,
                trajetDepart, trajets);
    }

    private Trajet rechercherTrajetArrive(Passage arrive) {
        for (Trajet trajet : trajetDAO.findAll()) {
            List<Passage> listePassages = trajet.getPremierPassage().getPassages();

            for (Passage passage : listePassages) {
                if (passage.arretIdentique(arrive)
                    && passage.getHoraire().compareTo(arrive.getHoraire()) <= 0
                    && trajet.getPeriode().equals(periodeSelectionnee)) {
                    return trajet;
                }
            }
        }

        return null;
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