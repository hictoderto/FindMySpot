package com.example.findmyspot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AcountScreen extends AppCompatActivity {
    private TextView home;
    private Button btn_close_Secion,btn_back,btn_change;
    private EditText codigo,nombre,apellidol,telefono,correo;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView btnOptions;
    private ImageView btn_image;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imagenUri;
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
        apellidol = findViewById(R.id.apellidosver);
        telefono = findViewById(R.id.telefonover);
        correo = findViewById(R.id.correover);
        btn_close_Secion = findViewById(R.id.button3);
        btn_change = findViewById(R.id.button2);
        btn_back = findViewById(R.id.button);
        home = findViewById(R.id.btn_homever);
        btnOptions = findViewById(R.id.btn_optionsver);
        drawerLayout = findViewById(R.id.drawer_layoutver);
        navigationView = findViewById(R.id.nav_viewver);
        btn_image = findViewById(R.id.profile_imagevers);


        codigo.setText(codigos);
        nombre.setText(nombres);
        apellidol.setText(apellidos);
        telefono.setText(telefonos);
        correo.setText(correos);

        codigo.setEnabled(false);
        nombre.setEnabled(false);
        apellidol.setEnabled(false);
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
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        home .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcountScreen.this, PrincipalSecreen.class);
                startActivity(intent);
                finish();
            }
        });
        btnOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(navigationView)) {
                    drawerLayout.closeDrawer(navigationView);
                } else {
                    drawerLayout.openDrawer(navigationView);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_search) {
                Toast.makeText(AcountScreen.this, "busqueda seleccionado", Toast.LENGTH_SHORT).show();
                // Aquí puedes hacer navegación, etc.
            } else if (id == R.id.nav_cancel) {
                Toast.makeText(AcountScreen.this, "cancelar seleccionado", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_about_us) {
                Toast.makeText(AcountScreen.this, "nosotros seleccionada", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(navigationView);
            return true;
        });
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeHandler();
            }
        });

    }

    private void changeHandler (){
        nombre.setEnabled(true);
        apellidol.setEnabled(true);
        telefono.setEnabled(true);
        correo.setEnabled(true);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombre.setEnabled(false);
                apellidol.setEnabled(false);
                telefono.setEnabled(false);
                correo.setEnabled(false);
                btn_image.setOnClickListener(null);
                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            }

        });
        btn_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyAcount();
            }
        });
        btn_image.setOnClickListener(v -> abrirGaleria());

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
            Glide.with(this)
                    .load(imagenUri)
                    .circleCrop()
                    .into(btn_image);

        }
    }
    private void ModifyAcount(){
        String id,nombreL,apellido,celular,email,contrasenia;
        id = codigo.getText().toString();
        nombreL = nombre.getText().toString();
        apellido = apellidol.getText().toString();
        celular = telefono.getText().toString();
        email = correo.getText().toString();
        OkHttpClient client = new OkHttpClient();
        // Crear cuerpo de la petición POST con usuario y password
        MultipartBody.Builder multipartBuilder  = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("codigo", id)
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
                .url(Constatnts.URL+"/usuario"+"/"+id)  // Cambia la URL
                .put(requestBody)
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
                            JSONObject user = json.getJSONObject("user");
                            String nombre = user.getString("nombre");
                            String id = user.getString("codigo");
                            String imagen = user.getString("imagen");
                            String apellidos = user.getString("apellidos");
                            String telefono = user.getString("telefono");
                            String correo = user.getString("correo");
                            getSharedPreferences("session", MODE_PRIVATE)
                                    .edit()
                                    .putBoolean("isLoggedIn", true)
                                    .putString("nombreUsuario", nombre)
                                    .putString("id",id)
                                    .putString("imagen",imagen)
                                    .putString("apellidos",apellidos)
                                    .putString("telefono",telefono)
                                    .putString("correo",correo)
                                    .apply();

                            runOnUiThread(() -> {
                                Intent intent = getIntent();
                                finish();
                                startActivity(intent);

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
