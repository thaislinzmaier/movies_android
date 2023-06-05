package com.example.app_filmes_android;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditarFilmeActivity extends AppCompatActivity {
        private DatabaseHelper dbHelper;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_edit_movie);
            Intent intent = getIntent();
            final int id = intent.getIntExtra("ID", 0);
            dbHelper = new DatabaseHelper(this);
            Movie movie = dbHelper.getMovie(id);
            final EditText titulo = (EditText) findViewById(R.id.editaTitulo);
            final EditText overview = (EditText) findViewById(R.id.editaOverview);
            titulo.setText(movie.getTitle());
            overview.setText(movie.getOverview());
            final Button alterar = (Button) findViewById(R.id.btnEditar);
            alterar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = new Movie();
                    movie.setId(id);
                    movie.setTitle(titulo.getText().toString());
                    movie.setOverview(overview.getText().toString());
                    dbHelper.updateMovie(movie);
                    Intent intent = new Intent(EditarFilmeActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });
            final Button remover = (Button) findViewById(R.id.btnExcluir);
            remover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AlertDialog.Builder(EditarFilmeActivity.this)
                            .setTitle("Excluir filme do meu perfil")
                            .setMessage("Deseja mesmo excluir esse filme?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes,
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            Movie movie = new Movie();
                                            movie.setId(id);
                                            dbHelper.deleteMovie(movie);
                                            Intent intent = new Intent(EditarFilmeActivity.this,
                                                    MainActivity.class);
                                            startActivity(intent);
                                        }
                                    })
                            .setNegativeButton(android.R.string.
                                    no, null).show();
                }
            });
        }
}
