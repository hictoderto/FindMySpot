package com.example.findmyspot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class DetalleReservaCancelacionActivity extends AppCompatActivity {

    private TextView folioTextView;
    private EditText nombreEditText, edificioEditText, aulaEditText, horaEditText;
    private Button regresarButton, cancelarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reserva_cancelacion);

        folioTextView = findViewById(R.id.textViewFolio);
        nombreEditText = findViewById(R.id.editTextNombre);
        edificioEditText = findViewById(R.id.editTextEdificio);
        aulaEditText = findViewById(R.id.editTextAula);
        horaEditText = findViewById(R.id.editTextHora);
        regresarButton = findViewById(R.id.btnRegresar);
        cancelarButton = findViewById(R.id.btnCancelarReserva);

        // Obtener datos enviados desde la pantalla anterior
        String folio = getIntent().getStringExtra("folio");
        String nombre = getIntent().getStringExtra("nombre");
        String edificio = getIntent().getStringExtra("edificio");
        String aula = getIntent().getStringExtra("aula");
        String hora = getIntent().getStringExtra("hora");

        folioTextView.setText(folio);
        nombreEditText.setText(nombre);
        edificioEditText.setText(edificio);
        aulaEditText.setText(aula);
        horaEditText.setText(hora);

        // Deshabilitar campos
        nombreEditText.setEnabled(false);
        edificioEditText.setEnabled(false);
        aulaEditText.setEnabled(false);
        horaEditText.setEnabled(false);

        regresarButton.setOnClickListener(v -> finish());

        cancelarButton.setOnClickListener(v -> {
            // Aquí puedes realizar lógica de cancelación real (API/DB)
            Intent intent = new Intent(DetalleReservaCancelacionActivity.this, CancelacionExitosaActivity.class);
            intent.putExtra("edificio", edificio);
            intent.putExtra("aula", aula);
            intent.putExtra("hora", hora);
            startActivity(intent);
            finish();
        });
    }
}