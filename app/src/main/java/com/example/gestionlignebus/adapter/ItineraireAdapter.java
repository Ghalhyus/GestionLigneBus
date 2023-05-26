package com.example.gestionlignebus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.viewholder.ItineraireViewHolder;

import java.util.List;

public class ItineraireAdapter extends RecyclerView.Adapter<ItineraireViewHolder> {

    private List<List<Passage>> passages;
    private List<List<String>> lignes;

    public ItineraireAdapter() {
    }

    public ItineraireAdapter(List<List<Passage>> passages, List<List<String>> lignes) {
        this.passages = passages;
        this.lignes = lignes;
    }


    @Override
    public ItineraireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_itineraire, parent, false);
        return new ItineraireViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraireViewHolder holder, int position) {
        holder.bind(passages.get(position), lignes.get(position));
    }

    @Override
    public int getItemCount() {
        return passages.size();
    }
}
