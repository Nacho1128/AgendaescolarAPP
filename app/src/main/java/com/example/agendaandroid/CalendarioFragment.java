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

    CalendarView calendarView;// calendario de tareas
    ListView lvTareasFecha;  //lista donde se muestran las tareas del dia
    DBHelper dbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        calendarView = view.findViewById(R.id.calendarView);
        lvTareasFecha = view.findViewById(R.id.lvTareasFecha);
        dbHelper = new DBHelper(requireActivity());

        //  Escucha los cambios cuando se selecciona una fecha en el calendario
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            //  Usa el mismo formato que AgregarFragment (DD-MM-YYYY)
            String fechaSeleccionada = String.format("%02d-%02d-%04d", dayOfMonth, (month + 1), year);
            cargarTareasPorFecha(fechaSeleccionada); // Carga las tareas para la fecha seleccionada
        });

        return view;
    }

    // 🔹 Carga las tareas que coincidan con la fecha seleccionada
    private void cargarTareasPorFecha(String fecha) {
        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //consulta las tareas segun la fecha elegida
        Cursor cursor = db.rawQuery(
                "SELECT " + DBHelper.COLUMN_ASIGNATURA + ", " +
                        DBHelper.COLUMN_DESCRIPCION +
                        " FROM " + DBHelper.TABLE_TAREAS +
                        " WHERE " + DBHelper.COLUMN_FECHA + " = ?",
                new String[]{fecha}
        );

        //recorre los resultados y los guarda en la lista
        while (cursor.moveToNext()) {
            String tarea = "📘 " + cursor.getString(0) + "\n📝 " + cursor.getString(1);
            lista.add(tarea);
        }

        //si no hay tareas, muestra un mensaje
        if (lista.isEmpty()) {
            lista.add("📭 No hay tareas programadas para esta fecha");
        }

        //muestra los resultados
        lvTareasFecha.setAdapter(
                new ArrayAdapter<>(requireActivity(), android.R.layout.simple_list_item_1, lista)
        );

        //se cierra el cursor y la bsdd
        cursor.close();
        db.close();
    }
}
