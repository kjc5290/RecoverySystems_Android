package com.bloothevents.kevincostello.bloothlocationservices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;
import android.os.RemoteException;
import android.util.Log;
import android.content.SharedPreferences;
import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;
import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;


import org.altbeacon.beacon.AltBeacon;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.MonitorNotifier;

import com.parse.Parse;
import com.parse.ParseQuery;
import com.parse.ParseObject;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseCloud;
import com.parse.FunctionCallback;




/**
 * Created by KevinCostello on 5/27/15.
 */
public class BloothLocationServices extends Service implements BeaconConsumer {
    //constants update rate, refresh time,
    protected static final String TAG = "BloothLocationService";
    private BeaconManager beaconManager;
    private static SharedPreferences preferences;

    private ArrayList<com.bloothevents.kevincostello.bloothlocationservices.Beacon> beaconsToMonitor = new ArrayList<com.bloothevents.kevincostello.bloothlocationservices.Beacon>();

    private ArrayList<HashMap<String,String>> seenBeacons = new ArrayList<HashMap<String,String>>();

    private ArrayList<HashMap<String, Object>> sentNotifs = new ArrayList<HashMap<String, Object>>();

    //event ID constant - will have to be changed for not DEBUG

    private static String EVENT_ID;
    private static String User_Info;

    private static Intent MAIN_ACTIVITY;


    private static String closestBeaconId;
    private Date lastRefresh;
    static Timer getclosestBeaconTimer;

