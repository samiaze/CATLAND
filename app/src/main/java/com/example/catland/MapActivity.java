package com.example.catland;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient locationClient;
    private ProgressBar loadingSpinner;
    private Button retryButton;
    private Location userLocation;

    private boolean hasLoadedVets = false;
    private JSONArray cachedVetResults = null;
    private List<com.google.android.gms.maps.model.Polyline> currentPolylines;


    private static final String TAG = "MapActivity";
    private static final String GOOGLE_API_KEY = "AIzaSyCL7R3pB8aAuFh4QX7_Kxr0mZI2lmO8ppQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = findViewById(R.id.mapToolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
            getSupportActionBar().setTitle("Vétérinaires à proximité");
        }

        loadingSpinner = findViewById(R.id.loadingSpinner);
        retryButton = findViewById(R.id.retryButton);

        currentPolylines = new java.util.ArrayList<>();


        locationClient = LocationServices.getFusedLocationProviderClient(this);

        retryButton.setOnClickListener(v -> {
            hasLoadedVets = false;
            cachedVetResults = null;
            fetchLocation();
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        fetchLocation();
    }

    private void fetchLocation() {
        loadingSpinner.setVisibility(View.VISIBLE);
        retryButton.setVisibility(View.GONE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission manquante", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        locationClient.getLastLocation().addOnSuccessListener(location -> {
            loadingSpinner.setVisibility(View.GONE);
            if (location != null) {
                userLocation = location;
                LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f));
                mMap.addMarker(new MarkerOptions().position(userLatLng).title("Vous êtes ici").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                searchNearbyVeterinarians(userLatLng);
            } else {
                retryButton.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Impossible d'obtenir la localisation", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            loadingSpinner.setVisibility(View.GONE);
            retryButton.setVisibility(View.VISIBLE);
            Log.e(TAG, "Location fetch failed", e);
        });
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void searchNearbyVeterinarians(LatLng userLatLng) {
        if (hasLoadedVets && cachedVetResults != null) {
            renderVetMarkersFromCache(cachedVetResults);
            return;
        }
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + userLatLng.latitude + "," + userLatLng.longitude +
                "&radius=200000&type=veterinary_care&key=" + GOOGLE_API_KEY;

        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) responseBuilder.append(line);
                conn.disconnect();

                JSONObject response = new JSONObject(responseBuilder.toString());
                JSONArray results = response.getJSONArray("results");
                cachedVetResults = results;
                hasLoadedVets = true;

                runOnUiThread(() -> renderVetMarkersFromCache(results));

            } catch (Exception e) {
                Log.e(TAG, "Error fetching nearby vets", e);
            }
        }).start();
    }

    @SuppressLint("PotentialBehaviorOverride")
    private void renderVetMarkersFromCache(JSONArray results) {
        for (int i = 0; i < results.length(); i++) {
            try {
                JSONObject place = results.getJSONObject(i);
                JSONObject loc = place.getJSONObject("geometry").getJSONObject("location");
                LatLng vetLatLng = new LatLng(loc.getDouble("lat"), loc.getDouble("lng"));
                String name = place.getString("name");

                boolean isOpenNow = false;
                if (place.has("opening_hours")) {
                    JSONObject openingHours = place.getJSONObject("opening_hours");
                    isOpenNow = openingHours.optBoolean("open_now", false);
                }

                Bundle tagData = new Bundle();
                tagData.putDouble("lat", vetLatLng.latitude);
                tagData.putDouble("lng", vetLatLng.longitude);
                tagData.putBoolean("open_now", isOpenNow);
                final Bitmap originalIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo);
                final Bitmap scaled = Bitmap.createScaledBitmap(originalIcon, 100, 100, false);
                final BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(scaled);
                Marker marker = mMap.addMarker(new MarkerOptions().position(vetLatLng).title(name).icon(icon));

                if (marker != null) marker.setTag(tagData);

            } catch (Exception e) {
                Log.e("MapActivity", "Error rendering cached vet data", e);
            }
        }

        mMap.setOnMarkerClickListener(marker -> {
            if (!"Vous êtes ici".equals(marker.getTitle())) {
                Bundle tag = (Bundle) marker.getTag();
                if (tag != null) {
                    LatLng destination = new LatLng(tag.getDouble("lat"), tag.getDouble("lng"));
                    boolean isOpenNow = tag.getBoolean("open_now", false);
                    drawRoute(userLocation, destination, marker.getTitle(), isOpenNow);
                }
            }
            return false;
        });
    }


    @SuppressLint("SetTextI18n")
    private void drawRoute(Location originLoc, LatLng destination, String vetName, boolean isOpenNow) {
        LatLng origin = new LatLng(originLoc.getLatitude(), originLoc.getLongitude());

        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origin.latitude + "," + origin.longitude + "&destination=" + destination.latitude + "," + destination.longitude + "&key=" + GOOGLE_API_KEY;

        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder responseBuilder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) responseBuilder.append(line);
                conn.disconnect();

                JSONObject json = new JSONObject(responseBuilder.toString());
                JSONArray routes = json.getJSONArray("routes");
                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String points = overviewPolyline.getString("points");
                    List<LatLng> decodedPath = PolyUtil.decode(points);

                    JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
                    JSONObject durationObj = leg.getJSONObject("duration");
                    int durationValue = durationObj.getInt("value");
                    int carMinutes = (int) Math.ceil(durationValue / 60.0);
                    int motoMinutes = (int) Math.ceil(carMinutes * 1.33);
                    int walkMinutes = (int) Math.ceil(carMinutes * 8.0);

                    runOnUiThread(() -> {
                        clearPolylines();
                        com.google.android.gms.maps.model.Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(decodedPath).width(10f).color(Color.BLUE));
                        currentPolylines.add(polyline);

                        View dialogView = getLayoutInflater().inflate(R.layout.vet_info_dialog, null);
                        ((TextView) dialogView.findViewById(R.id.vetName)).setText(vetName);
                        ((TextView) dialogView.findViewById(R.id.timeCar)).setText("En voiture : " + formatDuration(carMinutes));
                        ((TextView) dialogView.findViewById(R.id.timeMoto)).setText("En moto : " + formatDuration(motoMinutes));
                        ((TextView) dialogView.findViewById(R.id.timeWalk)).setText("À pied : " + formatDuration(walkMinutes));

                        TextView openNowText = dialogView.findViewById(R.id.openNowText);
                        View statusIndicator = dialogView.findViewById(R.id.statusIndicator);
                        LinearLayout statusSection = (LinearLayout) openNowText.getParent();

                        if (isOpenNow) {
                            openNowText.setText("OUVERT MAINTENANT");
                            openNowText.setTextColor(getResources().getColor(R.color.mint_green));
                            statusIndicator.setBackgroundResource(R.drawable.status_indicator_open);
                            statusSection.setBackgroundResource(R.drawable.status_background);
                        } else {
                            openNowText.setText("FERMÉ MAINTENANT");
                            openNowText.setTextColor(getResources().getColor(R.color.coral));
                            statusIndicator.setBackgroundResource(R.drawable.status_indicator_closed);
                            statusSection.setBackgroundResource(R.drawable.status_background_closed);
                        }

                        new AlertDialog.Builder(MapActivity.this).setView(dialogView).setPositiveButton("Fermer", null).setNeutralButton("Naviguer", (dialog, which) -> openGoogleMapsNavigation(destination)).show();
                    });
                }

            } catch (Exception e) {
                Log.e("MapActivity", "Error drawing route", e);
            }
        }).start();
    }

    private String formatDuration(int minutes) {
        if (minutes < 60) return minutes + " min";
        int hours = minutes / 60;
        int remainingMinutes = minutes % 60;
        if (remainingMinutes == 0) return hours + " h";
        return hours + " h " + remainingMinutes + " min";
    }


    private void openGoogleMapsNavigation(LatLng destination) {
        String uri = "google.navigation:q=" + destination.latitude + "," + destination.longitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "Google Maps n'est pas installé.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.action_refresh) {
            clearPolylines();
            hasLoadedVets = false;
            cachedVetResults = null;
            fetchLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void clearPolylines() {
        if (currentPolylines != null) {
            for (com.google.android.gms.maps.model.Polyline polyline : currentPolylines) {
                polyline.remove();
            }
            currentPolylines.clear();
        }
    }
}
