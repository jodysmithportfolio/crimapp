package com.somethingweird.crimapp;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Jody on 3/27/2016.
 * Crime class TODO: May need to create separate interface
 */
public class Crime implements CrimeInterface {




    /**
     * List of all crimes
     */
    public static List<Crime> Crimes = new ArrayList<>();
    private String Type;
    private String Location;
    private Date Occurred;
    private Date Between;
    private String Link;
    Calendar c = Calendar.getInstance();

    public Crime(){
        Type = "Crime";
        Location = "1810 N 4th St";
        Occurred = new Date();
        Occurred.setTime(c.getTimeInMillis());
        Between = new Date();
        Between.setTime(c.getTimeInMillis());
        Link = "No Link Available.";
    }

    public static List<Crime> getCrimes() {
        return Crimes;
    }

    public static void addCrime(Crime crime) {
        Crimes.add(crime);
    }

    public String getType() {
        return Type;
    }

    public String getLocation() {
        return Location;
    }

    public Date getOccurred() {
        return Occurred;
    }

    public Date getBetween() {
        return Between;
    }

    public String getLink() {
        return Link;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setOccurred(Date occurred) {
        Occurred = occurred;
    }

    public void setBetween(Date between) {
        Between = between;
    }

    public void setLink(String link) {
        Link = link;
    }


}
