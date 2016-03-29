package com.somethingweird.crimapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.ui.IconGenerator;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CrimeMap extends FragmentActivity implements OnMapReadyCallback {
    Float currentlat = null;
    Float currentlong = null;
    String searchString ="Columbus";
    boolean useUserLoc = false;
    private GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_map);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
//            Log.d("EXTRAS:  ", extras.getString("SEARCH_DATA"));
            searchString = extras.getString("SEARCH_DATA");
            if (searchString != null) {
                Address searchAddress = getAddress(searchString);
                currentlat = new Float(searchAddress.getLatitude());
                currentlong = new Float(searchAddress.getLongitude());
                Log.d("Current Loc",""+currentlat+" , "+currentlong);
            }
            useUserLoc = getIntent().getBooleanExtra("CURRENT_LOC",false);
        }
        if(searchString!=null){
            Toast.makeText(getApplicationContext(), "search around: " + searchString, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "searching current location", Toast.LENGTH_LONG).show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);
        LatLng currentLoc = new LatLng(40 ,-83);
        if (currentlat != null && currentlong != null) {
            currentLoc = new LatLng(currentlat, currentlong);
        }
        if(useUserLoc){
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location currentlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            try {
                currentLoc = new LatLng(currentlocation.getLatitude(), currentlocation.getLongitude());
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else{
            mMap.addMarker(new MarkerOptions().position(currentLoc).title(searchString).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 14));



        //Crime Marker Adding
        List<Crime> Crimes = Crime.getCrimes(); //static crime list from Crimes.java
        Address address;
        //Test list for addHeatMap
        List<LatLng> list = new ArrayList<>();
        LatLng testLoc;
        for(Crime c : Crimes){
            address = getAddress(c.getLocation());
            //marker adding using Address object
            if(address.hasLatitude()&&address.hasLongitude()) {
                mMap.addMarker(new MarkerOptions()
                        .draggable(false)
                        .title(c.getType())
                        .snippet(c.getOccurred().toString())
                        .position(new LatLng(address.getLatitude(), address.getLongitude())));
                testLoc = new LatLng(address.getLatitude(), address.getLongitude());
                list.add(testLoc);
            }else{
                Log.d("FAIL", "NO LAT/LONG for "+c.getLocation());
            }
        }
        addHeatMap(list);

    }

    /**
     * Method to add the heat map layer to the map.
     *
     * REQUIRES: @code(List<LatLng> list) a collection of LatLng objects representing the crimes
     * in the database.
     *
     * RETURNS: void.
     */
    private void addHeatMap(List<LatLng> list) {
        // Create a heat map tile provider, passing it the listlngs of the crime locations.
        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(list)
                .radius(50)
                .opacity(0.4)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        TileOverlay mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    /**
     * REQUIRES: @code{String address} is a string representation of an address
     * that exists in United States
     * RETURNS: An @code{Address object} for the given address.
     *
     * NOTE: Could be changed to return a LatLng object, this was chosen for
     * flexibility and address validation through geocoder is a required step anyway
     * -Jody
     */
    private Address getAddress(String address) {

        Address realAdd = new Address(Locale.US);
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.US);
        List<Address> addList;


        try {
            addList = geocoder.getFromLocationName(address, 1, 39.84, -83.23, 40.17, -82.75); //can return array of possibilities
            if(addList.size()>0){
                realAdd = addList.get(0);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return realAdd;
    }



}
