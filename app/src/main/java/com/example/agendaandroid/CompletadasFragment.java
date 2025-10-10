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
    ArrayList<String> listaTareas;
    ArrayList<Integer> listaIds;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completadas, container, false);

        lvCompletadas = view.findViewById(R.id.lvCompletadas);
        dbHelper = new DBHelper(getContext());

        mostrarCompletadas();

        // Click largo → restaurar tarea como pendiente
        lvCompletadas.setOnItemLongClickListener((parent, view1, position, id) -> {
            if (position < listaIds.size()) {
                int idTarea = listaIds.get(position);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.execSQL("UPDATE tareas SET completada = 0 WHERE _id = ?", new Object[]{idTarea});
                db.close();

                Toast.makeText(getContext(), "Tarea restaurada como pendiente 🔁", Toast.LENGTH_SHORT).show();
                mostrarCompletadas(); // refresca la lista
            }
            return true;
        });

        return view;
    }

    private void mostrarCompletadas() {
        listaTareas = new ArrayList<>();
        listaIds = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                DBHelper.TABLE_TAREAS,
                null,
                "completada = 1",
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

                String tarea = "✅ " + asignatura + "\n📝 " + descripcion + "\n📅 " + fecha;
                listaTareas.add(tarea);
                listaIds.add(id);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        if (listaTareas.isEmpty()) {
            listaTareas.add("No hay tareas completadas todavía 📭");
            Toast.makeText(getContext(), "Aún no completaste ninguna tarea 😅", Toast.LENGTH_SHORT).show();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                listaTareas
        );
        lvCompletadas.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mostrarCompletadas();
    }
}
