package com.mobile.blooth;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by KevinCostello on 6/30/15.
 */
@ParseClassName("Regions")
public class Beacon extends ParseObject {

    /*public String getText() {
        return getString("text");
    }

    public void setText(String value) {
        put("text", value);
    }

    public ParseUser getUser() {
        return getParseUser("user");
    }

    public void setUser(ParseUser value) {
        put("user", value);
    }

    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public static ParseQuery<Beacon> getQuery() {
        return ParseQuery.getQuery(Beacon.class);
    } */

    public String getUUID() {
        return getString("UUID");
    }

    public void setUUID(String value) {
        put("UUID", value);
    }

    public String getIdentifier() {
        return getString("identifier");
    }

    public void setIdentifier(String value) {
        put("identifier", value);
    }

    public String getGreeting() {
        return getString("greetingString");
    }

    public void setGreeting(String value){
        put("greetingString", value);
    }


   /* public static String getISO8601StringForCurrentDate() {
        Date now = new Date();
        return getISO8601StringForDate(now);
    } */

}

