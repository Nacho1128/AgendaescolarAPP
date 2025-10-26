package com.example.agendaandroid;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class TareaAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<HashMap<String, String>> listaTareas;

    public TareaAdapter(Context context, ArrayList<HashMap<String, String>> listaTareas) {
        this.context = context;
        this.listaTareas = listaTareas;
    }

    @Override
    public int getCount() {
        return listaTareas.size();
    }

    @Override
    public Object getItem(int position) {
        return listaTareas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tarea, parent, false);
        }

        ImageView ivTarea = convertView.findViewById(R.id.ivTarea);
        TextView tvAsignatura = convertView.findViewById(R.id.tvAsignatura);
        TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
        TextView tvFecha = convertView.findViewById(R.id.tvFecha);

        HashMap<String, String> tarea = listaTareas.get(position);

        tvAsignatura.setText(tarea.get("asignatura"));
        tvDescripcion.setText(tarea.get("descripcion"));
        tvFecha.setText("📅 " + tarea.get("fecha"));

        String imagen = tarea.get("imagen");
        if (imagen != null && !imagen.isEmpty()) {
            ivTarea.setImageURI(Uri.parse(imagen));
        } else {
            ivTarea.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        return convertView;
    }
}
