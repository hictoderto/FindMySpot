package com.example.findmyspot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InformationScreen extends AppCompatActivity {
    private TextView titulo,capacidad,tipo;
    OkHttpClient client = new OkHttpClient();
    ArrayList<String> reglas;
    private int id;
    private ImageView imagen,btn_image;
    private ArrayAdapter<String> adapter;
    private ListView listaReglas;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private TextView btnOptions,home;
    private Button btn_back,btn_continue;
    private String nombrell;


    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        Intent intent = getIntent();
        reglas = new ArrayList<>();

        btnOptions = findViewById(R.id.btn_options);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        id = intent.getIntExtra("id",0);
        titulo = findViewById(R.id.tvTitulo);
        capacidad =findViewById(R.id.tvSubtitulo);
        tipo = findViewById(R.id.textView8);
        imagen = findViewById(R.id.ivLugar);
        listaReglas = findViewById(R.id.listaReglas);
        home = findViewById(R.id.btn_home);
        btn_back = findViewById(R.id.btnRegresar);
        btn_image = findViewById(R.id.profile_image);
        btn_continue = findViewById(R.id.btnContinuar);


        getreglas();
        getInformationApi();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, reglas);
        listaReglas.setAdapter(adapter);
        String base64String = getSharedPreferences("session", MODE_PRIVATE)
                .getString("imagen", null);

        if (base64String != null) {
            // Convertir de Base64 a byte[]
            byte[] imageBytes = Base64.decode(base64String, Base64.DEFAULT);

            // Convertir byte[] a Bitmap
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);



            Glide.with(this)
                    .load(bitmap)
                    .circleCrop()
                    .into(btn_image);

        }
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
                Toast.makeText(InformationScreen.this, "busqueda seleccionado", Toast.LENGTH_SHORT).show();
                // Aquí puedes hacer navegación, etc.
            } else if (id == R.id.nav_cancel) {
                Toast.makeText(InformationScreen.this, "cancelar seleccionado", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_about_us) {
                Toast.makeText(InformationScreen.this, "nosotros seleccionada", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(navigationView);
            return true;
        });
        home .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformationScreen.this, PrincipalSecreen.class);
                startActivity(intent);
                finish();
            }
        });
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformationScreen.this, ReservarAulaActivity.class);
                intent.putExtra("nombre", nombrell);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InformationScreen.this, AcountScreen.class);
                startActivity(intent);
            }
        });
    }
    private void getreglas(){
        Request request = new Request.Builder()
                .url(Constatnts.URL+"/lugar/"+id+"/reglas")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(InformationScreen.this, "Error de red", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(InformationScreen.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                String json = response.body().string();
                try {
                    JSONObject respuesta = new JSONObject(json);
                    boolean success = respuesta.getBoolean("success");
                    if (success){
                        JSONArray array = respuesta.getJSONArray("reglas");
                        for(int i = 0; i < array.length(); i++){
                            JSONObject obj = array.getJSONObject(i);
                            String nombre = obj.getString("nombre");
                            String descripcion = obj.getString("descripcion");
                            boolean valida = obj.getBoolean("enfuncion");
                            if(valida){
                                reglas.add(nombre+": "+descripcion);
                            }
                        }
                        runOnUiThread(() -> adapter.notifyDataSetChanged());
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }


            }
        });
    }
    private void getInformationApi(){
        Request request = new Request.Builder()
                .url(Constatnts.URL+"/lugares/"+id)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(InformationScreen.this, "Error de red", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        Toast.makeText(InformationScreen.this, "Error del servidor", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }
                String json = response.body().string();

                runOnUiThread(() -> {

                    try {

                        JSONObject jsonA = new JSONObject(json);
                        boolean success = jsonA.getBoolean("success");
                        if (success){
                            JSONObject lugar = jsonA.getJSONObject("lugar");
                            nombrell = lugar.getString("nombre");
                            String tipoLugar = lugar.getString("tipo_lugar");
                            String imagenBase64 = lugar.getString("imagen");
                            int capacidadl = lugar.getInt("capacidad");
                            byte[] decodedString = Base64.decode(imagenBase64, Base64.DEFAULT);
                            Bitmap imagenBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imagen.setImageBitmap(imagenBitmap);
                            titulo.setText(nombrell);
                            tipo.setText(tipoLugar);
                            capacidad.setText(String.valueOf(capacidadl));
                        }
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }

                });
            }
        });
    }
}
