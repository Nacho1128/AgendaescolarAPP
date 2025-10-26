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

    // Campos de texto donde el usuario escribe los datos
    EditText etAsignatura, etDescripcion, etFecha;

    // Botón para guardar o actualizar la tarea
    Button btnGuardar;

    // Clase auxiliar que maneja la base de datos SQLite
    DBHelper dbHelper;

    // Guarda el ID de la tarea cuando se edita (por defecto -1 = nueva tarea)
    int idTarea = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Carga el diseño del fragment fragment agregar
        View view = inflater.inflate(R.layout.fragment_agregar, container, false);

        // Asocia los elementos del XML con las variables Java
        etAsignatura = view.findViewById(R.id.etAsignatura);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etFecha = view.findViewById(R.id.etFecha);
        btnGuardar = view.findViewById(R.id.btnGuardar);

        // Crea el objeto que conecta con la base de datos
        dbHelper = new DBHelper(getContext());

        // Si este fragmento recibe un "id_tarea", significa que el usuario quiere editar una
        if (getArguments() != null && getArguments().containsKey("id_tarea")) {
            idTarea = getArguments().getInt("id_tarea");
            cargarTarea(); // Se cargan los datos en los campos
        }

        // Cuando se presiona el botón "Guardar"
        btnGuardar.setOnClickListener(v -> {
            if (idTarea == -1) {
                // Si no hay id → es una nueva tarea
                guardarTarea();
            } else {
                // Si existe id → se actualiza una tarea existente
                actualizarTarea();
            }
        });

        return view;
    }

    // Carga los datos de una tarea existente para editarlos
    private void cargarTarea() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Consulta que obtiene la tarea según su ID
        Cursor cursor = db.query(DBHelper.TABLE_TAREAS,
                null,
                DBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(idTarea)},
                null,
                null,
                null);

        // Si se encuentra el registro, se rellenan los campos
        if (cursor.moveToFirst()) {
            etAsignatura.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ASIGNATURA)));
            etDescripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPCION)));
            etFecha.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_FECHA)));
        }

        cursor.close();
        db.close();

        // Cambia el texto del botón para indicar que se está editando
        btnGuardar.setText("Actualizar Tarea");
    }

    // Guarda una nueva tarea en la base de datos
    private void guardarTarea() {
        // Obtiene los valores de los EditText
        String asignatura = etAsignatura.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        // Verifica que los campos no estén vacíos
        if (asignatura.isEmpty() || descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Crea un conjunto de valores para insertar
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ASIGNATURA, asignatura);
        values.put(DBHelper.COLUMN_DESCRIPCION, descripcion);
        values.put(DBHelper.COLUMN_FECHA, fecha);

        // Inserta el nuevo registro en la tabla
        long newRowId = db.insert(DBHelper.TABLE_TAREAS, null, values);
        db.close();

        // Verifica si la inserción fue exitosa
        if (newRowId != -1) {
            Toast.makeText(getContext(), "Tarea guardada correctamente ✅", Toast.LENGTH_SHORT).show();
            limpiarCampos(); // Limpia los campos después de guardar
        } else {
            Toast.makeText(getContext(), "Error al guardar la tarea ❌", Toast.LENGTH_SHORT).show();
        }
    }

    // Actualiza una tarea existente
    private void actualizarTarea() {
        String asignatura = etAsignatura.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Se guardan los nuevos valores en ContentValues
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ASIGNATURA, asignatura);
        values.put(DBHelper.COLUMN_DESCRIPCION, descripcion);
        values.put(DBHelper.COLUMN_FECHA, fecha);

        // Actualiza el registro donde el ID coincida
        int filas = db.update(DBHelper.TABLE_TAREAS, values, DBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(idTarea)});
        db.close();

        // Muestra mensaje según el resultado
        if (filas > 0) {
            Toast.makeText(getContext(), "Tarea actualizada correctamente 🔄", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Error al actualizar la tarea ❌", Toast.LENGTH_SHORT).show();
        }
    }

    // Limpia los campos después de guardar
    private void limpiarCampos() {
        etAsignatura.setText("");
        etDescripcion.setText("");
        etFecha.setText("");
    }
}