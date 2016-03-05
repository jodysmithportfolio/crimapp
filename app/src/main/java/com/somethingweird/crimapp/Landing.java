package com.somethingweird.crimapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;
import java.util.Calendar;


public class Landing extends AppCompatActivity {
    Button callButton;
    Button getLocationButton;
    Button searchButton;
    Button aboutButton;
    NumberPicker hourpick;
    NumberPicker minpick;
    Spinner meridianpick;
    String[] meridians;
    Location currentlocation;
    EditText locationbox;

    private final String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        Calendar calendar = Calendar.getInstance();
        int currenthour = calendar.get(Calendar.HOUR);
        int currentmin = calendar.get(Calendar.MINUTE);
        int currentmer = calendar.get(Calendar.AM_PM);
        String currentmers = "PM";
        hourpick = (NumberPicker) findViewById(R.id.hourpicker);
        hourpick.setMaxValue(12);
        hourpick.setMinValue(1);
        hourpick.setValue(currenthour);//change to current hour
        minpick = (NumberPicker) findViewById(R.id.minutepicker);
        minpick.setMaxValue(59);
        minpick.setMinValue(0);
        minpick.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format("%02d", i);
            }
        });
        minpick.setValue(currentmin);//change to current min
        meridianpick = (Spinner) findViewById(R.id.meridianpicker);
        if (currentmer == Calendar.AM) {
            currentmers = "AM";
        }
        this.meridians = new String[]{"AM", "PM"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, meridians);
        meridianpick.setAdapter(adapter);
        int spinnerposition = adapter.getPosition(currentmers);
        meridianpick.setSelection(spinnerposition);
        locationbox = (EditText) findViewById(R.id.searchbylocationbox);
        callButton = (Button) findViewById(R.id.call911);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.call(getClass(),getApplicationContext());
            }
        });
        getLocationButton = (Button) findViewById(R.id.getlocationbutton);
        getLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertCurrentLocation();
            }
        });
        aboutButton = (Button) findViewById(R.id.aboutbutton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutIntent = new Intent(v.getContext(), About.class);
                startActivity(aboutIntent);
            }
        });

        searchButton = (Button) findViewById(R.id.searchbylocbutton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchMapIntent = new Intent(v.getContext(), CrimeMap.class);
                searchMapIntent.putExtra("SEARCH_DATA", locationbox.getText().toString());
                startActivity(searchMapIntent);
            }
        });
        insertCurrentLocation();
    }

    private void insertCurrentLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // get the last know location from location manager
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        Log.d(TAG,"Permissions Check: "+permissionCheck);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d(TAG,"Give explanation?");

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},permissionCheck);
            }
        }
        currentlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        // now get the lat/lon from the location and do something with it.
        if(currentlocation != null) {
            Log.d(TAG, "Lat:" + currentlocation.getLatitude() + "  Long:" + currentlocation.getLongitude());
            locationbox.setText("Current location: " + currentlocation.getLatitude() + ", " + currentlocation.getLongitude(), TextView.BufferType.EDITABLE);
            Toast.makeText(getApplicationContext(), "Current Location Inserted", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(), "Unable to find current Location", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart() {
        Log.d(TAG,"onStart Called");
        insertCurrentLocation();
        super.onStart();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop Called");
        super.onStop();
    }

    @Override
    public void onResume() {
        Log.d(TAG,"onResume Called");
        insertCurrentLocation();
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG,"onPause Called");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy Called");
        super.onDestroy();
    }
}
