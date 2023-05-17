package com.example.gestionlignebus.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.gestionlignebus.R;

public class FragmentItineraire extends Fragment implements View.OnClickListener {
    /**
     * Retourne une instance de FragmentDepense
     * @return l'instance
     */
    public static FragmentItineraire newInstance() {
        return new FragmentItineraire();
    }

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        return layoutInflater.inflate(R.layout.fragment_itineraire, container, false);
    }

    @Override
    public void onClick(View view) {
        // non utilisé
    }
}
