package com.example.gestionlignebus.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.core.content.ContextCompat;

import com.example.gestionlignebus.R;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<String> {

    public ListViewAdapter(Context context, int layout, List<String> liste) {
        super(context, layout, liste);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = super.getView(position, convertView, parent);
        Context context = this.getContext();

        if(position % 2 == 0)
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.item_pair));
        else
            view.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.item_impair));

        return view;
    }
}
