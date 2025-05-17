package com.example.findmyspot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout; // IMPORTANTE

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PrincipalSecreen extends AppCompatActivity {

    LinearLayout container;
    SwipeRefreshLayout swipeRefreshLayout; // <- nuevo
    OkHttpClient client = new OkHttpClient();
    LayoutInflater inflater;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView btnOptions;
    private ImageView btnAcount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal);

        container = findViewById(R.id.container);
        btnOptions = findViewById(R.id.btn_options);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        btnAcount = findViewById(R.id.profile_image);
        swipeRefreshLayout = findViewById(R.id.swipeRefresh); // ← lo conectas aquí
        inflater = LayoutInflater.from(this);
        String base64String = getSharedPreferences("session", MODE_PRIVATE)
                .getString("imagen", null);

        if (base64String != null) {
            // Convertir de Base64 a byte[]
            byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);

            // Convertir byte[] a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            // Mostrar en un ImageView
            ImageView imageView = findViewById(R.id.profile_image);

            Glide.with(this)
                    .load(bitmap)
                    .circleCrop()
                    .into(imageView);

        }

        cargarLugaresDesdeAPI(); // carga inicial

        // Configura el "tirar para recargar"
        swipeRefreshLayout.setOnRefreshListener(() -> {
            cargarLugaresDesdeAPI(); // recarga cuando haces swipe
        });
        btnAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrincipalSecreen.this, AcountScreen.class);
                startActivity(intent);
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
                Toast.makeText(PrincipalSecreen.this, "busqueda seleccionado", Toast.LENGTH_SHORT).show();
                // Aquí puedes hacer navegación, etc.
            } else if (id == R.id.nav_cancel) {
                Intent intent = new Intent(PrincipalSecreen.this, BuscarReservacionActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_about_us) {
                Toast.makeText(PrincipalSecreen.this, "nosotros seleccionada", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(navigationView);
            return true;
        });


    }

    private void cargarLugaresDesdeAPI() {
        Request request = new Request.Builder()
                .url(Constatnts.URL+"/lugares")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(PrincipalSecreen.this, "Error de red", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false); // detener animación
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(PrincipalSecreen.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false); // detener animación
                    });
                    return;
                }

                String json = response.body().string();
                runOnUiThread(() -> {
                    mostrarLugares(json);
                    swipeRefreshLayout.setRefreshing(false); // detener animación al terminar
                });
            }
        });
    }

    private void mostrarLugares(String json) {
        try {
            container.removeAllViews();

            JSONObject jsonA = new JSONObject(json);
            boolean success = jsonA.getBoolean("success");
            if(success) {
                JSONArray array = jsonA.getJSONArray("lugares");
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.getJSONObject(i);
                    int id = obj.getInt("id_lugar");
                    String nombre = obj.getString("nombre");
                    String tipo = obj.getString("tipo_lugar");
                    int capacidad = obj.getInt("capacidad");
                    String imagenBase64 = obj.getString("imagen");

                    byte[] decodedString = Base64.decode(imagenBase64, Base64.DEFAULT);
                    Bitmap imagenBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    View itemView = inflater.inflate(R.layout.item_card, container, false);

                    TextView titulo = itemView.findViewById(R.id.titulo);
                    TextView sub1 = itemView.findViewById(R.id.sub1);
                    TextView sub2 = itemView.findViewById(R.id.sub2);
                    ImageView imagen = itemView.findViewById(R.id.imagen);

                    titulo.setText(nombre);
                    sub1.setText(tipo);
                    sub2.setText("1000/" + capacidad);
                    imagen.setImageBitmap(imagenBitmap);

                    itemView.setOnClickListener(v -> {
                        Intent intent = new Intent(PrincipalSecreen.this, InformationScreen.class);
                        intent.putExtra("id",id);
                        startActivity(intent);
                    });

                    container.addView(itemView);
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
