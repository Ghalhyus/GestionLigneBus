package com.example.gestionlignebus;

import android.widget.TextView;

public class ArretHoraire {
    private String libelleArret;
    private String horairePassage;

    public ArretHoraire(String libelleArret, String horairePassage) {
        this.libelleArret = libelleArret;
        this.horairePassage = horairePassage;
    }

    public String getNomLibelle() {
        return libelleArret;
    }

    public String getHorairePassage() {
        return horairePassage;
    }
}
