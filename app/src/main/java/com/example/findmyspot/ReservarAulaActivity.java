package com.example.findmyspot;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ReservarAulaActivity extends AppCompatActivity {

    private EditText nombreEditText, emailEditText,placeName;
    private Button fechaButton, horaButton, continuarButton,btnRegresar;
    private CheckBox terminosCheckBox;
    private TextView fechaSeleccionadaTextView, horaSeleccionadaTextView;

    private String fechaSeleccionada = "";
    private String horaSeleccionada = "";
    private String idUser,nombrel;
    private int idPLace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservar_aula);

        nombreEditText = findViewById(R.id.editTextNombre);
        emailEditText = findViewById(R.id.editTextEmail);
        placeName = findViewById(R.id.editPlacName);
        fechaButton = findViewById(R.id.buttonFecha);
        horaButton = findViewById(R.id.buttonHora);
        continuarButton = findViewById(R.id.btnContinuar);
        terminosCheckBox = findViewById(R.id.checkboxTerminos);
        fechaSeleccionadaTextView = findViewById(R.id.textViewFechaSeleccionada);
        horaSeleccionadaTextView = findViewById(R.id.textViewHoraSeleccionada);
        btnRegresar = findViewById(R.id.btnRegresar);
        fechaButton.setOnClickListener(v -> mostrarSelectorFecha());
        horaButton.setOnClickListener(v -> mostrarSelectorHora());

        continuarButton.setOnClickListener(v -> {
            if (validarCampos()) {
                crearreservacion();
            }
        });
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        String nombre = getSharedPreferences("session", MODE_PRIVATE)
                .getString("nombreUsuario", null);
        String correo = getSharedPreferences("session", MODE_PRIVATE)
                .getString("correo", null);
         idUser = getSharedPreferences("session",MODE_PRIVATE)
                .getString("id",null);
        Intent intent = getIntent();
        nombrel = intent.getStringExtra("nombre");
         idPLace = intent.getIntExtra("id", 0);
        placeName.setText(nombrel);
        nombreEditText.setText(nombre);
        emailEditText.setText(correo);
        placeName.setEnabled(false);
        nombreEditText.setEnabled(false);
        emailEditText.setEnabled(false);
    }
    private void crearreservacion(){
        OkHttpClient client = new OkHttpClient();
        RequestBody formBody = new FormBody.Builder()
                .add("idusuario", idUser)
                .add("idlugar", String.valueOf(idPLace))
                .add("fechareservacion",fechaSeleccionada)
                .add("horareservacion",horaSeleccionada)
                .build();
        Request request = new Request.Builder()
                .url(Constatnts.URL+"/reservaciones")  // Cambia la URL
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // Error de conexión
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String respuesta = response.body().string();

                        JSONObject json = new JSONObject(respuesta);
                        boolean success = json.getBoolean("success");

                        if (success) {
                            JSONObject reservacion = json.getJSONObject("reservacion");
                            String id = reservacion.getString("id");
                            runOnUiThread(() -> {
                                Intent intent = new Intent(ReservarAulaActivity.this, ReservaExitosaActivity.class);
                                intent.putExtra("lugar", nombrel);
                                intent.putExtra("fecha", fechaSeleccionada);
                                intent.putExtra("hora", horaSeleccionada);
                                intent.putExtra("id", id);

                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), "error1", Toast.LENGTH_SHORT).show());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
                    }

                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show());

                }
            }
        });


    }

    private void mostrarSelectorFecha() {
        final Calendar calendario = Calendar.getInstance();
        int anio = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    fechaSeleccionada = dayOfMonth + "/" + (month + 1) + "/" + year;
                    fechaSeleccionadaTextView.setText(fechaSeleccionada);
                }, anio, mes, dia);
        datePickerDialog.show();
    }

    private void mostrarSelectorHora() {
        final Calendar calendario = Calendar.getInstance();
        int hora = calendario.get(Calendar.HOUR_OF_DAY);
        int minuto = calendario.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, minute1) -> {
                    horaSeleccionada = String.format("%02d:%02d", hourOfDay, minute1);
                    horaSeleccionadaTextView.setText(horaSeleccionada);
                }, hora, minuto, false);
        timePickerDialog.show();
    }

    private boolean validarCampos() {
        if (nombreEditText.getText().toString().isEmpty() ||
                emailEditText.getText().toString().isEmpty() ||
                fechaSeleccionada.isEmpty() ||
                horaSeleccionada.isEmpty() ||
                !terminosCheckBox.isChecked()) {

            Toast.makeText(this, "Por favor completa todos los campos y acepta los términos.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}