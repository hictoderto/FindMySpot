package com.example.findmyspot;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginScreen extends AppCompatActivity {
    private EditText username, password;
    private Button loginBtn, registerBtn;
    private TextView forgotPassword,errorMesage;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isLoggedIn = getSharedPreferences("session", MODE_PRIVATE)
                .getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // Ir directo a la pantalla principal
            Intent intent = new Intent(LoginScreen.this, PrincipalSecreen.class);
            startActivity(intent);
            finish(); // Cierra esta pantalla
            return;
        }
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        forgotPassword = findViewById(R.id.forgotPassword);
        errorMesage = findViewById(R.id.errorMesage);

        loginBtn.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();
            // Aquí puedes validar los datos y hacer login

            hacerLogin(user,pass);
        });

        registerBtn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginScreen.this, RegistroScreen.class);
            startActivity(intent);

        });


        forgotPassword.setOnClickListener(v -> {
        });
        username.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorMesage.setText(""); // Borra el mensaje de error cuando se escribe algo
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        password.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                errorMesage.setText(""); // Borra el mensaje de error cuando se escribe algo
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

    }
    public void hacerLogin(String usuario, String password) {
        OkHttpClient client = new OkHttpClient();


        // Crear cuerpo de la petición POST con usuario y password
        RequestBody formBody = new FormBody.Builder()
                .add("codigo", usuario)
                .add("password", password)
                .build();

        Request request = new Request.Builder()
                .url(Constatnts.URL+"/login")  // Cambia la URL
                .post(formBody)
                .build();

        // Ejecutar petición asíncrona
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
                                Intent intent = new Intent(LoginScreen.this, PrincipalSecreen.class);
                                startActivity(intent);
                                finish();
                            });
                        } else {
                            runOnUiThread(() -> {
                                errorMesage.setText("Usuario o contraseña incorrectos");
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show());
                    }

                } else {
                    runOnUiThread(() -> errorMesage.setText( "Usuario o contraseña incorrectos"));

                }
            }
        });
    }



}
