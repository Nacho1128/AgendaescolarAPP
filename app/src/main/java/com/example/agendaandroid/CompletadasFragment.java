package com.example.agendaandroid;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class CompletadasFragment extends Fragment {


    ListView lvCompletadas;


    DBHelper dbHelper;

    // Listas que guardan las tareas y sus ids
    ArrayList<String> listaTareas;
    ArrayList<Integer> listaIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_completadas, container, false);


        lvCompletadas = view.findViewById(R.id.lvCompletadas);


        dbHelper = new DBHelper(getContext());

        // Muestra las tareas completadas apenas se abre el fragment
        mostrarCompletadas();

        //  Click largo restaura la tarea (volverla a pendiente)
        lvCompletadas.setOnItemLongClickListener((parent, view1, position, id) -> {
            if (position < listaIds.size()) {
                int idTarea = listaIds.get(position); // Obtiene el ID de la tarea seleccionada

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Cambia el valor de completada a 0 (es decir, tarea pendiente)
                db.execSQL("UPDATE tareas SET completada = 0 WHERE _id = ?", new Object[]{idTarea});
                db.close();

                // Muestra un mensaje y actualiza la lista
                Toast.makeText(getContext(), "Tarea restaurada como pendiente 🔁", Toast.LENGTH_SHORT).show();
                mostrarCompletadas();
            }
            return true;
        });

        return view;
    }

    //Muestra las tareas completadas desde la base de datos
    private void mostrarCompletadas() {
        listaTareas = new ArrayList<>();
        listaIds = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta las tareas donde completada = 1 (solo las finalizadas)
        Cursor cursor = db.query(
                DBHelper.TABLE_TAREAS,
                null,
                "completada = 1",
                null,
                null,
                null,
                DBHelper.COLUMN_ID + " DESC" // ordena las más recientes primero
        );

        // Si hay tareas completadas, las recorre y guarda en la lista
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                String asignatura = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ASIGNATURA));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPCION));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_FECHA));

                // Arma un texto con íconos para mostrar
                String tarea = "✅ " + asignatura + "\n📝 " + descripcion + "\n📅 " + fecha;
                listaTareas.add(tarea);
                listaIds.add(id);
            } while (cursor.moveToNext());
        }

        // Cierra el cursor y la base
        cursor.close();
        db.close();

        // Si no hay tareas completadas, muestra un mensaje
        if (listaTareas.isEmpty()) {
            listaTareas.add("No hay tareas completadas todavía 📭");
        }

        // Crea el adaptador para mostrar los datos
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                listaTareas
        );

        // Asigna el adaptador al ListView
        lvCompletadas.setAdapter(adapter);
    }

    // Cada vez que se vuelve al fragment se refresca la lista
    @Override
    public void onResume() {
        super.onResume();
        mostrarCompletadas();
    }
}

