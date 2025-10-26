package com.example.agendaandroid;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "tareas.db";
    public static final int DATABASE_VERSION = 2;

    public static final String TABLE_TAREAS = "tareas";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ASIGNATURA = "asignatura";
    public static final String COLUMN_DESCRIPCION = "descripcion";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_COMPLETADA = "completada";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_TAREAS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ASIGNATURA + " TEXT NOT NULL, " +
                COLUMN_DESCRIPCION + " TEXT, " +
                COLUMN_FECHA + " TEXT, " +
                COLUMN_COMPLETADA + " INTEGER DEFAULT 0)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TAREAS);
        onCreate(db);
    }
}
