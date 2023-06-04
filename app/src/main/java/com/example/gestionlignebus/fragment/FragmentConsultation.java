package com.example.gestionlignebus.fragment;

import static android.content.Context.LOCALE_SERVICE;
import static com.example.gestionlignebus.MainActivity.CLE_LOG;
import static java.util.Arrays.asList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.gestionlignebus.MainActivity;
import com.example.gestionlignebus.R;
import com.example.gestionlignebus.activity.ArretActivity;
import com.example.gestionlignebus.activity.ChangerFicheHoraireActivity;
import com.example.gestionlignebus.activity.LigneActivity;
import com.example.gestionlignebus.adapter.GroupeSpinnerAdapter;
import com.example.gestionlignebus.adapter.ListViewAdapter;
import com.example.gestionlignebus.adapter.SpinnerAdapter;
import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.GroupeDAO;
import com.example.gestionlignebus.dao.LigneDAO;
import com.example.gestionlignebus.dao.PassageDAO;
import com.example.gestionlignebus.dao.PeriodeDAO;
import com.example.gestionlignebus.dao.TrajetDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;
import com.example.gestionlignebus.model.Ligne;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.model.Periode;
import com.example.gestionlignebus.utils.JSONUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Objects;

public class FragmentConsultation extends Fragment
        implements AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener {

    private static final String LIGNE = "Ligne";
    private static final String ARRET = "Arrêt";
    private static final int PICK_FILE_STABLE = 1;


    private Spinner spin;
    private AutoCompleteTextView search;

    private ListView listeRecherche;
    private ListViewAdapter adapterlist;

    private LigneDAO ligneDAO;
    private ArretDAO arretDAO;
    private PeriodeDAO periodeDAO;
    private GroupeDAO groupeDAO;
    private PassageDAO passageDAO;
    private TrajetDAO trajetDAO;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater
                .inflate(R.layout.fragment_consultation, container, false);

        initialisationDao();
        listeArret = arretDAO.findAll();
        listeLigne = ligneDAO.findAll();

        initialisationSpinner(view);

        initialisationListeRecherche(view);

        arretsAffiches = false;

        try {
            String masterKey = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            preferences =  EncryptedSharedPreferences.create(
                    "secret",
                    masterKey,
                    getContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM);
        } catch (GeneralSecurityException e) {
            Log.e(MainActivity.CLE_LOG,
                    "Erreur de sécurité lors la génération de la master Keys");
        } catch (IOException e) {
            Log.e(MainActivity.CLE_LOG,
                    "Erreur fichier introuvable pour la génération de la master Keys");
        }

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
     * Méthode initialisant le spinner du type de recherche
     * @param view
     */
    private void initialisationSpinner(View view) {
        spin = view.findViewById(R.id.liste_deroulant_recherche);
        List<String> options = asList(LIGNE, ARRET);
        SpinnerAdapter adapter = new SpinnerAdapter(this.getContext(), options);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);
    }

    /**
     * Initialisation des différents DAO
     */
    private void initialisationDao() {
        arretDAO = new ArretDAO(this.getContext());
        arretDAO.open();

        ligneDAO = new LigneDAO(this.getContext());
        ligneDAO.open();

        groupeDAO = new GroupeDAO(this.getContext());
        groupeDAO.open();

        periodeDAO = new PeriodeDAO(this.getContext());
        periodeDAO.open();

        passageDAO = new PassageDAO(this.getContext());
        passageDAO.open();

        trajetDAO = new TrajetDAO(this.getContext());
        trajetDAO.open();
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

            builder.setPositiveButton(R.string.btn_valider, (dialog, which) -> {
                groupeSelectionne = groupeSelectionne == null
                        ? groupeDAO.findAll().get(0) : groupeSelectionne;

                if (arretDAO.findByGroupe(groupeSelectionne).contains(arretSelectionne)) {
                    Toast.makeText(this.getContext(), R.string.erreur_ajout_arret,
                            Toast.LENGTH_SHORT).show();
                } else {
                    groupeDAO.ajouterArret(groupeSelectionne, arretSelectionne);
                }
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

            listeLigne = ligneDAO.findAll();
            listeLibelles = Ligne.getLibellesLignes(listeLigne);

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

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // Nothing
    }

    // Menu optionnel
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.menu_optionnel_base, menu);
        super.onCreateOptionsMenu(menu, menuInflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.importer_lignes:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                String[] mimetypes = {"application/json"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, PICK_FILE_STABLE);
                break;
            case R.id.importation_horaire:
                Intent intent2 = new Intent(this.getContext(), ChangerFicheHoraireActivity.class);
                startActivity(intent2);
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
        if (codeRequete == PICK_FILE_STABLE &&
                codeResultat ==  Activity.RESULT_OK) {
            enregistrerDonneeStable(intent);
            initialisationDao();
            // MAJ Adaptateur
            changerTypeRecherche();
        }
    }

    public void enregistrerDonneeStable(Intent intent) {
        Uri uri = Uri.parse(intent.getDataString());
        try {
            // On ouvre le fichier afin de pouvoir le lire
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // On récupère le contenu du JSON
            String content = JSONUtils.readJSON(bufferedReader);

            String erreurArret = null;
            String erreurPeriode = null;
            String erreurLigne = null;

            int nbErreurArret = 0;
            int nbErreurLigne = 0;
            int nbErreurPeriode = 0;

            if (!Objects.equals(content, "")) {
                // On supprime les anciennes données
                arretDAO.clear();
                ligneDAO.clear();
                periodeDAO.clear();
                trajetDAO.clear();
                passageDAO.clear();
                // On convertit les Arret JSON en arrêt java
                List<Arret> arretList = JSONUtils.jsonToArretList(content);
                if (arretList != null) {
                    // On enregistre les arrêts du fichier
                    for (Arret arret : arretList) {
                        Arret arretSaved = arretDAO.save(arret);
                        if (arretSaved == null) {
                            nbErreurArret++;
                        }
                    }
                    if (nbErreurArret > 0 ) {
                        erreurArret = String.format(getString(R.string.erreur_nb_arret), nbErreurArret);
                    }
                } else {
                    // Erreur lors de la lecture des arrêts
                    Toast.makeText(this.getContext(), getString(R.string.erreur_import_arret),
                            Toast.LENGTH_LONG).show();
                }

                List<Periode> periodeList = JSONUtils.jsonToPeriodeList(content);

                if ( periodeList != null) {
                    // On supprime les anciennes données
                    // On enregistre les périodes du fichier
                    for (Periode periode : periodeList) {
                        Periode periodeSaved = periodeDAO.save(periode);
                        if (periodeSaved == null) {
                            nbErreurPeriode++;
                        }
                    }
                    if (nbErreurPeriode > 0 ) {
                        erreurPeriode = String.format(getString(R.string.erreur_nb_periode), nbErreurPeriode);
                    }
                } else {
                    // Erreur lors de la lecture des périodes
                    Toast.makeText(this.getContext(), getString(R.string.erreur_import_periode),
                            Toast.LENGTH_LONG).show();
                }

                List<Ligne> ligneList = JSONUtils.jsonToLigneList(content);
                if ( ligneList != null ) {
                    // On supprime les anciennes données
                    // On enregistre les lignes du fichier
                    for (Ligne ligne : ligneList) {
                        Ligne ligneSaved = ligneDAO.save(ligne);
                        if (ligneSaved == null) {
                            nbErreurLigne++;
                        }
                    }
                    if (nbErreurLigne > 0 ) {
                        erreurLigne = String.format(getString(R.string.erreur_nb_ligne), nbErreurLigne);
                    }
                } else {
                    Toast.makeText(this.getContext(), getString(R.string.erreur_import_ligne),
                            Toast.LENGTH_LONG).show();
                }
            } else {
                // Erreur lecture fichier
                Toast.makeText(this.getContext(), getString(R.string.erreur_fichier_vide),
                        Toast.LENGTH_LONG).show();
            }
            // Si tout s'est bien passé, alors on affiche ce message
            if (erreurArret == null
                    && erreurLigne == null
                    && erreurPeriode == null) {
                Toast.makeText(this.getContext(), R.string.reussite_import_donnée_stable, Toast.LENGTH_LONG).show();
            } else {
                StringBuilder messageFinal = new StringBuilder();
                if (erreurArret != null) {
                    String res = String.format(getString(R.string.erreur_nb_arret), nbErreurArret);
                    messageFinal.append(res);
                }
                if (erreurPeriode != null) {
                    if (erreurArret != null) {
                        messageFinal.append("\n");
                    }
                    String res = String.format(getString(R.string.erreur_nb_periode), nbErreurPeriode);
                    messageFinal.append(res);
                }
                if (erreurLigne != null) {
                    if (erreurPeriode != null) {
                        messageFinal.append("\n");
                    }
                    String res = String.format(getString(R.string.erreur_nb_ligne), nbErreurLigne);
                    messageFinal.append(res);
                }
                if (messageFinal.length() > 0) {
                    Toast.makeText(this.getContext(), messageFinal.toString(), Toast.LENGTH_LONG).show();
                }
            }
        } catch (FileNotFoundException e) {
            Log.e(CLE_LOG, String.format("Erreur le fichier %s n'a pas été trouvé.", uri));
        } finally {
            arretDAO.close();
            periodeDAO.close();
            ligneDAO.close();
        }
    }
}
