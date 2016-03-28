package com.somethingweird.crimapp;

import android.support.annotation.XmlRes;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by iago on 3/27/2016.
 */
public interface CrimeInterface {

    String getType();

    String getLocation();

    Date getOccurred();

    Date getBetween();

    String getLink();

    void setType(String type);

    void setLocation(String location);

    void setOccurred(Date occured);

    void setBetween(Date between);

    void setLink(String narrative);
}
