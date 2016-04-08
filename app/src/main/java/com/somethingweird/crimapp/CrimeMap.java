package com.somethingweird.crimapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CrimeMap extends AppCompatActivity implements OnMapReadyCallback {
    Float currentlat = null;
    Float currentlong = null;
    String searchString ="Columbus";
    String destination = "Columbus";
    boolean useUserLoc = false;
    boolean direction = false;
    private GoogleMap mMap;
    boolean time = false;
    float searchHour;
    float searchMin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences themePref;
        themePref = getSharedPreferences(getResources().getString(R.string.theme_shared_pref),MODE_PRIVATE);
        String theme = themePref.getString("THEME","DARK");
        Log.d("THEME:", theme);
        if("DARK".equals(theme)){
            setTheme(R.style.CrimeDark);
        }else{
            setTheme(R.style.CrimeLight);
        }

        setContentView(R.layout.activity_crime_map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch(Exception e){
            e.printStackTrace();
        }
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
                int lines;
                searchString = "";
                for(lines=0;lines<searchAddress.getMaxAddressLineIndex();lines++){
                    searchString+=searchAddress.getAddressLine(lines);
                    searchString+=", ";
                }
                Log.d("ORIGIN", searchString);
            }
            if(time=extras.getBoolean("SEARCH_TIME")){
                searchHour = extras.getFloat("SEARCH_HOUR");
                searchMin = extras.getFloat("SEARCH_MIN");
            }
            useUserLoc = getIntent().getBooleanExtra("CURRENT_LOC",false);
            direction = extras.getBoolean("DIRECTIONS");
            if(direction){
                destination = extras.getString("DESTINATION");
                Address dest = getAddress(destination);
                int maxl= dest.getMaxAddressLineIndex();
                int i;
                destination = "";
                for(i=0;i<maxl;i++){
                    destination+=dest.getAddressLine(i);
                    destination+=", ";
                }
            }
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu911:

                Util.call(getClass(),getApplicationContext());
                return true;

            case R.id.menuAbout:

                Intent searchMapIntent = new Intent(getApplicationContext(), About.class);
                startActivity(searchMapIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        finish();
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
        SharedPreferences themePref;
        themePref = getSharedPreferences(getResources().getString(R.string.theme_shared_pref),MODE_PRIVATE);
        String theme = themePref.getString("THEME","DARK");
        Log.d("THEME:", theme);
        if("DARK".equals(theme)){
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        }else{
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        }
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 11));
        new addCrimes().execute();
        if(direction) {
            new addDirectionLayer().execute(searchString, destination);
        }

    }

    /**
     * Method to add the heat map layer to the map.
     *
     * REQUIRES: @code(List<LatLng> list) a collectio3956n of LatLng objects representing the crimes
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
            Log.d("REAL ADDRESS", address + " converted to: " + realAdd.toString());

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

    private class addCrimes extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            addCrimesOnMap();
            return null;
        }

        public void addCrimesOnMap(){
            //mMap = googleMap;
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
                final List<LatLng> l = list;
                runOnUiThread(new Runnable() {
                    public void run() {
                        addHeatMap(l);
                        l.clear();
                    }
                });

            }

        }
        @Override
        protected void onPostExecute(String result) {
            super .onPostExecute(result);

        }
    }

    private class addDirectionLayer extends AsyncTask<String, String, List<LatLng>> {
        protected List<LatLng> doInBackground(String... locs) {
            String origin = locs[0];
            String destination = locs[1];
            List<LatLng> dirList = new ArrayList<>();
            try {
                String urlString= "http://maps.googleapis.com/maps/api/directions/xml?origin=\""+
                        URLEncoder.encode(origin, "UTF-8")+
                        "\"&destination=\""+
                        URLEncoder.encode(destination, "UTF-8")+
                        "\"&sensor=false";
                Log.d("URL DIRECTION", urlString);
                URL uri = new URL(urlString);
                InputStream is;
                HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
                conn.setDoInput(true);
                conn.connect();
                is = conn.getInputStream();
                XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
                XmlPullParser parser = xmlFactoryObject.newPullParser();
                parser.setInput(is, null);
                String name;
                int eventType = parser.getEventType();
                boolean cont = false;
                while(eventType!=XmlPullParser.END_DOCUMENT){
                    name = parser.getName();
                    if(eventType==XmlPullParser.START_TAG) {
                        if("overview_polyline".equals(name)){
                            cont = true;
                        }
                        if("points".equals(name)&&cont){
                            dirList.addAll(decodePoly(parser.nextText()));
                        }//some else
                    }
                    eventType = parser.next();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            final List<LatLng> list = dirList;
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        LatLng orgsrc = list.get(0);
                        for (int i = 0; i < list.size() - 1; i++) {
                            LatLng src = list.get(i);
                            LatLng dest = list.get(i + 1);

                            // mMap is the Map Object
                            Polyline line = mMap.addPolyline(
                                    new PolylineOptions().add(
                                            new LatLng(src.latitude, src.longitude),
                                            new LatLng(dest.latitude, dest.longitude)
                                    ).width(10).color(Color.RED)
                            );
//                            Log.d("EXECUTION","From: "+src.toString()+" to: "+dest.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Snackbar.make(findViewById(android.R.id.content), "Unable to get directions for you query", Snackbar.LENGTH_LONG).show();
                    }
                }
            });
            return list;
        }
        /* Decodes Encoded Poly lines from Google Directions API*
         * Code From http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
         */
        private List<LatLng> decodePoly(String encoded) {
            List<LatLng> poly = new ArrayList<>();
            int index = 0, len = encoded.length();
            int lat = 0, lng = 0;

            while (index < len) {


                int b, shift = 0, result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lat += dlat;

                shift = 0;
                result = 0;
                do {
                    b = encoded.charAt(index++) - 63;
                    result |= (b & 0x1f) << shift;
                    shift += 5;
                } while (b >= 0x20);
                int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
                lng += dlng;

                LatLng p = new LatLng((((double) lat / 1E5)),
                        (((double) lng / 1E5)));
                poly.add(p);
            }

            return poly;
        }

    }
}
