package com.example.catland;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.catland.Categories.CategoriesMainActivity;
import com.example.catland.Sante.SanteMainActivity;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 1001;
    LinearLayout btnScanner, btnAuthentifier, btnConseilSante, btnCategories, btnFindVets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnScanner = findViewById(R.id.btnScanner);
        btnAuthentifier = findViewById(R.id.btnAuthentifier);
        btnConseilSante = findViewById(R.id.btnConseilSante);
        btnCategories = findViewById(R.id.btnCategories);
        btnFindVets = findViewById(R.id.btnFindVets);

        btnScanner.setOnClickListener(view -> startActivity(new Intent(this, ReadNFCActivity.class)));
        btnAuthentifier.setOnClickListener(view -> startActivity(new Intent(this, OCRAuthChoiceActivity.class)));
        btnConseilSante.setOnClickListener(view -> startActivity(new Intent(this, SanteMainActivity.class)));
        btnCategories.setOnClickListener(view -> startActivity(new Intent(this, CategoriesMainActivity.class)));
        btnFindVets.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
            } else {
                checkLocationEnabledAndProceed();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkLocationEnabledAndProceed();
            } else {
                Toast.makeText(this, "Permission refusée. L'application a besoin de la localisation.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkLocationEnabledAndProceed() {
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Toast.makeText(this, "Veuillez activer la localisation.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else {
            startActivity(new Intent(MainActivity.this, MapActivity.class));
        }
    }

}
