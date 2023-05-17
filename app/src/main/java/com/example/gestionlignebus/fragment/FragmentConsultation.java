package com.example.gestionlignebus.fragment;

import static java.util.Arrays.asList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.fragment.app.Fragment;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.activity.ArretActivity;
import com.example.gestionlignebus.activity.LigneActivity;
import com.example.gestionlignebus.adapter.GroupeSpinnerAdapter;
import com.example.gestionlignebus.adapter.ListViewAdapter;
import com.example.gestionlignebus.adapter.PeriodeSpinnerAdapter;
import com.example.gestionlignebus.adapter.SpinnerAdapter;
import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.GroupeDAO;
import com.example.gestionlignebus.dao.LigneDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;
import com.example.gestionlignebus.model.Ligne;

import java.util.List;

public class FragmentConsultation extends Fragment
        implements AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener {

    private static final String LIGNE = "Ligne";
    private static final String ARRET = "Arrêt";

    private Spinner spin;
    private AutoCompleteTextView search;

    private ListView listeRecherche;
    private ListViewAdapter adapterlist;

    private LigneDAO ligneDAO;
    private ArretDAO arretDAO;
    private GroupeDAO groupeDAO;

    private List<String> listeLibelles;
    private List<Arret> listeArret;
    private List<Ligne> listeLigne;
    private Groupe groupeSelectionne;
    private boolean arretsAffiches;

    private SharedPreferences preferences;


    public static FragmentConsultation newInstance() {
        return new FragmentConsultation();
    }


    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater
                .inflate(R.layout.fragment_consultation, container, false);
        View fragmentConsultationView;
        fragmentConsultationView = layoutInflater.inflate(R.layout.fragment_groupe,
                container, false);



        initialisationDao();
        listeArret=arretDAO.findAll();
        listeLigne=ligneDAO.findAll();

        initialisationSpinner(view);

        initialisationListeRecherche(view);

        arretsAffiches = false;

        preferences = PreferenceManager.getDefaultSharedPreferences(fragmentConsultationView.getContext());


        return view;
    }

    /**
     * Methode initialisant la liste recherche
     * @param view
     */
    private void initialisationListeRecherche(View view) {
        listeRecherche = view.findViewById(R.id.recherche_list);
        listeLibelles = Ligne.getLibellesLignes(ligneDAO.findAll());

        adapterlist = new ListViewAdapter(this.getContext(),
                android.R.layout.simple_list_item_1, listeLibelles);

        search = view.findViewById(R.id.barre_recherche);
        search.setAdapter(adapterlist);
        listeRecherche.setAdapter(adapterlist);
        listeRecherche.setOnItemClickListener(this);
        registerForContextMenu(listeRecherche);
    }

    /**
     *
     * @param view
     */
    private void initialisationSpinner(View view) {
        spin = view.findViewById(R.id.liste_deroulant_recherche);
        List<String> options = asList(LIGNE, ARRET);
        SpinnerAdapter adapter = new SpinnerAdapter(this.getContext(), options);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);
    }

    private void initialisationDao() {
        arretDAO = new ArretDAO(this.getContext());
        arretDAO.open();

        ligneDAO = new LigneDAO(this.getContext());
        ligneDAO.open();

        groupeDAO = new GroupeDAO(this.getContext());
        groupeDAO.open();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View fragmentGroupe,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        if (arretsAffiches) {
            new MenuInflater(fragmentGroupe.getContext())
                    .inflate(R.menu.menu_contextuel_recherche_arret, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo information =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if(item.getItemId() == R.id.ajouter_groupe) {

            Arret arretSelectionne = arretDAO.findAll().get(information.position);

            AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());

            final View customLayout = createSpinner();
            builder.setView(customLayout);

            builder.setTitle(R.string.titre_ajouter_arret);

            builder.setPositiveButton(R.string.bouton_valider, (dialog, which) -> {
                groupeSelectionne = groupeSelectionne == null
                        ? groupeDAO.findAll().get(0) : groupeSelectionne;
                groupeDAO.ajouterArret(groupeSelectionne, arretSelectionne);
            });

            builder.setNegativeButton(R.string.annuler, null);

            builder.show();
        }

        return (super.onContextItemSelected(item));
    }

    private View createSpinner() {
        View customLayout = getLayoutInflater().inflate(R.layout.popup_liste,
                null);

        Spinner spinner = customLayout.findViewById(R.id.spinner_popup_liste);
        List<Groupe> groupes = groupeDAO.findAll();
        GroupeSpinnerAdapter spinnerAdapter
                = new GroupeSpinnerAdapter(this.getContext(), groupes);

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        return customLayout;
    }

    @Override//item de la liste
    public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
        if(spin.getSelectedItem().equals(LIGNE)) {
            SharedPreferences.Editor editeur = preferences.edit();
            editeur.putLong(LigneActivity.CLE_LIGNE, listeLigne.get(index).getId());
            editeur.apply();
            startActivity(new Intent(view.getContext(), LigneActivity.class));
        }else{
            SharedPreferences.Editor editeur = preferences.edit();
            editeur.putLong(ArretActivity.CLE_ID, listeArret.get(index).getId());
            editeur.apply();
            startActivity(new Intent(view.getContext(), ArretActivity.class));
        }
    }

    @Override//item du spinner
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
        changerTypeRecherche();
        List<Groupe> groupes = groupeDAO.findAll();
        if (groupes.isEmpty() && index < groupes.size()) {
            groupeSelectionne = groupes.get(index);
        }
    }

    /**
     * Change le type de le recherche.
     * Soit on recherche des lignes, soit on recherches des arrêts.
     */
    private void changerTypeRecherche() {
        if(spin.getSelectedItem().equals(LIGNE)) {
            arretsAffiches = false;
            search.setText("");

            listeLibelles = Ligne.getLibellesLignes(ligneDAO.findAll());

            adapterlist = new ListViewAdapter(
                    this.getContext(), android.R.layout.simple_list_item_1, listeLibelles);
            listeRecherche.setAdapter(adapterlist);
            search.setAdapter(adapterlist);
        } else {
            arretsAffiches = true;
            search.setText("");

            listeLibelles = Arret.getLibellesArrets(arretDAO.findAll());

            adapterlist = new ListViewAdapter(
                    this.getContext(),
                    android.R.layout.simple_list_item_1,
                    listeLibelles);
            search.setAdapter(adapterlist);
            listeRecherche.setAdapter(adapterlist);
        }
    }

    public void ajoutArret(Arret arretSelectionne, DialogInterface dialog, int which) {
        Groupe groupe = this.groupeSelectionne;

        if (groupe == null) {
            groupe = this.groupeDAO.findAll().get(0);
        }

        this.groupeSelectionne = groupe;
        this.groupeDAO.ajouterArret(groupe, arretSelectionne);
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Nothing
    }
}
