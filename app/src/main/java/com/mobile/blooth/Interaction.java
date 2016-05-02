package com.mobile.blooth;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.Serializable;
import java.util.Date;
/**
 * Created by KevinCostello on 7/21/15.
 */
@ParseClassName("Interaction")
public class Interaction extends ParseObject implements Serializable {

    public Interaction() {
        // A default constructor is required.
        super();
    }

    public String getBeacon() {
        return getString("beacon");
    }

    public void setBeacon(String value) {
        put("beacon", value);
    }

    public ParseFile getFile() {
        return getParseFile("file");
    }

    public void setFile(ParseFile value) {
        put("file", value);
    }

    public String getType() {
        return getString("type");
    }

    public void setType(String value){
        put("type", value);
    }

    public Date getLastSent(){
        return getDate("lastSent");
    }

    public void setLastSent(Date value){
        put("lastSent", value);
    }

    public String getnotifText(){
        return getString("notifText");
    }

    public void setnotifText(String value){
        put("notifText", value);
    }

    public String getWebLink(){
        return getString("websiteLink");
    }

    public void setWebLink(String value){
        put("websiteLink", value);
    }

}
