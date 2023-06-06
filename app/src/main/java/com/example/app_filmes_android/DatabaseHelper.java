package com.example.app_filmes_android;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_MOVIES = "movies";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_OVERVIEW = "overview";

    private static final String[] COLUNAS = {COLUMN_ID, COLUMN_TITLE, COLUMN_OVERVIEW};
    private Context context;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context.getApplicationContext();
    }

    private void copyDatabaseFromAssets(Context context) {
            try {
                InputStream inputStream = context.getAssets().open("database.db");
                String outFileName = context.getFilesDir().getPath() + File.separator + DATABASE_NAME;
                OutputStream outputStream = new FileOutputStream(outFileName);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                Log.w("TAG", "SALVOU ESSA PORRA");
            } catch (IOException e) {
                e.printStackTrace();
                Log.w("TAG", "NÃO SALVOU ESSA DESGRAÇAAAAAAAAAAAAAAAAAAAAAAAA");
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
        Log.w("TAG", String.valueOf(db));

        String[] projection = {"id"};
        String selection = "id = ?";
        String[] selectionArgs = {String.valueOf(movie.getId())};
        Log.w("TAG", String.valueOf(movie.getId()));
        Cursor cursor = db.query("movies", projection, selection, selectionArgs, null, null, null);

        if (cursor.getCount() > 0) {
            int lastUsedId = movie.getId();
            lastUsedId++;
            String novoMovieId = Integer.toString(lastUsedId);

            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, novoMovieId);
            values.put(COLUMN_TITLE, movie.getTitle());
            values.put(COLUMN_OVERVIEW, movie.getOverview());
            db.insert(TABLE_MOVIES, null, values);
            Log.w("TAG", "INSERIU O FILME COM ID NOVO");

        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ID, movie.getId());
            values.put(COLUMN_TITLE, movie.getTitle());
            values.put(COLUMN_OVERVIEW, movie.getOverview());
            db.insert(TABLE_MOVIES, null, values);
            Log.w("TAG", "INSERIU O FILME COM ID VELHO");

        }
        cursor.close();
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

    private Movie cursorToMovie(Cursor cursor) {
        Movie movie = new Movie();
        movie.setId(Integer.parseInt(cursor.getString(0)));
        movie.setTitle(cursor.getString(1));
        movie.setOverview(cursor.getString(2));
        return movie;
    }
    public ArrayList<Movie> getAllMovies() {
        ArrayList<Movie> listaMovies = new ArrayList<Movie>();
        String query = "SELECT * FROM " +
                TABLE_MOVIES
                + " ORDER BY " + COLUMN_TITLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Movie movie = cursorToMovie(cursor);
                listaMovies.add(movie);
            } while (cursor.moveToNext());
        }
        return listaMovies;
    }

    public Movie getMovie(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MOVIES,
                COLUNAS,
                " id = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
            if (cursor == null) {
                return null;
            } else {
                cursor.moveToFirst();
                Movie movie = cursorToMovie(cursor);
                return movie;
            }
        }

    public int updateMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, movie.getTitle());
        values.put(COLUMN_OVERVIEW, movie.getOverview());
        int i = db.update(TABLE_MOVIES,values,COLUMN_ID + " = ?", new String[]{ String.valueOf(movie.getId()) });
        db.close();
        return i;
    }

    public int deleteMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(TABLE_MOVIES, COLUMN_ID + " = ?", new String[]{ String.valueOf(movie.getId()) });
        db.close();
        return i;
    }
}

