package com.example.gestionlignebus.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.model.Passage;
import com.example.gestionlignebus.viewholder.ItineraireViewHolder;

import java.util.List;

public class ItineraireAdapter extends RecyclerView.Adapter<ItineraireViewHolder> {

    private List<List<Passage>> itineraires;
    private List<List<Passage>> trajet;
    private List<List<String>> lignes;
    private FragmentManager fragmentManager;
    private View map;

    public ItineraireAdapter(List<List<Passage>> itineraires, List<List<Passage>> trajet,
                             List<List<String>> lignes, FragmentManager fragmentManager) {
        this.itineraires = itineraires;
        this.trajet = trajet;
        this.lignes = lignes;
        this.fragmentManager = fragmentManager;
    }

    @Override
    public ItineraireViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardview_itineraire, parent, false);

        if (map == null) {
            map = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.popup_map,null);
        }

        return new ItineraireViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItineraireViewHolder holder, int position) {
        holder.bind(itineraires.get(position), trajet.get(position),
                lignes.get(position), fragmentManager, map);
    }

    @Override
    public int getItemCount() {
        return itineraires.size();
    }
}
