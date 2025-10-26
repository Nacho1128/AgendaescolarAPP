package com.example.agendaandroid;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

public class AgregarFragment extends Fragment {

    // Campos de texto
    EditText etAsignatura, etDescripcion, etFecha;

    // Botones
    Button btnGuardar, btnSeleccionarImagen;

    // Imagen de vista previa
    ImageView ivPreview;

    // Control de base de datos
    DBHelper dbHelper;

    // Variables para edición de tarea e imagen
    int idTarea = -1;
    Uri imagenUri;

    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Carga el layout del fragment
        View view = inflater.inflate(R.layout.fragment_agregar, container, false);

        // Conecta elementos visuales con el código
        etAsignatura = view.findViewById(R.id.etAsignatura);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        etFecha = view.findViewById(R.id.etFecha);
        btnGuardar = view.findViewById(R.id.btnGuardar);
        btnSeleccionarImagen = view.findViewById(R.id.btnSeleccionarImagen);
        ivPreview = view.findViewById(R.id.ivPreview);

        dbHelper = new DBHelper(getContext());

        // 🔹 Formato automático para la fecha (DD-MM-AAAA)
        etFecha.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;
                isFormatting = true;

                //se eliminan caracteres no numericos
                String input = s.toString().replaceAll("[^\\d]", "");
                StringBuilder formatted = new StringBuilder();

                //limita la fecha a 8 caracteres
                if (input.length() > 8) input = input.substring(0, 8);

                //se agrega los guiones al formato de fecha
                for (int i = 0; i < input.length(); i++) {
                    formatted.append(input.charAt(i));
                    if ((i == 1 || i == 3) && i != input.length() - 1) {
                        formatted.append("-");
                    }
                }
                //actualiza el texto formateado en el campo
                int cursorPos = formatted.length();
                etFecha.setText(formatted.toString());
                etFecha.setSelection(Math.min(cursorPos, etFecha.getText().length()));
                isFormatting = false;
            }
        });

        // si recibe un id por argumentos se edita la tarea
        if (getArguments() != null && getArguments().containsKey("id_tarea")) {
            idTarea = getArguments().getInt("id_tarea");
            cargarTarea();
        }

        // Botón para guardar/actualizar tarea
        btnGuardar.setOnClickListener(v -> {
            if (idTarea == -1) guardarTarea();
            else actualizarTarea();
        });

        // Botón para seleccionar imagen
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        return view;
    }

    // Abre la galería de imágenes del celular
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Recibe el resultado de la selección y la muestra en la vista
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null) {
            imagenUri = data.getData();
            ivPreview.setImageURI(imagenUri);
        }
    }

    // Carga los datos de una tarea ya existente
    private void cargarTarea() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBHelper.TABLE_TAREAS,
                null,
                DBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(idTarea)},
                null, null, null);

        if (cursor.moveToFirst()) {
            etAsignatura.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_ASIGNATURA)));
            etDescripcion.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_DESCRIPCION)));
            etFecha.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_FECHA)));

            String imagen = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.COLUMN_IMAGEN));
            if (imagen != null && !imagen.isEmpty()) {
                imagenUri = Uri.parse(imagen);
                ivPreview.setImageURI(imagenUri);
            }
        }

        cursor.close();
        db.close();
        btnGuardar.setText("Actualizar Tarea");
    }

    // Guarda una nueva tarea en la bsdd
    private void guardarTarea() {
        String asignatura = etAsignatura.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String fecha = etFecha.getText().toString().trim();

        //valida si los campos no estan vacios
        if (asignatura.isEmpty() || descripcion.isEmpty() || fecha.isEmpty()) {
            Toast.makeText(getContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ASIGNATURA, asignatura);
        values.put(DBHelper.COLUMN_DESCRIPCION, descripcion);
        values.put(DBHelper.COLUMN_FECHA, fecha);

        //guarda la imagen
        if (imagenUri != null)
            values.put(DBHelper.COLUMN_IMAGEN, imagenUri.toString());

        long newRowId = db.insert(DBHelper.TABLE_TAREAS, null, values);
        db.close();

        if (newRowId != -1) {
            Toast.makeText(getContext(), "Tarea guardada correctamente ✅", Toast.LENGTH_SHORT).show();
            limpiarCampos();
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
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_ASIGNATURA, asignatura);
        values.put(DBHelper.COLUMN_DESCRIPCION, descripcion);
        values.put(DBHelper.COLUMN_FECHA, fecha);

        //actualiza la imagen
        if (imagenUri != null)
            values.put(DBHelper.COLUMN_IMAGEN, imagenUri.toString());

        int filas = db.update(DBHelper.TABLE_TAREAS, values, DBHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(idTarea)});
        db.close();

        if (filas > 0)
            Toast.makeText(getContext(), "Tarea actualizada correctamente 🔄", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getContext(), "Error al actualizar la tarea ❌", Toast.LENGTH_SHORT).show();
    }

    // Limpia los campos
    private void limpiarCampos() {
        etAsignatura.setText("");
        etDescripcion.setText("");
        etFecha.setText("");
        ivPreview.setImageResource(android.R.drawable.ic_menu_gallery);
        imagenUri=null;
    }
}