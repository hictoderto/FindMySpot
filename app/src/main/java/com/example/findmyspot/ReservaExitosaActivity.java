package com.example.findmyspot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ReservaExitosaActivity extends AppCompatActivity {

    private Button btnRegresar;
    private TextView texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_exitosa);

        btnRegresar = findViewById(R.id.btnVolver);
        texto = findViewById(R.id.textoConfirmacion);

        // Recuperar datos del intent
        Intent intent = getIntent();
        String nombreLugar = intent.getStringExtra("lugar");
        String idPlace = intent.getStringExtra("id");
        String hora = intent.getStringExtra("hora");
        String fecha = intent.getStringExtra("fecha");

        // Crear el mensaje
        String mensaje = "Has reservado exitosamente el lugar " + nombreLugar +
                " el día " + fecha + " a las " + hora + " con folio: " + idPlace;

        // Mostrar el mensaje
        texto.setText(mensaje);

        // Botón para volver al menú principal
        btnRegresar.setOnClickListener(v -> {
            Intent volver = new Intent(ReservaExitosaActivity.this, PrincipalSecreen.class);
            startActivity(volver);
            finish();
        });
    }
}
