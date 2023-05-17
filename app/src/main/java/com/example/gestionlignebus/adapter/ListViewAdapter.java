package com.example.gestionlignebus.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
        Resources res = context.getResources();

        if(position % 2 == 0)
            view.setBackgroundColor(res.getColor(R.color.item_pair));
        else
            view.setBackgroundColor(res.getColor(R.color.item_impair));

        return view;
    }
}
