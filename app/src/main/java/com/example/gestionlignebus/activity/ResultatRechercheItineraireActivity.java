package com.example.gestionlignebus.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.adapter.ItineraireAdapter;
import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.PeriodeDAO;
import com.example.gestionlignebus.dao.TrajetDAO;
import com.example.gestionlignebus.fragment.FragmentItineraire;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.model.Trajet;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ResultatRechercheItineraireActivity  extends AppCompatActivity
        implements View.OnClickListener {

    private RecyclerView itineraires;
    private Button boutonRetour;
    private List<List<Passage>> passages;
    private List<List<String>> lignes;
    private Periode periodeSelectionnee;
    private boolean autoriserCorrespondance;
    private ArretDAO arretDAO;
    private TrajetDAO trajetDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ItineraireAdapter adapter;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resultat_recherche_itineraire);

        initialiserDao();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Récupération de la liste des itinéraires
        itineraires = this.findViewById(R.id.itineraires);

        PeriodeDAO periodeDAO = new PeriodeDAO(this);
        periodeDAO.open();

        periodeSelectionnee = periodeDAO.findById(
                preferences.getLong(FragmentItineraire.CLE_PERIODE, -1));

        autoriserCorrespondance = preferences.getBoolean(
                FragmentItineraire.CLE_AUTORISE_CORRESPONDANCE, false);

        // Initialisation de la liste des itinéraires
        passages = new ArrayList<>();
        lignes = new ArrayList<>();
        rechercherItineraires(preferences);

        adapter = new ItineraireAdapter(passages, lignes);
        itineraires.setAdapter(adapter);
        itineraires.setLayoutManager(new LinearLayoutManager(this));

        // Initialisation du bouton de retour
        boutonRetour = this.findViewById(R.id.btn_retour);
        boutonRetour.setOnClickListener(this);
    }

    private void initialiserDao() {
        arretDAO = new ArretDAO(this);
        arretDAO.open();

        trajetDAO =new TrajetDAO(this);
        trajetDAO.open();
    }

    private void rechercherItineraires(SharedPreferences preferences) {
        TextView messageErreur;
        String libelleArretDepart
                = preferences.getString(FragmentItineraire.CLE_ARRET_DEPART, "");
        String libelleArretArrive
                = preferences.getString(FragmentItineraire.CLE_ARRET_ARRIVE, "");
        String horaireDepart
                = preferences.getString(FragmentItineraire.CLE_HORAIRE_DEPART, "");
        String horaireArrive
                = preferences.getString(FragmentItineraire.CLE_HORAIRE_ARRIVE, "");

        try {
            Passage passageDepart = creerPassage(libelleArretDepart, horaireDepart);
            Passage passageArrive = creerPassage(libelleArretArrive, horaireArrive);

            rechercheItineraire(passageDepart, passageArrive);
        } catch (Exception e) {
            // empty body
        }

        if (passages.isEmpty()) {
            messageErreur = this.findViewById(R.id.message_erreur);
            messageErreur.setVisibility(View.VISIBLE);
            messageErreur.setText(R.string.message_aucun_itineraire);

            itineraires.setVisibility(View.INVISIBLE);
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

                Passage arriveTmp = Passage.getPassageByArret(listePassages, arrive.getArret());

                if (passage.arretIdentique(depart) && arriveTmp != null
                        && passage.getHoraire().compareTo(depart.getHoraire()) >= 0
                        && (arriveTmp.getHoraire().isBefore(arrive.getHoraire())
                        || arriveTmp.getHoraire().equals(arrive.getHoraire())) ) {
                    arriveTmp = listePassages.get(listePassages.indexOf(arriveTmp));

                    itineraire.add(passage);
                    itineraire.add(arriveTmp);
                    passages.add(itineraire);

                    List<String> ligneItineraire = new ArrayList<>();
                    ligneItineraire.add(trajet.getLigne().getLibelle());
                    ligneItineraire.add(trajet.getLigne().getLibelle());
                    lignes.add(ligneItineraire);
                } else if (passage.arretIdentique(depart)
                        && passage.getHoraire().compareTo(depart.getHoraire()) >= 0
                        && autoriserCorrespondance) {
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
                        && correspondanceDepart.getPassages().contains(arrive)
                        && (correspondanceDepart.getHoraire().isBefore(arrive.getHoraire())
                        || correspondanceDepart.getHoraire().equals(arrive.getHoraire()))) {
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

    @Override
    public void onClick(View view) {
        if (view.getId() == boutonRetour.getId()) {
            finish();
        }
    }
}