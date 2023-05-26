package com.example.gestionlignebus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gestionlignebus.model.Ligne;

import java.util.List;

public class LigneSpinnerAdapter extends ArrayAdapter<Ligne> {

    public LigneSpinnerAdapter(@NonNull Context context, List<Ligne> lignes) {
        super(context, android.R.layout.simple_spinner_item, lignes);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        Ligne option = getItem(position);
        textView.setText(option.getLibelle());
        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        Ligne option = getItem(position);
        textView.setText(option.getLibelle());
        textView.setTextSize(18);
        return textView;
    }
}
