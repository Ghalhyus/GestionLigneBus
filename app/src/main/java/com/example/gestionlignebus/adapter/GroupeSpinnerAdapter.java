package com.example.gestionlignebus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gestionlignebus.model.Groupe;

import java.util.List;

public class GroupeSpinnerAdapter extends ArrayAdapter<Groupe> {

    public GroupeSpinnerAdapter(@NonNull Context context, @NonNull List<Groupe> options) {
        super(context, android.R.layout.simple_spinner_item, options);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        Groupe option = getItem(position);
        textView.setText(option.getLibelle());
        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        Groupe option = getItem(position);
        textView.setText(option.getLibelle());
        textView.setTextSize(18);
        return textView;
    }
}

