package com.example.findmyspot;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistroScreen extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageView;
    private Button btnCrearCuenta,btngoback;
    private EditText codigo,nombre,apellidos,telefono,correo,password;
    private Uri imagenUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro); // Usa tu layout real

        imageView = findViewById(R.id.miImagen); // Asegúrate de tener un ImageView con ese ID

        Button btnSeleccionar = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionar.setOnClickListener(v -> abrirGaleria());
        btnCrearCuenta = findViewById(R.id.crearcuenta);
        btngoback = findViewById(R.id.logingo);
        codigo = findViewById(R.id.Enterusername);
        nombre = findViewById(R.id.nombre);
        apellidos = findViewById(R.id.apellido);
        telefono = findViewById(R.id.telefono);
        correo = findViewById(R.id.correo);
        password = findViewById(R.id.contrasenia);
        btnCrearCuenta.setOnClickListener(v -> craerCuenta());
        btngoback.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroScreen.this, LoginScreen.class);
            startActivity(intent);
            finish();
        });
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imagenUri = data.getData();
            imageView.setImageURI(imagenUri);
        }
    }
    private void craerCuenta(){
        String id,nombreL,apellido,celular,email,contrasenia;
        id = codigo.getText().toString();
        nombreL = nombre.getText().toString();
        apellido = apellidos.getText().toString();
        celular = telefono.getText().toString();
        email = correo.getText().toString();
        contrasenia = password.getText().toString();
        OkHttpClient client = new OkHttpClient();


        // Crear cuerpo de la petición POST con usuario y password
        MultipartBody.Builder multipartBuilder  = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("codigo", id)
                .addFormDataPart("password", contrasenia)
                .addFormDataPart("nombre", nombreL)
                .addFormDataPart("apellidos", apellido)
                .addFormDataPart("telefono", celular)
                .addFormDataPart("correo", email);

        if (imagenUri != null) {
            try {
                String fileName = "imagen.jpg"; // o usa un nombre dinámico
                // Convertir el Uri en byte[]
                InputStream inputStream = getContentResolver().openInputStream(imagenUri);
                byte[] imagenBytes = new byte[inputStream.available()];
                inputStream.read(imagenBytes);
                inputStream.close();

                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), imagenBytes);
                multipartBuilder.addFormDataPart("imagen", fileName, fileBody);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        RequestBody requestBody = multipartBuilder.build();

        Request request = new Request.Builder()
                .url("http://192.168.1.101:4000/usuario")  // Cambia la URL
                .post(requestBody)
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
                            String mensaje = json.getString("message");
                            JSONObject user = json.getJSONObject("user");
                            String nombre = user.getString("nombre");

                            runOnUiThread(() -> {
                                Intent intent = new Intent(RegistroScreen.this, LoginScreen.class);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                Toast.makeText(getApplicationContext(), "error1", Toast.LENGTH_SHORT).show();
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
                    }

                } else {
                    String respuesta = response.body().string();

                    try {
                        JSONObject json = new JSONObject(respuesta);
                        String mensaje = json.getString("error");
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), "problema: "+mensaje, Toast.LENGTH_SHORT).show());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

}
