package com.example.findmyspot;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CancelacionExitosaActivity extends AppCompatActivity {

    private TextView mensajeTextView;
    private Button regresarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancelacion_exitosa);

        mensajeTextView = findViewById(R.id.textMensajeExito);
        regresarButton = findViewById(R.id.btnRegresarFinal);

        String edificio = getIntent().getStringExtra("edificio");
        String aula = getIntent().getStringExtra("aula");
        String hora = getIntent().getStringExtra("hora");

        String mensaje = "Haz Cancelado exitosamente El Aula " + edificio + "-" + aula +
                " el día 16 de mayo a las " + hora + ".";

        mensajeTextView.setText(mensaje);

        regresarButton.setOnClickListener(v -> finish()); // O volver a la pantalla inicial
    }
}
