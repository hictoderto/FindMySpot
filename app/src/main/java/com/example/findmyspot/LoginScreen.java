package com.example.findmyspot;

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
    private TextView forgotPassword;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        loginBtn.setOnClickListener(v -> {
            String user = username.getText().toString();
            String pass = password.getText().toString();
            // Aquí puedes validar los datos y hacer login

            hacerLogin(user,pass);
        });

        registerBtn.setOnClickListener(v -> {
            hacerPingServidor();
        });


        forgotPassword.setOnClickListener(v -> {
            // Ir a recuperación de contraseña
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
                .url("http://192.168.1.101:4000/login")  // Cambia la URL
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
                    String respuesta = response.body().string();

                    // Aquí puedes parsear la respuesta JSON si quieres

                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Login exitoso", Toast.LENGTH_SHORT).show());
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), respuesta, Toast.LENGTH_SHORT).show());
                    // Por ejemplo, abrir otra actividad
                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
    public void hacerPingServidor() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("http://192.168.1.101:4000/") // La ruta raíz de tu API Flask
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(getApplicationContext(), "Error:"+e, Toast.LENGTH_SHORT).show());
                        System.out.println(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String respuesta = response.body().string();
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Respuesta del servidor: " + respuesta, Toast.LENGTH_LONG).show());
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(getApplicationContext(), "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


}
