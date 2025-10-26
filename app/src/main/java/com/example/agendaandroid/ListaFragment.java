package com.example.agendaandroid;

import android.app.AlertDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.HashMap;

public class ListaFragment extends Fragment {

    ListView listViewTareas;
    DBHelper dbHelper;
    ArrayList<Integer> listaIds;//lista para guardar los id de las tareas

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        listViewTareas = view.findViewById(R.id.listViewTareas);
        dbHelper = new DBHelper(getContext());

        mostrarTareas();

        // Click largo marcar como completada
        listViewTareas.setOnItemLongClickListener((parent, view1, position, id) -> {
            if (position < listaIds.size()) {
                int idTarea = listaIds.get(position);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE tareas SET completada = 1 WHERE _id = ?", new Object[]{idTarea});
                db.close();
                Toast.makeText(getContext(), "Tarea marcada como completada ✅", Toast.LENGTH_SHORT).show();
                mostrarTareas();
            }
            return true;
        });

        // Click corto eliminar tarea
        listViewTareas.setOnItemClickListener((adapterView, view12, position, id) -> {
            if (position < listaIds.size()) {
                confirmarEliminar(listaIds.get(position));
            }
        });

        requireActivity().setTitle("");
        listViewTareas.post(() -> listViewTareas.setSelectionAfterHeaderView());

        return view;
    }

    //carga las tareas desde la bsdd
    private void mostrarTareas() {
        ArrayList<HashMap<String, String>> listaMap = new ArrayList<>();
        listaIds = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DBHelper.TABLE_TAREAS,
                null,
                "completada = 0",
                null,
                null,
                null,
                DBHelper.COLUMN_ID + " DESC"
        );

        //si hay tareas se agregan a la lista
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                String asignatura = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ASIGNATURA));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPCION));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_FECHA));
                String imagen = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGEN));

                //guarda los datos en un hashmap
                HashMap<String, String> tarea = new HashMap<>();
                tarea.put("id", String.valueOf(id));
                tarea.put("asignatura", asignatura);
                tarea.put("descripcion", descripcion);
                tarea.put("fecha", fecha);
                tarea.put("imagen", imagen);

                listaMap.add(tarea);
                listaIds.add(id);
            } while (cursor.moveToNext());
        } else {
            //si no hay tareas se muestra un mensaje
            HashMap<String, String> vacia = new HashMap<>();
            vacia.put("asignatura", "No hay tareas pendientes 😴");
            vacia.put("descripcion", "");
            vacia.put("fecha", "");
            vacia.put("imagen", "");
            listaMap.add(vacia);
        }

        cursor.close();
        db.close();

        //crea el adaptador para mostrar los datos
        TareaAdapter adapter = new TareaAdapter(getContext(), listaMap);
        listViewTareas.setAdapter(adapter);
    }

    //muestra un dialogo para confirmar la eliminacion
    private void confirmarEliminar(int idTarea) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar tarea")
                .setMessage("¿Seguro que deseas eliminar esta tarea?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarTarea(idTarea))
                .setNegativeButton("Cancelar", null)
                .show();
    }


    //elimina una tarea
    private void eliminarTarea(int idTarea) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete(DBHelper.TABLE_TAREAS, DBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(idTarea)});
        db.close();

        if (filas > 0) {
            Toast.makeText(getContext(), "Tarea eliminada correctamente 🗑", Toast.LENGTH_SHORT).show();
            mostrarTareas();
        } else {
            Toast.makeText(getContext(), "Error al eliminar la tarea ❌", Toast.LENGTH_SHORT).show();
        }
    }

    //cada vez que se vuelve al fragment se refresca la lista
    @Override
    public void onResume() {
        super.onResume();
        mostrarTareas();
    }
}


