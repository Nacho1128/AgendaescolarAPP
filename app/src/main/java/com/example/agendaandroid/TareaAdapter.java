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
    private ArrayList<HashMap<String, String>> listaTareas; //lista con las tareas y sus datos

    public TareaAdapter(Context context, ArrayList<HashMap<String, String>> listaTareas) { //constructor
        this.context = context;
        this.listaTareas = listaTareas;
    }

    //devuelve la cantidad de elementos que tiene la lista
    @Override
    public int getCount() {
        return listaTareas.size();
    }

    //devuelve un elemento en una posicion especifica
    @Override
    public Object getItem(int position) {
        return listaTareas.get(position);
    }

    //devuelve el id de un elemento en una posicion especifica
    @Override
    public long getItemId(int position) {
        return position;
    }

    //crea una vista para cada elemento de la lista
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_tarea, parent, false);
        }

        //vincula los elementos de la vista con los datos de la tarea
        ImageView ivTarea = convertView.findViewById(R.id.ivTarea);
        TextView tvAsignatura = convertView.findViewById(R.id.tvAsignatura);
        TextView tvDescripcion = convertView.findViewById(R.id.tvDescripcion);
        TextView tvFecha = convertView.findViewById(R.id.tvFecha);

        //obtiene los datos de la tarea en la posicion actual
        HashMap<String, String> tarea = listaTareas.get(position);

        //asigna los datos a los elementos de la vista
        tvAsignatura.setText(tarea.get("asignatura"));
        tvDescripcion.setText(tarea.get("descripcion"));
        tvFecha.setText("📅 " + tarea.get("fecha"));

        //muestra la imagen de la tarea si existe
        String imagen = tarea.get("imagen");
        if (imagen != null && !imagen.isEmpty()) {
            ivTarea.setImageURI(Uri.parse(imagen));
        } else {
            ivTarea.setImageResource(android.R.drawable.ic_menu_gallery);
        }

        return convertView;
    }
}
