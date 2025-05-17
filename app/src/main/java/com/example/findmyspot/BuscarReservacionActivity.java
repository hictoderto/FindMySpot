package com.example.findmyspot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BuscarReservacionActivity extends AppCompatActivity {

    private EditText folioEditText;
    private Button buscarButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_reservacion);

        folioEditText = findViewById(R.id.editTextFolio);
        buscarButton = findViewById(R.id.btnBuscarFolio);

        buscarButton.setOnClickListener(v -> {
            String folio = folioEditText.getText().toString().trim();

            if (folio.isEmpty()) {
                Toast.makeText(this, "Por favor ingresa un folio válido.", Toast.LENGTH_SHORT).show();
            } else {
                // Aquí puedes consultar la base de datos o API si es necesario.
                // Por ahora enviaremos datos de prueba a la siguiente actividad.

                Intent intent = new Intent(BuscarReservacionActivity.this, DetalleReservaCancelacionActivity.class);
                intent.putExtra("folio", folio);
                intent.putExtra("nombre", "Juanito Jicamas");
                intent.putExtra("edificio", "X");
                intent.putExtra("aula", "16");
                intent.putExtra("hora", "4:00 P.M.");
                startActivity(intent);
                finish();
            }
        });
    }
}