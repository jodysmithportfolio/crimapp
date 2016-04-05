package com.somethingweird.crimapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.maps.model.Marker;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class CrimeMap extends FragmentActivity implements OnMapReadyCallback {
    Float currentlat = null;
    Float currentlong = null;
    String searchString ="Columbus";
    boolean useUserLoc = false;
    private GoogleMap mMap;
    boolean time = false;
    float searchHour;
    float searchMin;
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
                if(searchAddress.hasLongitude()&&searchAddress.hasLatitude()){
                    currentlat = new Float(searchAddress.getLatitude());
                    currentlong = new Float(searchAddress.getLongitude());
                }else{
                    currentlat = (float)40.0;
                    currentlong = (float)-83.0;
                }
                Log.d("Current Loc",""+currentlat+" , "+currentlong);
            }
            if(time=extras.getBoolean("SEARCH_TIME")){
                searchHour = extras.getFloat("SEARCH_HOUR");
                searchMin = extras.getFloat("SEARCH_MIN");
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
        new addCrimes().execute(googleMap);
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
            addList = geocoder.getFromLocationName(address, 1, 39.808631, -83.210280, 40.157272, -82.771378); //can return array of possibilities
            if(addList.size()>0){
                realAdd = addList.get(0);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return realAdd;
    }

    private boolean timeCompare(float searchTime, float crimeTime){
        if(searchTime+1>=crimeTime && searchTime-1<=crimeTime){
            return true;
        }
        return false;
    }

    class addCrimes extends AsyncTask<GoogleMap, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        protected String doInBackground(GoogleMap... googleMaps) {
            addCrimesOnMap(googleMaps[0]);
            return null;
        }

        public void addCrimesOnMap(GoogleMap googleMap){
            mMap = googleMap;
            //Crime Marker Adding
            List<Crime> Crimes = Crime.getCrimes(); //static crime list from Crimes.java
            Address address;
            //Test list for addHeatMap
            List<LatLng> list = new ArrayList<>();
            LatLng testLoc;
            for(Crime c : Crimes){
                if(time){
                    Log.d("LOCATION",c.getLocation());
                    Calendar tempCal = new GregorianCalendar();
                    tempCal.setTime(c.getOccurred());
                    Calendar crimeTime = new GregorianCalendar();
                    crimeTime.setTime(new Date());
                    crimeTime.set(Calendar.HOUR_OF_DAY,tempCal.get(Calendar.HOUR_OF_DAY));
                    crimeTime.set(Calendar.MINUTE,tempCal.get(Calendar.MINUTE));
                    Calendar searchTime = new GregorianCalendar();
                    float searchTimeFloat;
                    float crimeTimeFloat;
                    searchTimeFloat = searchHour + searchMin/60;
                    crimeTimeFloat = (float)crimeTime.get(Calendar.HOUR_OF_DAY) + (float)crimeTime.get(Calendar.MINUTE)/60;
                    if(!timeCompare(searchTimeFloat,crimeTimeFloat)){
                        Log.d("NOT NEAR searchTime", Float.toString(crimeTimeFloat));
                    }else{
                        Log.d("YES! NEAR searchTime", Float.toString(crimeTimeFloat));
                        address = getAddress(c.getLocation());
                        //marker adding using Address object
                        if (address.hasLatitude() && address.hasLongitude()) {
                            final Address add =  address;
                            final Crime cr = c;
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    mMap.addMarker(new MarkerOptions()
                                            .draggable(false)
                                            .title(cr.getType())
                                            .snippet(cr.getOccurred().toString())
                                            .position(new LatLng(add.getLatitude(), add.getLongitude())));
                                }
                            });
                            testLoc = new LatLng(address.getLatitude(), address.getLongitude());
                            list.add(testLoc);
                        } else {
                            Log.d("FAIL", "NO LAT/LONG for " + c.getLocation());
                        }
                    }
                    Log.d("   ","    ");
                }else {
                    address = getAddress(c.getLocation());
                    //marker adding using Address object
                    if (address.hasLatitude() && address.hasLongitude()) {
                        final Address add =  address;
                        final Crime cr = c;
                        runOnUiThread(new Runnable() {
                            public void run() {
                                mMap.addMarker(new MarkerOptions()
                                        .draggable(false)
                                        .title(cr.getType())
                                        .snippet(cr.getOccurred().toString())
                                        .position(new LatLng(add.getLatitude(), add.getLongitude())));
                            }
                        });
                        testLoc = new LatLng(address.getLatitude(), address.getLongitude());
                        list.add(testLoc);
                    } else {
                        Log.d("FAIL", "NO LAT/LONG for " + c.getLocation());
                    }
                }
            }
            if(list.size()<1){
                Log.d("LIST LENGTH", Integer.toString(list.size()));
                Snackbar.make(findViewById(android.R.id.content), "No crimes that matches your criteria", Snackbar.LENGTH_LONG)
                        .show();
            }else{
                final List l = list;
                runOnUiThread(new Runnable() {
                    public void run() {
                        addHeatMap(l);
                    }
                });
            }

        }


                @Override
        protected void onPostExecute(String result) {
            super .onPostExecute(result);

        }
    }

}
