package com.example.findmyspot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class AcountScreen extends AppCompatActivity {
    private Button btn_close_Secion;
    private EditText codigo,nombre,apellido,telefono,correo;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);
        String base64String = getSharedPreferences("session", MODE_PRIVATE)
                .getString("imagen", null);
        String codigos,nombres,apellidos,telefonos,correos;
        codigos = getSharedPreferences("session", MODE_PRIVATE)
                .getString("id", null);
        nombres = getSharedPreferences("session", MODE_PRIVATE)
                .getString("nombreUsuario", null);
        apellidos = getSharedPreferences("session", MODE_PRIVATE)
                .getString("apellidos", null);
        telefonos = getSharedPreferences("session", MODE_PRIVATE)
                .getString("telefono", null);
        correos = getSharedPreferences("session", MODE_PRIVATE)
                .getString("correo", null);
        codigo = findViewById(R.id.codigover);
        nombre =findViewById(R.id.nombrever);
        apellido = findViewById(R.id.apellidosver);
        telefono = findViewById(R.id.telefonover);
        correo = findViewById(R.id.correover);
        btn_close_Secion = findViewById(R.id.button3);
        codigo.setText(codigos);
        nombre.setText(nombres);
        apellido.setText(apellidos);
        telefono.setText(telefonos);
        correo.setText(correos);
        codigo.setEnabled(false);
        nombre.setEnabled(false);
        apellido.setEnabled(false);
        telefono.setEnabled(false);
        correo.setEnabled(false);

        if (base64String != null) {
            // Convertir de Base64 a byte[]
            byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);

            // Convertir byte[] a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            // Mostrar en un ImageView
            ImageView imageView = findViewById(R.id.profile_imagevers);

            Glide.with(this)
                    .load(bitmap)
                    .circleCrop()
                    .into(imageView);

        }
        btn_close_Secion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CERRAR SESIÓN
                getSharedPreferences("session", MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();

                // Regresar a Login
                Intent intent = new Intent(AcountScreen.this, LoginScreen.class);
                startActivity(intent);
                finish();

            }
        });
    }

}
