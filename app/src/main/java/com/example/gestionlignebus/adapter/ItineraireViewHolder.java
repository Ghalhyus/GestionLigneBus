package com.example.gestionlignebus.adapter;

import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.R;

public class ItineraireViewHolder extends RecyclerView.ViewHolder {


    private ListView itineraire;

    public ItineraireViewHolder(@NonNull View itemView) {
        super(itemView);
        itineraire = itemView.findViewById(R.id.itineraire_groupe_liste);
    }

    public void bind() {
        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(
                itemView.getContext(),
                R.layout.ligne_itineraire,
                null, // TODO Ajouter le curseur correspondant
                new String[] {

                },
                new int[] {
                        R.id.itineraire_arret_nom,
                        R.id.itineraire_ligne_nom,
                        R.id.itineraire_horaire
                },
                0
        );
        itineraire.setAdapter(simpleCursorAdapter);
    }
}
