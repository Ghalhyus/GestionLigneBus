package com.example.gestionlignebus.viewholder;

import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.adapter.PassageAdapter;
import com.example.gestionlignebus.model.Passage;

import java.util.List;

public class ItineraireViewHolder extends RecyclerView.ViewHolder {

    private ListView itineraire;

    public ItineraireViewHolder(@NonNull View itemView) {
        super(itemView);
        itineraire = itemView.findViewById(R.id.itineraire);
    }

    public void bind(List<Passage> passages, List<String> lignes) {
        PassageAdapter adapter = new PassageAdapter(itineraire.getContext(),
                R.layout.ligne_itineraire , passages, lignes);
        itineraire.setAdapter(adapter);
    }
}
