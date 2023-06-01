package com.example.gestionlignebus.activity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.adapter.ArretSpinnerAdapter;
import com.example.gestionlignebus.adapter.ListViewAdapter;
import com.example.gestionlignebus.dao.ArretDAO;
import com.example.gestionlignebus.dao.GroupeDAO;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Groupe;

import java.util.ArrayList;
import java.util.List;

public class ArretsGroupeActivity  extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    public static final String CLE_ID = "id_groupe";


    private Button retour;


    private List<String> listeNomsArrets;
    private ListViewAdapter adapter;

    private GroupeDAO groupeDao;
    private ArretDAO arretDao;
    private Groupe groupe;
    private List<Arret> arretsGroupe;
    private Arret arretSelectionne;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences;
        ListView listeArretsView;
        TextView titreListe;
        Button ajoutArret;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groupe_arret);

        arretDao = new ArretDAO(this);
        arretDao.open();
        groupeDao = new GroupeDAO(this);
        groupeDao.open();

        ajoutArret = findViewById(R.id.ajouter_arret_button);
        retour = findViewById(R.id.retour_groupes_button);
        ajoutArret.setOnClickListener(this);
        retour.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        groupe = groupeDao.findById(preferences.getLong(CLE_ID, 1));
        arretsGroupe = groupe.getArrets() == null ? new ArrayList<>() : groupe.getArrets();

        titreListe = findViewById(R.id.titre_liste_arrets_groupe);
        titreListe.setText(groupe.getLibelle());

        listeNomsArrets = Arret.getLibellesArrets(arretsGroupe);
        adapter = new ListViewAdapter(this,
                android.R.layout.simple_list_item_1,
                listeNomsArrets);
        listeArretsView = findViewById(R.id.arrets_groupe_liste);
        listeArretsView.setAdapter(adapter);
        registerForContextMenu(listeArretsView);
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View fragmentGroupe,
                                    ContextMenu.ContextMenuInfo menuInfo) {
            new MenuInflater(fragmentGroupe.getContext())
                    .inflate(R.menu.menu_contextuel_groupe_arret, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo information =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getItemId() == R.id.retirer_arret){
            Arret arretASupp = groupe.getArrets().get(information.position);

            AlertDialog.Builder builder = new AlertDialog.Builder(information.targetView.getContext());

            builder.setMessage(String.format(getString(R.string.message_supprimer_arret_groupe),
                    arretASupp.getLibelle()));

            builder.setTitle(R.string.titre_retirer_arret_groupe);

            builder.setPositiveButton(R.string.btn_valider, (dialog, which) -> {
                groupe = groupeDao.retirerArret(groupe, arretASupp);
                nettoyerListe();
            });

            builder.setNegativeButton(R.string.annuler, null);

            builder.show();
        }

        return (super.onContextItemSelected(item));
    }
    @Override
    public void onClick(View view) {
        if (view.getId() == retour.getId()) {
            finish();
        } else {

            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

            final View customLayout = createSpinner();
            builder.setView(customLayout);

            builder.setTitle(R.string.titre_ajouter_arret);

            builder.setPositiveButton(R.string.btn_valider, (dialog, which) -> {
                groupe = groupeDao.ajouterArret(groupe, arretSelectionne);
                nettoyerListe();
            });

            builder.setNegativeButton(R.string.annuler, null);

            builder.show();
        }
    }

    /**
     * Créer le spinner contenant la liste des arrêts en base de données.
     * @return Le spinner créé.
     */
    private View createSpinner() {
        View customLayout = getLayoutInflater().inflate(R.layout.popup_liste,
                null);

        Spinner spinner = customLayout.findViewById(R.id.spinner_popup_liste);
        List<Arret> arrets = arretDao.findAll();
        ArretSpinnerAdapter spinnerAdapter
                = new ArretSpinnerAdapter(this, arrets);

        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        return customLayout;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int index, long l) {
        arretSelectionne = arretDao.findAll().get(index);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // non utilisé
    }

    public void nettoyerListe() {
        adapter.clear();
        arretsGroupe = groupe.getArrets();
        listeNomsArrets = Arret.getLibellesArrets(arretsGroupe);
        adapter.addAll(listeNomsArrets);
    }
}
