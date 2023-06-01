package com.example.gestionlignebus.viewholder;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gestionlignebus.R;
import com.example.gestionlignebus.adapter.PassageAdapter;
import com.example.gestionlignebus.model.Arret;
import com.example.gestionlignebus.model.Passage;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.List;

public class ItineraireViewHolder extends RecyclerView.ViewHolder
        implements AdapterView.OnItemClickListener, OnMapReadyCallback {

    private ListView itineraire;
    private List<Passage> trajet;
    private PassageAdapter adapter;
    private FragmentManager fragmentManager;
    private View map;

    public ItineraireViewHolder(@NonNull View itemView) {
        super(itemView);
        itineraire = itemView.findViewById(R.id.itineraire);
    }

    public void bind(List<Passage> itineraires, List<Passage> trajet,
                     List<String> lignes, FragmentManager fragmentManager) {
        adapter = new PassageAdapter(itineraire.getContext(),
                R.layout.ligne_itineraire , itineraires, lignes);
        itineraire.setAdapter(adapter);
        itineraire.setOnItemClickListener(this::onItemClick);

        this.trajet = trajet;

        this.fragmentManager = fragmentManager;

        map = LayoutInflater.from(adapter.getContext())
                .inflate(R.layout.popup_map,null);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

        if (map.getParent() != null) {
            ((ViewGroup)map.getParent()).removeView(map);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        builder.setView(map);

        builder.setTitle(R.string.titre_carte_itineraire);

        builder.setNeutralButton(R.string.btn_retour, null);

        builder.show();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        for (Passage passage : trajet) {
            Arret arret = passage.getArret();
            double latitude = Double.parseDouble(arret.getPosition().split(":")[0]);
            double longitude = Double.parseDouble(arret.getPosition().split(":")[1]);
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(latitude, longitude))
                    .title(passage.toString()));

        }
        double latitude = Double.parseDouble(
                trajet.get(0).getArret().getPosition().split(":")[0]);
        double longitude = Double.parseDouble(
                trajet.get(0).getArret().getPosition().split(":")[1]);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng( new LatLng(latitude,longitude)));
        googleMap.setMinZoomPreference(15);

    }
}
