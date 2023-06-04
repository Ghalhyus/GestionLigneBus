package com.example.gestionlignebus.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.model.Passage;

import java.util.List;

public class PassageAdapter extends ArrayAdapter<Passage> {

    private List<Passage> passages;
    private List<String> lignes;

    public PassageAdapter(Context context, int layout, List<Passage> passages,
                          List<String> lignes) {
        super(context, layout, passages);
        this.passages = passages;
        this.lignes = lignes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ligne_itineraire, parent, false);

        ((TextView) view.findViewById(R.id.itineraire_arret_nom))
                .setText(passages.get(position).getArret().getLibelle());

        ((TextView) view.findViewById(R.id.itineraire_ligne_nom)).setText(lignes.get(position));

        ((TextView) view.findViewById(R.id.itineraire_horaire))
                .setText(passages.get(position).getHoraire().toString());

        if (position % 2 == 0) {

        ((TextView) view.findViewById(R.id.itineraire_type)).setText(R.string.colonne_depart);
        view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.item_pair));
        } else {
            ((TextView) view.findViewById(R.id.itineraire_type)).setText(R.string.colonne_arrive);
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.item_impair));
        }

        return view;
    }

    public List<Passage> getPassages() {
        return passages;
    }

    public List<String> getLignes() {
        return lignes;
    }
}
