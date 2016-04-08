package com.somethingweird.crimapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

public class About extends AppCompatActivity {
    Button backButton;

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

        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        try{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }catch(Exception e){
            e.printStackTrace();
        }

        final Switch themeSwitch = (Switch) findViewById(R.id.theme_switch);
        if("DARK".equals(theme)){
            themeSwitch.setText(getString(R.string.dark_theme));
            themeSwitch.setChecked(true);

        }else{
            themeSwitch.setChecked(false);
            themeSwitch.setText(getString(R.string.light_theme));
        }

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                SharedPreferences.Editor themeEditor = getSharedPreferences(getResources().getString(R.string.theme_shared_pref), MODE_PRIVATE).edit();
                if(isChecked){
                    themeEditor.putString("THEME","DARK").apply();
                    themeSwitch.setText(getString(R.string.dark_theme));
                    Log.d("THEME:::", getSharedPreferences(getResources().getString(R.string.theme_shared_pref), MODE_PRIVATE).getString("THEME", "EROR"));
                    Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }else{
                    themeEditor.putString("THEME","LIGHT").apply();
                    themeSwitch.setText(getString(R.string.light_theme));
                    Log.d("THEME:::", getSharedPreferences(getResources().getString(R.string.theme_shared_pref), MODE_PRIVATE).getString("THEME", "EROR"));Intent i = getBaseContext().getPackageManager()
                            .getLaunchIntentForPackage( getBaseContext().getPackageName() );
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                }

            }
        });




//        SharedPreferences.Editor themeEditor = getSharedPreferences(getResources().getString(R.string.theme_shared_pref), MODE_PRIVATE).edit();
//        themeEditor.putString("THEME","LIGHT").apply();
    }

    // Menu icons are inflated just as they were with actionbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu911:

                Util.call(getClass(),getApplicationContext());
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.menuAbout:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...

                Intent searchMapIntent = new Intent(getApplicationContext(), About.class);
                startActivity(searchMapIntent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}