    @Override
    public void onCreate() {
    //called one time when the service is started
        //call function to query parse for region information update timestamps for refresh and regions to monitorbeaconManager = BeaconManager.getInstanceForApplication(this);

        //sets up the altbeacon beacon manager. if successful connection to the service onBeaconConnect is called

        beaconManager = BeaconManager.getInstanceForApplication(this);
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));//0215 is iBeacon protocol for the 4-5 bytes, bytes

                                                                                                                                // is offset and m assumes that the manufacturer data starts at 0-1
        beaconManager.bind(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();

         // gets the previously created intent
        if (checkRequiredIds() == Boolean.FALSE){
            String USER_INFO = intent.getStringExtra("User_Info"); // will return "FirstKeyValue"
            String EVENT_ID = intent.getStringExtra("Event_Id");
            setEventId(EVENT_ID);
            setUser_Info(USER_INFO);
            onDestroy();
        }


        //if we get killed restart w last intent info
        // this func needs to be able to be called more than once
        return START_STICKY;

    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        //this is to interact (provide data) to any activity that binds. Use broadcast and receivers instead of binding or pending intents
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "service stopping", Toast.LENGTH_SHORT).show();
    }

    public void onBeaconServiceConnect() {

        // reference http://stackoverflow.com/questions/24906423/android-bluetooth-low-energy-ibeacon?rq=1
        //reference  http://altbeacon.github.io/android-beacon-library/javadoc/index.html setBeaconLayout
            /*m - matching byte sequence for this beacon type to parse (exactly one required)
              s - ServiceUuuid for this beacon type to parse (optional, only for Gatt-based becons)
                i - identifier (at least one required, multiple allowed)
            p - power calibration field (exactly one required)
                d - data field (optional, multiple allowed)*/


        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");
                //use this to update local notif
                boolean isInList = false;
                String seenKey = region.getUniqueId();
                //Traverse the list
                for (HashMap curMap : sentNotifs) {
                    //If this map has the object, that is the key doesn't return a null object
                    if (curMap.get(seenKey) != null) {
                        //Store instance to the map
                        isInList = true;
                        //Stop traversing because we are done
                        //call send localNotif
                        sendLocalNotifForBeacon(region.getUniqueId());
                        break;
                    }
                }
                //if isInList is still false, the beacon has not been added/sent a local notif yet
                if (isInList = false) {
                    //this region is not in the list so add it
                    com.bloothevents.kevincostello.bloothlocationservices.Beacon newbeacon = new com.bloothevents.kevincostello.bloothlocationservices.Beacon();
                    HashMap<String, Object> newBeacon = new HashMap<String, Object>();
                    //find the beacon object
                    for (int i = 0; i < beaconsToMonitor.size(); i++) {
                        if (beaconsToMonitor.get(i).getIdentifier().equals(region.getUniqueId())) {
                            newbeacon = beaconsToMonitor.get(i);
                            break;
                        }
                    }

                    newBeacon.put(region.getUniqueId(), newbeacon);
                    newBeacon.put("lastSentNotif", null);
                    sentNotifs.add(newBeacon);
                    //call send localNotif
                    sendLocalNotifForBeacon(region.getUniqueId());
                }

            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
                if (state == 1) {
                    //start timer to get the closest beacon every 10s
                    if (getclosestBeaconTimer == null) {
                        getclosestBeaconTimer = new Timer();
                        getclosestBeaconTimer.scheduleAtFixedRate(new updateClosestBeacon(), 10000, 10000); // seconds * 1000 b.c. milliseconds
                    }

                    try {
                        beaconManager.startRangingBeaconsInRegion(region);
                    } catch (RemoteException e) {
                    }
                }

                if (state == 0) {
                    try {
                        beaconManager.stopRangingBeaconsInRegion(region);
                        //if there are no more regionsBeingRanged stop the timer
                        if (beaconManager.getRangedRegions().size() == 0) {
                            getclosestBeaconTimer.cancel();
                            getclosestBeaconTimer = null;
                            closestBeaconId = null;
                        }
                    } catch (RemoteException e) {
                    }
                }
            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is " + beacons.iterator().next().getIdentifiers() + " and is about " + beacons.iterator().next().getDistance() + " meters away"); //distance is in meters, fluctuates with RSSI
                    //add the idenfitierNAme_major_minor with distance to hashmap and add to Array list
                    String beaconHash = region.getUniqueId() + "_" + beacons.iterator().next().getIdentifier(1) + "_" + beacons.iterator().next().getIdentifier(2);
                    Log.i(TAG, "BeaconHash is " + beaconHash);
                    HashMap<String, String> beaconMap = new HashMap<String, String>();
                    beaconMap.put("Identifier", beaconHash);
                    beaconMap.put("Distance", Double.toString(beacons.iterator().next().getDistance()));
                    beaconMap.put("RSSI", Double.toString(beacons.iterator().next().getRssi()));
                    seenBeacons.add(beaconMap); // add to array list that the timer looks at every ten secs
                }
            }
        });

        getBeaconsAtEvent(); //can also maybe call this from onCreate or use on onServiceStart to see the last refresh date
    }


    private void getBeaconsAtEvent() {
        //this will query parse.com for the regions objects for event id provided above,
        // with every object returned, create a new Beacon.Class object to start monitoring with beacon manager
        ParseObject.registerSubclass(com.bloothevents.kevincostello.bloothlocationservices.Beacon.class);
        //Parse.initialize(this, "vSG1sUHBHr6u4yV98nhLpRhBd99TymLG7R9Pj7zK", "myMfTfqnMoboNwpU5vGWA2lD2pM6c5nShfZIGFzF"); //this shouldnt b here
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Regions");
        query.whereEqualTo("eventId", EVENT_ID);
        query.findInBackground(new FindCallback<ParseObject>() {

            public void done(List<ParseObject> regionList, ParseException e) {
                if (e == null) {
                    Log.d("getBeaconsAtEvent", "Retrieved " + regionList.size() + " beacons");

                    for (int i = 0; i < regionList.size(); i++) {
                        ParseObject region = regionList.get(i);
                        com.bloothevents.kevincostello.bloothlocationservices.Beacon beacon = new com.bloothevents.kevincostello.bloothlocationservices.Beacon();


                        //set the attributes from the parse object to our beacon model and add to list/array
                        beacon.setUUID(region.getString("UUID"));
                        beacon.setIdentifier(region.getString("identifier"));
                        beacon.setGreeting(region.getString("greetingString"));
                        Log.d("forLoopInQuery", "beacon is " + beacon.getIdentifier());
                        beaconsToMonitor.add(beacon);
                    }
                    startMonitoringForRegions();

                } else {
                    Log.d("getBeaconsAtEvent", "Error: " + e.getMessage());
                }
            }
        });

    }

    private void startMonitoringForRegions() {
        //this func will start monitoring for Region objects that are in the arraylist beaconsToMonitor to it

        //for each object in param do
        for (int i = 0; i < beaconsToMonitor.size(); i++) {
            com.bloothevents.kevincostello.bloothlocationservices.Beacon beacon = beaconsToMonitor.get(i);
        try {
            Log.i("startMonitoring", "attemptingToMonitor " + beacon.getIdentifier());
            beaconManager.startMonitoringBeaconsInRegion(new Region(beacon.getIdentifier(), Identifier.parse(beacon.getUUID()), null, null)); //Region region2 = new Region("myIdentifier2", Identifier.parse("2F234454-CF6D-4A0F-ADF2-F4911BA9FFA6"), Identifier.parse("1"), Identifier.parse("2"));
        } catch (RemoteException e) {
            Log.i("monitorBeacon", "Error: " + e.getMessage());
            }
        }
    }

    private void sendLocalNotifForBeacon(String regionID) {
        //this func will be called from the didEnterRegion it will use the params to search seenNotifs for the entry,
        // it will then compare the last sentNotif field to see when the last notification was sent. If null send right away and update, if !null see if the time is greater than 1 hour

        for(HashMap curMap : sentNotifs){
            //If this map has the object, that is the key doesn't return a null object
            if(curMap.get(regionID) != null) {
                //Store instance to the map
                if (curMap.get("lastSentNotif") == null){
                    //send notif and save the current date
                    NotificationManager notificationManager = (NotificationManager)
                            getSystemService(NOTIFICATION_SERVICE);
                    PendingIntent pIntent = null;

                    if (MAIN_ACTIVITY == null){
                         pIntent = PendingIntent.getActivity(this, 0, new Intent(), 0);
                    }else{
                        pIntent = PendingIntent.getActivity(this, 0, MAIN_ACTIVITY, 0);
                    }


                    // build notification
                    // the addAction re-use the same intent to keep the example short
                   /* Beacon beacon = (Beacon)curMap.get(regionID);
                    Notification.Builder n  = new Notification.Builder(this)
                            .setContentTitle("Welcome")
                            .setContentText(beacon.getGreeting())
                            .setSmallIcon(R.drawable.PwC)
                            .setContentIntent(pIntent)
                            .setAutoCancel(true);

                    notificationManager.notify(0, n.build());*/


                } else{
                    //get the date and see if it has been more than an hour. if yes send notif and update date, if no do nothing
                }

                //Stop traversing because we are done
                break;
            }
        }
    }

    private void checkin() {
        //this function will call the parse.com cloud handler with params userInfo, regionID, and event Id (Optional user or system enabled boolean)
        Date now = new Date();
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("userInfo", User_Info);
        params.put("regionID", closestBeaconId);
        params.put("eventId", EVENT_ID);
        params.put("timeStamp", now);
        ParseCloud.callFunctionInBackground("checkIn", params, new FunctionCallback<String>() {
           public void done(String response, ParseException e) {
                if (e == null) {
                    // ratings is 4.5
                    Log.i(TAG, "CheckIn Response is: " + response);
                } else if (e != null){
                    Log.i(TAG, "There was an Error Checking in:  " + e.getLocalizedMessage());
                }
            }
        });
    }

    class updateClosestBeacon extends TimerTask {
        public void run() {
            Log.i(TAG, "UpdateTimerFired");

            //trying to sort by distance
            Collections.sort(seenBeacons, new Comparator<Map<String, String>>() {
                @Override
                public int compare(final Map<String, String> map1, final Map<String, String> map2) {
                    // Get fields from maps, compare
                    Double map1Distance = Double.parseDouble(map1.get("Distance"));
                    Double map2Distance = Double.parseDouble(map2.get("Distance"));

                    return map1Distance.compareTo(map2Distance);

                }
            });

            Log.i(TAG, "SeenBeacons" + seenBeacons);
            Log.i(TAG, "closest beacon is" + seenBeacons.get(0));

            //if null, set to the closest beacon, if not null and not equal to seenBeacons(0) set to seenBeacons(0)
            if (closestBeaconId == null){
                closestBeaconId = seenBeacons.get(0).get("Identifier");
                //call checkin
                checkin();

            } else if (!(closestBeaconId.equals(seenBeacons.get(0).get("Identifier")))) {
                closestBeaconId = seenBeacons.get(0).get("Identifier");
                //call checkin
                checkin();
            }

            //clear the list
            seenBeacons.clear();

        }
    }

    public void setUser_Info(String user_info) {
        //set the private static String EVENT_ID
        User_Info = user_info;
    }

    public void setEventId(String event_id) {
        //set the private static String User_Info
        EVENT_ID = event_id;
    }

    private Boolean checkRequiredIds(){
        if (EVENT_ID != null && !EVENT_ID.isEmpty() && User_Info != null && !User_Info.isEmpty()) {
            //check parse.com
            return true;
        }else {
            return  false;
        }
    }

    public void setMainIntent (Intent mainIntent){
        MAIN_ACTIVITY = mainIntent;
    }


}

