package com.example.app_filmes_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_MOVIES = "movies";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_OVERVIEW = "overview";
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    private void copyDatabaseFromAssets(Context context) {
            try {
                InputStream inputStream = context.getAssets().open("database.db");
                String outFileName = context.getDatabasePath(DATABASE_NAME).getPath();
                OutputStream outputStream = new FileOutputStream(outFileName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        copyDatabaseFromAssets(context);
        if (!isTableExists(db, TABLE_MOVIES)) {
            String createTableQuery = "CREATE TABLE " + TABLE_MOVIES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_OVERVIEW + " TEXT" +
                    // Adicione outras colunas na tabela conforme necessário
                    ")";
            db.execSQL(createTableQuery);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aqui você pode atualizar o esquema do banco de dados, se necessário
    }

    public void insertMovie(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, movie.getId());
        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_OVERVIEW, movie.getOverview());

        db.insert(TABLE_MOVIES, null, values);
        db.close();
        }

    public void insertMovies(List<MovieResponse> movies) {
        SQLiteDatabase db = getWritableDatabase();

        if (!isTableExists(db, TABLE_MOVIES)) {
            onCreate(db);
        }
        for(MovieResponse movie : movies) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, movie.getId());
            values.put(COLUMN_TITLE, movie.getTitle());
            values.put(COLUMN_OVERVIEW, movie.getOverview());

            db.insert(TABLE_MOVIES, null, values);
        }
        db.close();

    }

    private boolean isTableExists(SQLiteDatabase db, String tableName) {
        Cursor cursor = db.rawQuery("SELECT DISTINCT tbl_name FROM sqlite_master WHERE tbl_name = ?", new String[] { tableName });
        boolean tableExists = cursor.getCount() > 0;
        cursor.close();
        return tableExists;
    }
}
