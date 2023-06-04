package com.example.gestionlignebus.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.model.ArretHoraire;

public class ArretHoraireViewHolder extends RecyclerView.ViewHolder{
    private TextView libelleLigne;
    private TextView horairePassage;

    public ArretHoraireViewHolder(View view){
        super(view);
        libelleLigne=view.findViewById(R.id.item_arret);
        horairePassage=view.findViewById(R.id.item_heure);
    }

    public void bind(ArretHoraire lignehoraire){
        libelleLigne.setText(lignehoraire.getNomLibelle());
        horairePassage.setText(lignehoraire.getHorairePassage());
    }
}
