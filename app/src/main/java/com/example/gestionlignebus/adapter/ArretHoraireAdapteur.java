package com.example.gestionlignebus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.model.ArretHoraire;
import com.example.gestionlignebus.viewholder.ArretHoraireViewHolder;
import com.example.gestionlignebus.R;

import java.util.List;

public class ArretHoraireAdapteur extends RecyclerView.Adapter<ArretHoraireViewHolder> {

    private List<ArretHoraire> lesDonnees;

    public ArretHoraireAdapteur(List<ArretHoraire> lesDonnees) {
        this.lesDonnees = lesDonnees;
    }

    @Override
    public ArretHoraireViewHolder onCreateViewHolder(ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(
                viewGroup.getContext()).inflate(R.layout.vue_item_ligne,
                viewGroup,false);
        return new ArretHoraireViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArretHoraireViewHolder holder, int position) {
        ArretHoraire myObject = lesDonnees.get(position);
        holder.bind(myObject);
    }

    @Override
    public int getItemCount() {
        return lesDonnees.size();
    }
}
