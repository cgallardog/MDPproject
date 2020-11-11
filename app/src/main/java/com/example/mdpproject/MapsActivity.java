package com.example.mdpproject;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient currentLocation;
    LocationRequest locationRequest;
    Marker marker;
    private boolean existMarker = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationRequest = new LocationRequest();
        currentLocation = new FusedLocationProviderClient(this);
        locationRequest.setPriority(PRIORITY_HIGH_ACCURACY);


        int permission1 = ContextCompat.checkSelfPermission(MapsActivity.this, "android.permission.ACCESS_COARSE_LOCATION");
        int permission2 = ContextCompat.checkSelfPermission(MapsActivity.this, "android.permission.ACCESS_FINE_LOCATION");

        if (permission1 == PERMISSION_GRANTED && permission2 == PERMISSION_GRANTED) {
            currentLocation.requestLocationUpdates(locationRequest, locationCallback, null);
        }

    }

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                setMarker(location);
            }
        };
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Intent intent = getIntent();

        ArrayList<String> placesLongitude = intent.getStringArrayListExtra("latitude");
        ArrayList<String> placesLatitude = intent.getStringArrayListExtra("longitude");
        ArrayList<String> placesNames = intent.getStringArrayListExtra("name");
        ArrayList<String> placesStreet = intent.getStringArrayListExtra("street");

        for (int i = 0; i<placesLatitude.size(); i++) {
            Double latitude = Double.valueOf(placesLatitude.get(i));
            Double longitude = Double.valueOf(placesLongitude.get(i));
            LatLng position = new LatLng(longitude, latitude);
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(placesNames.get(i))
                    .snippet(placesStreet.get(i)));

            if (i == placesLatitude.size() - 1) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
            }
        }
    }

    private void setMarker(Location location) {
        if (existMarker) {
            marker.remove();
        }
        LatLng currentPos = new LatLng(location.getLatitude(), location.getLongitude());
        marker = mMap.addMarker(new MarkerOptions()
                .position(currentPos)
                .title("Marker current position")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPos, 13));
    }
}

AndroidManifest.xml
google_maps_api.xml

        echo "# MDPproject" >> README.md
        git init
        git add README.md
        git commit -m "first commit"
        git branch -M main
        git remote add origin https://github.com/cgallardog/MDPproject.git
        git push -u origin main