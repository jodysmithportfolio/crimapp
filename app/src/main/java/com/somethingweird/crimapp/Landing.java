package com.somethingweird.crimapp;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.content.Context;

//import com.google.gson.Gson;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;


public class Landing extends AppCompatActivity {
//    Button getLocationButton;
//    Button searchByLocButton;
//    Button searchByTimeButton;
//    Button getDirectionsButton;
//    EditText destinationbox;
//    NumberPicker hourpick;
//    NumberPicker minpick;
//    Spinner meridianpick;
//    String[] meridians;
    Location currentlocation;
//    EditText locationbox;

    private final String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences  themePref;
        themePref = getSharedPreferences(getResources().getString(R.string.theme_shared_pref),MODE_PRIVATE);
        String theme = themePref.getString("THEME","DARK");
        Log.d("THEME:", theme);
        if("DARK".equals(theme)){
            setTheme(R.style.CrimeDark);
        }else{
            setTheme(R.style.CrimeLight);
        }
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Snackbar.make(findViewById(android.R.id.content), "You can change the theme anytime!", Snackbar.LENGTH_LONG)
                .setAction("Theme Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(view.getContext(), About.class);
                        startActivity(i);
                    }
                }).show();
        setContentView(R.layout.activity_landing1);
        new setupCrimes().execute();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Calendar calendar = Calendar.getInstance();
        int currenthour = calendar.get(Calendar.HOUR);
        int currentmin = calendar.get(Calendar.MINUTE);
        int currentmer = calendar.get(Calendar.AM_PM);
        String currentmers = "PM";
        final NumberPicker hourpick = (NumberPicker) findViewById(R.id.hourpicker);
        hourpick.setMaxValue(12);
        hourpick.setMinValue(1);
        hourpick.setValue(currenthour);//change to current hour
        final NumberPicker minpick = (NumberPicker) findViewById(R.id.minutepicker);
        minpick.setMaxValue(59);
        minpick.setMinValue(0);
        minpick.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int i) {
                return String.format(Locale.US,"%02d", i);
            }
        });
        minpick.setValue(currentmin);//change to current min
        final Spinner meridianpick = (Spinner) findViewById(R.id.meridianpicker);
        if (currentmer == Calendar.AM) {
            currentmers = "AM";
        }
        final String[] meridians = new String[]{"AM", "PM"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, meridians);
        //noinspection ConstantConditions
        meridianpick.setAdapter(adapter);
        int spinnerposition = adapter.getPosition(currentmers);
        meridianpick.setSelection(spinnerposition);
        final EditText locationbox = (EditText) findViewById(R.id.searchbylocationbox);
        final EditText destinationbox = (EditText) findViewById(R.id.destEditText);
        final CheckBox allCrimes = (CheckBox) findViewById(R.id.all_crimes);
        //noinspection ConstantConditions
        allCrimes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection ConstantConditions
                if(allCrimes.isChecked()){
                    hourpick.setEnabled(false);
                    minpick.setEnabled(false);
                    meridianpick.setEnabled(false);
                }else{
                    hourpick.setEnabled(true);
                    minpick.setEnabled(true);
                    meridianpick.setEnabled(true);
                }
            }
        });

        //noinspection ConstantConditions
        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent i = new Intent(view.getContext(),CrimeMap.class);
                try {
                    String origin = locationbox.getText().toString();

                    String dest = destinationbox.getText().toString();
                    if (!"".equals(origin)) {
                        i.putExtra("SEARCH_ORIGIN", origin);
                    }
                    if (!"".equals(dest)) {
                        i.putExtra("SEARCH_DEST", dest);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(!allCrimes.isChecked()){

                    float hour = hourpick.getValue();
                    float min = minpick.getValue();
                    Calendar searchTime = new GregorianCalendar();
                    searchTime.set(Calendar.HOUR, (int) hour);
                    searchTime.set(Calendar.MINUTE, (int) min);
                    if(meridianpick.getSelectedItem()=="PM"){
                        searchTime.set(Calendar.AM_PM,Calendar.PM);
                    }else{
                        searchTime.set(Calendar.AM_PM,Calendar.AM);
                    }
                    i.putExtra("SEARCH_HOUR", (float)searchTime.get(Calendar.HOUR_OF_DAY));
                    i.putExtra("SEARCH_MIN", (float)searchTime.get(Calendar.MINUTE));
                    Log.d("TIME SENT:", ""+searchTime.get(Calendar.HOUR_OF_DAY)+":"+searchTime.get(Calendar.MINUTE));
                }
                startActivity(i);

            }
        });


//        getLocationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent searchMapIntent = new Intent(v.getContext(), CrimeMap.class);
//                searchMapIntent.putExtra("CURRENT_LOC", true);
//                startActivity(searchMapIntent);
//            }
//        });
//
//        final Button searchByLocButton = (Button) findViewById(R.id.searchbylocbutton);
//        searchByLocButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent searchMapIntent = new Intent(v.getContext(), CrimeMap.class);
//                searchMapIntent.putExtra("SEARCH_DATA", locationbox.getText().toString());
//                startActivity(searchMapIntent);
//            }
//        });
//        final Button getDirectionsButton = (Button) findViewById(R.id.getDirButton);
//        getDirectionsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent searchMapIntent = new Intent(v.getContext(), CrimeMap.class);
//                searchMapIntent.putExtra("SEARCH_DATA", locationbox.getText().toString());
//                searchMapIntent.putExtra("DIRECTIONS",true);
//                searchMapIntent.putExtra("DESTINATION",destinationbox.getText().toString());
//                startActivity(searchMapIntent);
//            }
//        });
//        final Button searchByTimeButton = (Button) findViewById(R.id.search_by_time_button);
//        searchByTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent searchMapIntent = new Intent(v.getContext(), CrimeMap.class);
//                float hour = hourpick.getValue();
//                float min = hourpick.getValue();
//                Calendar searchTime = new GregorianCalendar();
//                searchTime.set(Calendar.HOUR, (int) hour);
//                searchTime.set(Calendar.MINUTE, (int) min);
////                searchTime.setTime();
//                if(meridianpick.getSelectedItem()=="PM"){
//                    searchTime.set(Calendar.AM_PM,Calendar.PM);
//                }else{
//                    searchTime.set(Calendar.AM_PM,Calendar.AM);
//                }
//                searchMapIntent.putExtra("SEARCH_TIME", true);
//                searchMapIntent.putExtra("SEARCH_HOUR", (float)searchTime.get(Calendar.HOUR_OF_DAY));
//                searchMapIntent.putExtra("SEARCH_MIN", (float)searchTime.get(Calendar.MINUTE));
//                startActivity(searchMapIntent);
//            }
//        });


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
    public void onStart() {
        Log.d(TAG,"onStart Called");
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
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause Called");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy Called");
        super.onDestroy();
    }
    class setupCrimes extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
        }

        protected String doInBackground(String... args) {
            //Crime Marker Adding
            InputStream in;
            try {
                in = getResources().openRawResource(R.raw.crimedb);

                XmlPullParser parser = Xml.newPullParser();

                int eventType = parser.getEventType();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);

                while(eventType != XmlPullParser.END_DOCUMENT){
                    eventType = parser.getEventType();
                    Crime crime = parseXML(parser); //Builds a single crime
                    Crime.addCrime(crime); //adds to list
                }

                in.close();
            } catch (IOException | XmlPullParserException e) {
                e.printStackTrace();
            }


            return null;
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
                                    crime.setBetween(dateParser.parse(betw));
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
        protected void onPostExecute(String result) {
            super .onPostExecute(result);

        }
    }
}
