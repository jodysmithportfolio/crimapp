package com.somethingweird.crimapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
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

import com.google.gson.Gson;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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

        //Build crime DB
        String FileName = "CrimeDB";


        String path = "C:UsersiagoDownloadsTic-Tac-Toe-Using-FragmentsCrimappappsrcmain\n" +
                "        esxmlcrimedb.xml";
        FileInputStream in;
        try {
            in = new FileInputStream(path);

            XmlPullParser parser = Xml.newPullParser();

            int eventType = parser.getEventType();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            List<Crime> Crimes = new ArrayList<>();
            while(eventType != XmlPullParser.END_DOCUMENT){
                eventType = parser.getEventType();
                Crime crime = parseXML(parser); //Bulds a single crime
                Crimes.add(crime); //adds to list
            }

            in.close();
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }



        // TODO: Save the XML to internal storage
        //saveData();
        //End initialize crime DB section

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

    /*
        Uses supplied parser to parse a single crime
        requires specific format:
        <crime>
	        <type>string</type>
	        <location>string</location>
	        <occured>Date</occured>
	        <between>Date</between>
	        <link>string</link>
        </crime>
     */
    public Crime parseXML(XmlPullParser parser){
        Crime crime = new Crime();
        try {

            int eventType = parser.getEventType();
            SimpleDateFormat dateParser;
            dateParser = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
            while(eventType!=XmlPullParser.END_DOCUMENT){
                //Parse XML and build a single crime
                if(eventType == XmlPullParser.START_TAG) {
                    String tag = parser.getName();
                    switch (tag) {
                        case "type":
                            parser.next();
                            crime.setType(parser.getText());
                            parser.next();
                            break;
                        case "location":
                            parser.next();
                            crime.setLocation(parser.getText());
                            parser.next();
                            break;
                        case "occurred":
                            parser.next();
                            String occ = parser.getText();
                            if(!occ.equals("N/A") &! occ.isEmpty()){
                                crime.setOccurred(dateParser.parse(occ));
                            }
                            parser.next();
                            break;
                        case "link":
                            parser.next();
                            crime.setLink(parser.getText());
                            parser.next();
                            break;
                        case "between":
                            parser.next();
                            String betw = parser.getText();
                            if(!betw.equals("N/A") &! betw.isEmpty()){
                                crime.setOccurred(dateParser.parse(betw));
                            }
                            parser.next();
                            break;
                        case "crime":
                            parser.next();
                            break;
                    }


                }
                if(eventType == XmlPullParser.END_TAG){
                    parser.next();
                    break; //I'm so sorry but doing it another way would be less readable
                }


                eventType = parser.next();
            }

        } catch (ParseException | IOException | XmlPullParserException e) {
            e.printStackTrace();
        }
        return crime;
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
