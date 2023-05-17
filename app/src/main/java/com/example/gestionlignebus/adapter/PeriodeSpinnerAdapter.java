package com.example.gestionlignebus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.gestionlignebus.model.Periode;

import java.util.List;

public class PeriodeSpinnerAdapter extends ArrayAdapter<Periode> {

    public PeriodeSpinnerAdapter(@NonNull Context context, @NonNull List<Periode> options) {
        super(context, android.R.layout.simple_spinner_item, options);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getView(position, convertView, parent);
        Periode option = getItem(position);
        textView.setText(option.getLibelle());
        return textView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
        Periode option = getItem(position);
        textView.setText(option.getLibelle());
        textView.setTextSize(18);
        return textView;
    }
}

