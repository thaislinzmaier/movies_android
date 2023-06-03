package com.example.app_filmes_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_MOVIES = "movies";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_OVERVIEW = "overview";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_MOVIES + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY, " +
                COLUMN_TITLE + " TEXT, " +
                COLUMN_OVERVIEW + " TEXT" +
                // Adicione outras colunas na tabela conforme necessário
                ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aqui você pode atualizar o esquema do banco de dados, se necessário
    }

    public void insertMovies(List<Movie> movies) {
        SQLiteDatabase db = getWritableDatabase();

        for (Movie movie : movies) {
            ContentValues values = new ContentValues();
            values.put("title", movie.getTitle());
            values.put("overview", movie.getOverview());
            // Adicione outras colunas e valores conforme necessário

            db.insert("movies", null, values);
        }

        db.close();
    }
}