package com.example.gestionlignebus.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.activity.ArretsGroupeActivity;
import com.example.gestionlignebus.adapter.ListViewAdapter;
import com.example.gestionlignebus.dao.GroupeDAO;
import com.example.gestionlignebus.model.Groupe;
import com.example.gestionlignebus.utils.Preferences;

import java.util.List;

public class FragmentGroupe extends Fragment
        implements AdapterView.OnItemClickListener, View.OnClickListener {


    private ListViewAdapter adapter;
    private List<Groupe> listeGroupes;
    private List<String> listeNomsGroupes;

    private GroupeDAO groupeDao;

    private Preferences preferences;

    public static FragmentGroupe newInstance() {

        return new FragmentGroupe();
    }


    @Override
    public View onCreateView(
            LayoutInflater layoutInflater,
            ViewGroup container,
            Bundle savedInstanceState) {
        Button ajouterGroupe;
        View fragmentGroupeView;
        ListView listeGroupesLayout;
        fragmentGroupeView = layoutInflater.inflate(R.layout.fragment_groupe,
                container, false);

        groupeDao = new GroupeDAO(fragmentGroupeView.getContext());
        groupeDao.open();

        listeGroupes = groupeDao.findAll();
        listeNomsGroupes = Groupe.getLibellesGroupes(listeGroupes);

        listeGroupesLayout = fragmentGroupeView.findViewById(R.id.groupe_liste);

        adapter = new ListViewAdapter(fragmentGroupeView.getContext(),
                android.R.layout.simple_list_item_1,
                listeNomsGroupes);

        listeGroupesLayout.setAdapter(adapter);
        listeGroupesLayout.setOnItemClickListener(this::onItemClick);
        registerForContextMenu(listeGroupesLayout);

        ajouterGroupe = fragmentGroupeView.findViewById(R.id.creer_groupe_button);
        ajouterGroupe.setOnClickListener(this::onClick);

        preferences = Preferences.getPreferences(getContext());

        return fragmentGroupeView;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View fragmentGroupeView, int index, long l) {
        SharedPreferences.Editor editeur = preferences.edit();
        editeur.putLong(ArretsGroupeActivity.CLE_ID, listeGroupes.get(index).getId());
        editeur.apply();

        startActivity(new Intent(adapterView.getContext(),
                ArretsGroupeActivity.class));
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View fragmentGroupeView,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        new MenuInflater(fragmentGroupeView.getContext()).inflate(R.menu.menu_contextuel_groupe, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo information =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        switch(item.getItemId()) {
            case R.id.renommer:
                modifierGroupe(information);
                break;
            case R.id.supprimer:
                supprimerGroupe(information);
                break;
            case R.id.annuler :
                break;
            default:
                break;
        }

        return (super.onContextItemSelected(item));
    }
    @Override
    public void onClick(View fragmentGroupeView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());

        final View customLayout = getLayoutInflater().inflate(R.layout.popup_saisie_groupe,
                null);
        builder.setView(customLayout);

        builder.setTitle(R.string.titre_popup_creer_groupe);

        builder.setPositiveButton(R.string.btn_valider, (dialog, which) ->
            onClickAjouterGroupe(customLayout)
        );

        builder.setNegativeButton(R.string.annuler, null);

        builder.show();
    }

    /**
     * Appel l'activité de la popup pour modifier un groupe.
     * @param information Les informations du groupe sélectionné.
     */
    private void modifierGroupe(AdapterView.AdapterContextMenuInfo information) {
        Groupe groupe = groupeDao.findById(listeGroupes.get(information.position).getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());

        final View customLayout = getLayoutInflater().inflate(R.layout.popup_saisie_groupe,
                null);
        ((EditText)customLayout.findViewById(R.id.saisie)).setText(groupe.getLibelle());
        builder.setView(customLayout);

        builder.setTitle(getText(R.string.titre_popup_modifier_groupe) + groupe.getLibelle());

        builder.setPositiveButton(R.string.btn_valider, (dialog, which) ->
            onClickModifierGroupe(customLayout, groupe)
        );

        builder.setNegativeButton(R.string.annuler, null);

        builder.show();
    }

    /**
     * Appel l'activité de la popup pour supprimer un groupe.
     * @param information Les informations du groupe sélectionné.
     */
    private void supprimerGroupe(AdapterView.AdapterContextMenuInfo information) {
        Groupe groupe = groupeDao.findById(listeGroupes.get(information.position).getId());

        AlertDialog.Builder builder = new AlertDialog.Builder(getView().getContext());

        builder.setMessage(String.format(getString(R.string.message_popup_supprimer_groupe),
                        listeNomsGroupes.get(information.position)));

        builder.setTitle(getText(R.string.titre_popup_supprimer_groupe) + groupe.getLibelle());

        builder.setPositiveButton(R.string.btn_valider, (dialog, which) -> {
            groupeDao.delete(groupe);
            acutaliserListe();
        });

        builder.setNegativeButton(R.string.annuler, null);

        builder.show();
    }

    public void onClickAjouterGroupe(View customLayout) {
        String saisie = ((EditText)customLayout.findViewById(R.id.saisie)).getText().toString();

        if (!saisie.isEmpty()) {
            Groupe groupeSaved = groupeDao.save(new Groupe(saisie));
            if (groupeSaved != null) {
                acutaliserListe();
            } else {
                Toast.makeText(this.getContext(), R.string.erreur_groupe_existant, Toast.LENGTH_LONG ).show();
            }
        } else {
            Toast.makeText(getView().getContext(),
                            R.string.message_erreur_saisie_vide, Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void onClickModifierGroupe(View customLayout, Groupe groupe) {
        String saisie = ((EditText)customLayout.findViewById(R.id.saisie)).getText().toString();

        if (!saisie.isEmpty()) {
            groupe.setLibelle(saisie);
            groupeDao.update(groupe);
            acutaliserListe();
        } else {
            Toast.makeText(getView().getContext(),
                            R.string.message_erreur_saisie_vide, Toast.LENGTH_LONG)
                    .show();
        }
    }

    public void acutaliserListe() {
        adapter.clear();
        listeGroupes = groupeDao.findAll();
        listeNomsGroupes = Groupe.getLibellesGroupes(listeGroupes);
        adapter.addAll(listeNomsGroupes);
    }
}
