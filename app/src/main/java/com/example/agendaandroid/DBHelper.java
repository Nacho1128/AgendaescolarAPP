package com.example.agendaandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "tareas.db"; //nombre de la bsdd
    public static final int DATABASE_VERSION = 4; //version de la bsdd

    //tablas y columnas
    public static final String TABLE_TAREAS = "tareas";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ASIGNATURA = "asignatura";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_COMPLETADA = "completada";

    public static final String COLUMN_IMAGEN = "imagen";

    //constructor que inicializa la bsdd
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //se ejecuta la primera vez que se crea la bsdd
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TAREAS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ASIGNATURA + " TEXT NOT NULL, " +
                COLUMN_DESCRIPCION + " TEXT, " +
                COLUMN_FECHA + " TEXT, " +
                COLUMN_IMAGEN + " TEXT, " +
                COLUMN_COMPLETADA + " INTEGER DEFAULT 0)");
    }

    //se ejecuta si se cambia la version de la bsdd y borra la tabla anterior si existe y la vuelve a crear
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        onCreate(db);
    }
}
