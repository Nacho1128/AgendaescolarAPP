package com.example.agendaandroid;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;

public class CalendarioFragment extends Fragment {

    CalendarView calendarView;
    ListView lvTareasFecha;
    DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        lvTareasFecha = view.findViewById(R.id.lvTareasFecha);
        dbHelper = new DBHelper(getContext());

        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            String fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
            cargarTareasPorFecha(fechaSeleccionada);
        });

        return view;
    }

    private void cargarTareasPorFecha(String fecha) {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT asignatura, descripcion FROM tareas WHERE fecha = ?", new String[]{fecha});

        while (cursor.moveToNext()) {
            String tarea = "📘 " + cursor.getString(0) + "\n" + cursor.getString(1);
            lista.add(tarea);
        }

        lvTareasFecha.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, lista));
        cursor.close();
        db.close();
    }
}
