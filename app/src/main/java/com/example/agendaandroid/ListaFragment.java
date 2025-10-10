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

    ListView listViewTareas;
    DBHelper dbHelper;
    ArrayList<String> listaTareas;
    ArrayList<Integer> listaIds; // IDs de cada tarea

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lista, container, false);

        listViewTareas = view.findViewById(R.id.listViewTareas);
        dbHelper = new DBHelper(getContext());

        mostrarTareas();

        // 👇 Click largo → Marcar como completada
        listViewTareas.setOnItemLongClickListener((parent, view1, position, id) -> {
            if (position < listaIds.size()) {
                int idTarea = listaIds.get(position);

                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE tareas SET completada = 1 WHERE _id = ?", new Object[]{idTarea});
                db.close();

                Toast.makeText(getContext(), "Tarea marcada como completada ✅", Toast.LENGTH_SHORT).show();
                mostrarTareas(); // refresca la lista
            }
            return true;
        });

        // 👇 Click corto → Eliminar tarea
        listViewTareas.setOnItemClickListener((adapterView, view12, position, id) -> {
            if (position < listaIds.size()) {
                confirmarEliminar(listaIds.get(position));
            }
        });

        return view;
    }

    private void mostrarTareas() {
        listaTareas = new ArrayList<>();
        listaIds = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DBHelper.TABLE_TAREAS,
                null,
                "completada = 0", // 👈 solo mostrar tareas pendientes
                null,
                null,
                null,
                DBHelper.COLUMN_ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ID));
                String asignatura = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ASIGNATURA));
                String descripcion = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPCION));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_FECHA));

                String tarea = "📘 " + asignatura + "\n📝 " + descripcion + "\n📅 " + fecha;
                listaTareas.add(tarea);
                listaIds.add(id);
            } while (cursor.moveToNext());
        } else {
            listaTareas.add("No hay tareas pendientes 😴");
        }

        cursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                listaTareas
        );
        listViewTareas.setAdapter(adapter);
    }

    private void confirmarEliminar(int idTarea) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar tarea")
                .setMessage("¿Seguro que deseas eliminar esta tarea?")
                .setPositiveButton("Sí", (dialog, which) -> eliminarTarea(idTarea))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void eliminarTarea(int idTarea) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete(DBHelper.TABLE_TAREAS, DBHelper.COLUMN_ID + "=?", new String[]{String.valueOf(idTarea)});
        db.close();

        if (filas > 0) {
            Toast.makeText(getContext(), "Tarea eliminada correctamente 🗑️", Toast.LENGTH_SHORT).show();
            mostrarTareas();
        } else {
            Toast.makeText(getContext(), "Error al eliminar la tarea ❌", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mostrarTareas();
    }
}



