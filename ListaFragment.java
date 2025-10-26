package com.example.agendaandroid;

import android.app.AlertDialog;
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

public class ListaFragment extends Fragment {

    // Lista donde se muestran las tareas pendientes
    ListView listViewTareas;

    // Clase que gestiona la base de datos SQLite
    DBHelper dbHelper;

    // Listas para guardar los textos de las tareas y sus IDs
    ArrayList<String> listaTareas;
    ArrayList<Integer> listaIds; // IDs de cada tarea en la base

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Infla (carga) el diseño del fragment (fragment_lista.xml)
        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        // Conecta los elementos del layout con el código
        listViewTareas = view.findViewById(R.id.listViewTareas);
        dbHelper = new DBHelper(getContext());

        // Muestra todas las tareas al iniciar
        mostrarTareas();

        // 👇 Click largo → marca una tarea como completada
        listViewTareas.setOnItemLongClickListener((parent, view1, position, id) -> {
            if (position < listaIds.size()) {
                int idTarea = listaIds.get(position);

                // Abre la base en modo escritura
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Actualiza el valor de "completada" a 1 para esa tarea
                db.execSQL("UPDATE tareas SET completada = 1 WHERE _id = ?", new Object[]{idTarea});
                db.close();

                Toast.makeText(getContext(), "Tarea marcada como completada ✅", Toast.LENGTH_SHORT).show();

                // Actualiza la lista para reflejar el cambio
                mostrarTareas();
            }
            return true;
        });

        // 👇 Click corto → elimina una tarea
        listViewTareas.setOnItemClickListener((adapterView, view12, position, id) -> {
            if (position < listaIds.size()) {
                confirmarEliminar(listaIds.get(position));
            }
        });

        return view;
    }

    // 🔍 Muestra las tareas pendientes en la lista
    private void mostrarTareas() {
        listaTareas = new ArrayList<>();
        listaIds = new ArrayList<>();

        // Abre la base de datos en modo lectura
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta que obtiene solo las tareas pendientes (completada = 0)
        Cursor cursor = db.query(
                DBHelper.TABLE_TAREAS,
                null,
                "completada = 0", // 👈 filtro
                null,
                null,
                null,
                DBHelper.COLUMN_ID + " DESC" // orden: más recientes primero
        );

        // Si hay tareas, las recorre y agrega a las listas
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                String asignatura = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ASIGNATURA));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPCION));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_FECHA));

                // Crea un texto bonito para mostrar en el ListView
                String tarea = "📘 " + asignatura + "\n📝 " + descripcion + "\n📅 " + fecha;
                listaTareas.add(tarea);
                listaIds.add(id);
            } while (cursor.moveToNext());
        } else {
            // Si no hay tareas, muestra un mensaje por defecto
            listaTareas.add("No hay tareas pendientes 😴");
        }

        // Cierra la conexión
        cursor.close();
        db.close();

        // Crea el adaptador para mostrar los datos en el ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                listaTareas
        );
        listViewTareas.setAdapter(adapter);
    }

    // 🗑️ Muestra un cuadro de confirmación antes de eliminar
    private void confirmarEliminar(int idTarea) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar tarea")
                .setMessage("¿Seguro que deseas eliminar esta tarea?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarTarea(idTarea))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ❌ Elimina una tarea según su ID
    private void eliminarTarea(int idTarea) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Elimina el registro donde el ID coincida
        int filas = db.delete(DBHelper.TABLE_TAREAS, DBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(idTarea)});
        db.close();

        if (filas > 0) {
            Toast.makeText(getContext(), "Tarea eliminada correctamente 🗑️", Toast.LENGTH_SHORT).show();
            mostrarTareas(); // Actualiza la lista
        } else {
            Toast.makeText(getContext(), "Error al eliminar la tarea ❌", Toast.LENGTH_SHORT).show();
        }
    }

    // 🔁 Refresca la lista cada vez que se vuelve al fragment
    @Override
    public void onResume() {
        super.onResume();
        mostrarTareas();
    }
}



