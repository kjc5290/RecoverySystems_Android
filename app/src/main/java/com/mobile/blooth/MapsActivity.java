package com.mobile.blooth;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mobile.blooth.Activity.EventChooserActivity;
import com.mobile.blooth.Activity.EventScheduleActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity {

    String kEventId;
    ParseObject eventObject;
    UiSettings mapSettings;
    List<Address> geocodeMatches = null;
    String Address1;
    String Address2;
    String State;
    String Zipcode;
    String Country;
    Boolean indoorFile;
    private Toolbar toolbar;
    SharedPreferences prefs;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

//        getSupportActionBar().setTitle("Event Map");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));
        setToolbar();
        indoorFile = Boolean.TRUE;

        prefs = this.getSharedPreferences("com.mobile.blooth", Context.MODE_PRIVATE);
        kEventId = prefs.getString("objectId", "No Event Selected");
        setUpMapIfNeeded();

        Log.i("EventId Is", kEventId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {

        mapSettings  = mMap.getUiSettings();

        mapSettings.setAllGesturesEnabled(true);
        mapSettings.setMyLocationButtonEnabled(true);
        mapSettings.setIndoorLevelPickerEnabled(true);
        mapSettings.setMapToolbarEnabled(true);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereEqualTo("objectId", kEventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> regionList, ParseException e) {
                eventObject = regionList.get(0);
                if (eventObject.getParseFile("eventMap")== null){
                    indoorFile = Boolean.FALSE;
                }
                try {
                    geocodeMatches = new Geocoder(getApplicationContext()).getFromLocation(eventObject.getParseGeoPoint("EventLocation").getLatitude(), eventObject.getParseGeoPoint("EventLocation").getLongitude(), 1);
                } catch (IOException error) {
                    // TODO Auto-generated catch block
                    error.printStackTrace();
                }

                if (!geocodeMatches.isEmpty()) {
                    Address1 = geocodeMatches.get(0).getAddressLine(0);
                    Address2 = geocodeMatches.get(0).getAddressLine(1);
                    State = geocodeMatches.get(0).getAdminArea();
                    Zipcode = geocodeMatches.get(0).getPostalCode();
                    Country = geocodeMatches.get(0).getCountryName();
                }


                LatLng eventLocation = new LatLng(eventObject.getParseGeoPoint("EventLocation").getLatitude(), eventObject.getParseGeoPoint("EventLocation").getLongitude());
                Marker eventLocationMarker = mMap.addMarker(new MarkerOptions()
                        .position(eventLocation)
                        .title(eventObject.getString("EventName"))
                        //.snippet
                        );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLocation, 18));
            }
        });
    }

//    //options action bar menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.map_menu, menu);
//        inflater.inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    //start the new activity for each option in menu with an intent
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//
//        switch (item.getItemId()) {
//
//            case R.id.logout:
//                ParseUser.logOut();
//                // Start and intent for the dispatch activity
//                Intent intent = new Intent(MapsActivity.this, DispatchActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                return true;
//            case R.id.event_choose:
//                Intent chooser = new Intent(MapsActivity.this, EventChooserActivity.class);
//                chooser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(chooser);
//                return true;
//
//            case R.id.event_schedule:
//                /*Intent eventSchedule = new Intent(EventScheduleActivity.this, EventScheduleActivity.class);
//                eventSchedule.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(eventSchedule);*/
//                startActivity(new Intent(this, EventScheduleActivity.class));
//                return true;
//
//            case R.id.my_offers:
//                Intent my_offers = new Intent(MapsActivity.this, MyOffers.class);
//                my_offers.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(my_offers);
//                finish();
//                return true;
//
//            case R.id.offer_store:
//                Intent storeOffers = new Intent(getApplicationContext(), OfferStore.class);
//                storeOffers.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(storeOffers);
//                return true;
//
//            case R.id.event_surveys:
//                Intent eventSurveys = new Intent(getApplicationContext(), SurveyActivity.class);
//                eventSurveys.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(eventSurveys);
//                return(true);
//
//            case R.id.profile:
//                Intent profile = new Intent(getApplicationContext(), my_profile.class);
//                profile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(profile);
//                return(true);
//
//            case R.id.photo_contest:
//                Intent photocontest = new Intent(getApplicationContext(), PhotoContestActivity.class);
//                photocontest.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(photocontest);
//                return(true);
//
//            case R.id.activity_feed:
//                Intent activityFeed = new Intent(getApplicationContext(), ActivityFeed.class);
//                activityFeed.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(activityFeed);
//                return(true);
//
//
//            case R.id.indoor_map:
//                Intent indoor_map = new Intent(getApplicationContext(), EventMap.class);
//                indoor_map.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(indoor_map);
//                invalidateOptionsMenu();
//                return(true);
//
//            case R.id.EventMap:
//                Intent eventMap = new Intent(getApplicationContext(), MapsActivity.class);
//                eventMap.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(eventMap);
//                return(true);
//
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(!indoorFile){
            menu.findItem(R.id.indoor_map).setVisible(false);
        }

        return true;
    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);
        if (toolbar != null) {
            toolbar.setTitle("Event Map");
            setSupportActionBar(toolbar);

            toolbar.setNavigationIcon(R.drawable.ic_back);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        }

    }

}
