package com.example.agendaandroid;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AgregarFragment extends Fragment {

    EditText etAsignatura, etDescripcion, etFecha;
    Button btnGuardar;
    DBHelper dbHelper;
    int idTarea = -1; // -1 significa "nueva tarea"

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agregar, container, false);

        etAsignatura = view.findViewById(R.id.etAsignatura);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etFecha = view.findViewById(R.id.etFecha);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        dbHelper = new DBHelper(getContext());

        if (getArguments() != null && getArguments().containsKey("id_tarea")) {
            idTarea = getArguments().getInt("id_tarea");
            cargarTarea();
        }

        btnGuardar.setOnClickListener(v -> {
            if (idTarea == -1) {
                guardarTarea();
            } else {
                actualizarTarea();
            }
        });

        return view;
    }

    private void cargarTarea() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_TAREAS,
                null,
                DBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(idTarea)},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            etAsignatura.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ASIGNATURA)));
            etDescripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPCION)));
            etFecha.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_FECHA)));
        }

        cursor.close();
        db.close();

        btnGuardar.setText("Actualizar Tarea");
    }

    private void guardarTarea() {
        String asignatura = etAsignatura.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        if (asignatura.isEmpty() || descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ASIGNATURA, asignatura);
        values.put(DBHelper.COLUMN_DESCRIPCION, descripcion);
        values.put(DBHelper.COLUMN_FECHA, fecha);

        long newRowId = db.insert(DBHelper.TABLE_TAREAS, null, values);
        db.close();

        if (newRowId != -1) {
            Toast.makeText(getContext(), "Tarea guardada correctamente ✅", Toast.LENGTH_SHORT).show();
            limpiarCampos();
        } else {
            Toast.makeText(getContext(), "Error al guardar la tarea ❌", Toast.LENGTH_SHORT).show();
        }
    }

    private void actualizarTarea() {
        String asignatura = etAsignatura.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ASIGNATURA, asignatura);
        values.put(DBHelper.COLUMN_DESCRIPCION, descripcion);
        values.put(DBHelper.COLUMN_FECHA, fecha);

        int filas = db.update(DBHelper.TABLE_TAREAS, values, DBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(idTarea)});
        db.close();

        if (filas > 0) {
            Toast.makeText(getContext(), "Tarea actualizada correctamente 🔄", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error al actualizar la tarea ❌", Toast.LENGTH_SHORT).show();
        }
    }

    private void limpiarCampos() {
        etAsignatura.setText("");
        etDescripcion.setText("");
        etFecha.setText("");
    }
}

