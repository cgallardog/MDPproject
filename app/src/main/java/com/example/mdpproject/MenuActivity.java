package com.example.mdpproject;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.mdpproject.adapter.PlacesArrayAdapter;
import com.example.mdpproject.model.PlacesData;
import com.example.mdpproject.thread.LoadWebContents;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class MenuActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private String logTag; // to clearly identify logs
    Button bMeasure, bSearch;
    Spinner place_views;
    public static final String LOGSLOADWEBCONTENT = "LOGSLOADWEBCONTENT"; // to clearly identify logs
    private static final String URL_GARDEN = "https://datos.madrid.es/egob/catalogo/200761-0-parques-jardines.json";
    private static final String URL_SPORTCENTRE = "https://datos.madrid.es/egob/catalogo/200186-0-polideportivos.json";
    private static final String URL_SWIMMINGPOOL = "https://datos.madrid.es/egob/catalogo/210227-0-piscinas-publicas.json";
    private static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
    ExecutorService es;
    private ArrayList<String> placesNames_ArrayList;
    private ArrayList<String> placesLatitude;
    private ArrayList<String> placesLongitude;

    String name, age, gender, height, weight;
    String[] permissions;
    private ArrayList<String> placesStreet_ArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Build the logTag with the Thread and Class names:
        logTag = LOGSLOADWEBCONTENT + ", Thread = " + Thread.currentThread().getName() + ", Class = " +
                this.getClass().getName().substring(this.getClass().getName().lastIndexOf(".") + 1);

        bMeasure = findViewById(R.id.startMeasure);
        bSearch = findViewById(R.id.searchPlaces);
        place_views = findViewById(R.id.placesSpinner);
        placesNames_ArrayList = new ArrayList<>();
        placesLatitude = new ArrayList<>();
        placesLongitude = new ArrayList<>();
        placesStreet_ArrayList = new ArrayList<>();
        permissions = new String[2];

        int permission1 = ContextCompat.checkSelfPermission(MenuActivity.this, "android.permission.ACCESS_COARSE_LOCATION");
        int permission2 = ContextCompat.checkSelfPermission(MenuActivity.this, "android.permission.ACCESS_FINE_LOCATION");

        if (permission1 == PERMISSION_GRANTED && permission2 == PERMISSION_GRANTED) {

        } else {
            permissions[0] = "android.permission.ACCESS_COARSE_LOCATION";
            permissions[1] = "android.permission.ACCESS_FINE_LOCATION";

            ActivityCompat.requestPermissions(MenuActivity.this, permissions, REQUEST_CODE);
        }


        //Getting the Intent
        Intent i = getIntent();
        //Getting the Values from First Activity using the Intent received
        name=i.getStringExtra("name_key");
        age=i.getStringExtra("age_key");
        gender=i.getStringExtra("gender_key");
        height=i.getStringExtra("height_key");
        weight=i.getStringExtra("weight_key");

        bMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MenuActivity.this,MeasureActivity.class);

                // Adding values to the intent to pass them to Second Activity
                i.putExtra("name_key", name);
                i.putExtra("age_key",age);
                i.putExtra("gender_key", gender);
                i.putExtra("weight_key", weight);
                i.putExtra("height_key",height);

                Intent intent = new Intent(MenuActivity.this, MeasureActivity.class);
                startActivity(intent);
            }
        });

        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place = place_views.getSelectedItem().toString();
                readJSON(place);
                toggle_buttons(false);
            }
        });

        PlacesData placesData = new PlacesData();
        PlacesArrayAdapter placesArrayAdapter = new PlacesArrayAdapter(this, placesData.getPlacesDataList());
        place_views.setAdapter(placesArrayAdapter);

        // Create an executor for the background tasks:
        es = Executors.newSingleThreadExecutor();
    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            // message received from background thread: load complete (or failure)
            String string_result;
            super.handleMessage(msg);
            Log.d(logTag, "message received from background thread");
            //toggle_buttons(true); // re-enable the button

            if((string_result = msg.getData().getString("text")) != null) {
                getJSONParse(string_result);
                setInMap();
                toggle_buttons(true);
            }
        }
    };

    private void readJSON(String place) {
        if (place == "Swimming pool") {
            LoadWebContents loadWebContents = new LoadWebContents(handler, CONTENT_TYPE_JSON, URL_SWIMMINGPOOL);
            es.execute(loadWebContents);
        } else if (place == "Sport centre") {
            LoadWebContents loadWebContents = new LoadWebContents(handler, CONTENT_TYPE_JSON, URL_SPORTCENTRE);
            es.execute(loadWebContents);
        } else if (place == "Garden and Park") {
            LoadWebContents loadWebContents = new LoadWebContents(handler, CONTENT_TYPE_JSON, URL_GARDEN);
            es.execute(loadWebContents);
        }
    }

    private void setInMap() {
        Intent intent = new Intent(MenuActivity.this, MapsActivity.class);
        intent.putExtra("name", placesNames_ArrayList);
        intent.putExtra("street", placesStreet_ArrayList);
        intent.putExtra("latitude", placesLatitude);
        intent.putExtra("longitude", placesLongitude);
        startActivity(intent);
    }
    private void getJSONParse(String string) {
        try {
            JSONObject jsonObject = new JSONObject(string);
            JSONArray jsonArray = jsonObject.getJSONArray("@graph");
            for (int i = 0 ; i < jsonArray.length(); i++) {

                JSONObject details = jsonArray.getJSONObject(i);
                String gardenName = details.getString("title");
                placesNames_ArrayList.add(gardenName);

                JSONObject address = details.getJSONObject("address");
                String street = address.getString("street-address");
                placesStreet_ArrayList.add(street);

                JSONObject location = details.getJSONObject("location");
                String latitude = location.getString("latitude");
                String longitude = location.getString("longitude");
                placesLatitude.add(latitude);
                placesLongitude.add(longitude);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void toggle_buttons(boolean state) {
        // enable or disable buttons (depending on state)
        bMeasure.setEnabled(state);
        bSearch.setEnabled(state);
        place_views.setEnabled(state);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                    break;
                }
        }
    }
}