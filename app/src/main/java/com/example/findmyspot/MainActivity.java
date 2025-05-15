package com.example.findmyspot;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private TextView chargingText;
    private boolean isLoading = true; // Para detener la animación cuando cargue
    private final String[] dots = {"Cargando", "Cargando.", "Cargando..", "Cargando..."};
    private int dotIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        chargingText = findViewById(R.id.textView);

        // Iniciar animación de texto
        animateChargingText();

        // Simula carga con hilo
        new Thread(() -> {
            for (int progress = 0; progress <= 100; progress++) {
                try {
                    Thread.sleep(50); // Simula carga
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int finalProgress = progress;
                runOnUiThread(() -> progressBar.setProgress(finalProgress));
            }

            // Cuando termine la carga, ocultar barra y detener animación
            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                isLoading = false;
                chargingText.setText("Carga completa");
                // Por ejemplo, poner 16dp de padding en todos lados
                int paddingInDp = 75;
                float scale = getResources().getDisplayMetrics().density;
                int paddingInPx = (int) (paddingInDp * scale + 0.5f);

                chargingText.setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx);

                Intent intent = new Intent(MainActivity.this, LoginScreen.class);
                startActivity(intent);
                finish();

            });
        }).start();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para animar el texto
    private void animateChargingText() {
        chargingText.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isLoading) {
                    chargingText.setText(dots[dotIndex]);
                    dotIndex = (dotIndex + 1) % dots.length;
                    chargingText.postDelayed(this, 500); // cada 500 ms
                }
            }
        }, 500);
    }
}
