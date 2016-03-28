package com.somethingweird.crimapp;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by iago on 3/27/2016.
 */
public class Crime {



    //TODO: Pull from XML in constructor like: Crime(XMLtree XML)
    private String Type;
    private String Location;
    private Date Occured;
    private Date Between;
    private String Narrative;
    Calendar c = Calendar.getInstance();

    public Crime(){
        Type = "Crime";
        Location = "1810 N 4th St, Columbus, OH";
        Occured = new Date();
        Occured.setTime(c.getTimeInMillis());
        Between = new Date();
        Between.setTime(c.getTimeInMillis());
        Narrative = "There is no narrative available.";
    }
    public String getType() {
        return Type;
    }

    public String getLocation() {
        return Location;
    }

    public Date getOccured() {
        return Occured;
    }

    public Date getBetween() {
        return Between;
    }

    public String getNarrative() {
        return Narrative;
    }

    public void setType(String type) {
        Type = type;
    }

    public void setLocation(String location) {
        Location = location;
    }

    public void setOccured(Date occured) {
        Occured = occured;
    }

    public void setBetween(Date between) {
        Between = between;
    }

    public void setNarrative(String narrative) {
        Narrative = narrative;
    }


}
