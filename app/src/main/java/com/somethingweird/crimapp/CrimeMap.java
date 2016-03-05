package com.somethingweird.crimapp;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class CrimeMap extends FragmentActivity implements OnMapReadyCallback {
    Float currentlat = null;
    Float currentlong = null;
    String searchString;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_map);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            Log.d("EXTRAS:  ", extras.getString("SEARCH_DATA"));
            searchString = extras.getString("SEARCH_DATA");
            if(searchString!=null) {
                if (searchString.startsWith("Current location:")) {
                    searchString = searchString.substring(18);
                    Log.d("Concat String", searchString);
                    int commaPos = searchString.indexOf(",");
                    currentlat = Float.parseFloat(searchString.substring(0, commaPos));
                    currentlong = Float.parseFloat(searchString.substring(commaPos + 2, searchString.length()));
                }
            }
        }
        Toast.makeText(getApplicationContext(), "searchSt ring: "+ searchString , Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


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
        LatLng currentLoc = new LatLng(40, -83);
        if(currentlat!=null && currentlong!=null){
            currentLoc = new LatLng(currentlat, currentlong);
        }
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
    }
}
