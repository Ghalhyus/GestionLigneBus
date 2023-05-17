package com.example.gestionlignebus.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.gestionlignebus.fragment.FragmentConsultation;
import com.example.gestionlignebus.fragment.FragmentGroupe;
import com.example.gestionlignebus.fragment.FragmentItineraire;

public class AdapterPage extends FragmentStateAdapter {
    /**
     * Le nombre de fragment (onglet) de l'application
     */
    private static final int NB_FRAGMENT = 3;

    public AdapterPage(FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0 :
                return FragmentConsultation.newInstance();
            case 1 :
                return FragmentGroupe.newInstance();
            case 2 :
                return FragmentItineraire.newInstance();
            default:
                return FragmentConsultation.newInstance();
        }
    }

    @Override
    public int getItemCount() {
        return NB_FRAGMENT;
    }
}
