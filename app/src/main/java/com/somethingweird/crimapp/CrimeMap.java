package com.somethingweird.crimapp;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CrimeMap extends FragmentActivity implements OnMapReadyCallback {
    Float currentlat = null;
    Float currentlong = null;
    String searchString;
    private GoogleMap mMap;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_map);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d("EXTRAS:  ", extras.getString("SEARCH_DATA"));
            searchString = extras.getString("SEARCH_DATA");
            if (searchString != null) {
                if (searchString.startsWith("Current location:")) {
                    searchString = searchString.substring(18);
                    Log.d("Concat String", searchString);
                    int commaPos = searchString.indexOf(",");
                    currentlat = Float.parseFloat(searchString.substring(0, commaPos));
                    currentlong = Float.parseFloat(searchString.substring(commaPos + 2, searchString.length()));
                }
            }
        }
        Toast.makeText(getApplicationContext(), "searchSt ring: " + searchString, Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        LatLng currentLoc = new LatLng(40, -83);
        if (currentlat != null && currentlong != null) {
            currentLoc = new LatLng(currentlat, currentlong);
        }
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
        //getCrimes
        //call method that

        //address to Address Object
        String s = "429 N High St, Columbus, OH"; //placeholder for Crime.GetAddress
        Address address = getAddress(s);

        //marker adding using Address object
        mMap.addMarker(new MarkerOptions()
                .draggable(false)
                .title("Crime")
                .position(new LatLng(address.getLatitude(), address.getLongitude())));

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
        List<Address> Add_List;

        //following would not run without try/catch
        try {
            Add_List = geocoder.getFromLocationName(address, 1); //can return array of possibilities
            realAdd = Add_List.get(0);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return realAdd;
    }
    //ATTN: AUTO GENERATED STUFF NOT SURE IF NECESSARY - Jody
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CrimeMap Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.somethingweird.crimapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CrimeMap Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.somethingweird.crimapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
