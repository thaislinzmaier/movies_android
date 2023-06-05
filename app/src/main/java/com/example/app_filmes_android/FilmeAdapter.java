package com.example.app_filmes_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class FilmeAdapter extends ArrayAdapter<Movie> {
        private final Context context;
        private final ArrayList<Movie> elementos;
        public FilmeAdapter(Context context, ArrayList<Movie> elementos) {
            super(context, R.layout.linha, elementos);
            this.context = context;
            this.elementos = elementos;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.linha, parent, false);
            TextView titulo = (TextView) rowView.findViewById(R.id.txtTitle);
            TextView overview = (TextView) rowView.findViewById(R.id.txtOverview);
            titulo.setText(elementos.get(position).getTitle());
            overview.setText(elementos.get(position).getOverview());
            return rowView;
        }
    }
