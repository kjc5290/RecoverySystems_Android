package com.mobile.blooth;

/**
 * Created by KevinCostello on 6/16/15.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mobile.blooth.Activity.EventChooserActivity;
import com.mobile.blooth.Activity.EventScheduleActivity;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import java.util.List;

public class MyOffers extends AppCompatActivity {
    private myOffersAdapter offersAdapter;
    private ListView listView;
    ProgressDialog pDialog;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);
        setToolbar();
        //set the listview general full screen layout from activity_main.xml
        listView = (ListView) findViewById(R.id.listView2);

        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);


        // Show progressbpDialog = new ProgressDialog(MyOffers.this);
        // Set progressbar title
        // Set progressbar message
        //pDialog.setTitle("Activity Feed");
        pDialog = new ProgressDialog(MyOffers.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);



        //set the title
//        getSupportActionBar().setTitle("My Offers");
//
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));


        //check if event id is set
        String kEventId = prefs.getString("objectId", "No Event Selected");
        Log.i("EventID", kEventId);

        //check if the user has selected a event
        if (prefs.getString("currentEvent", "No Event Selected").equalsIgnoreCase("No Event Selected")) {
            Intent intent = new Intent(MyOffers.this, EventChooserActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), prefs.getString("currentEvent", "No Event Selected"), Toast.LENGTH_SHORT).show();
        }

        offersAdapter = new myOffersAdapter(this, kEventId);
        offersAdapter.addOnQueryLoadListener(new ParseQueryAdapter.OnQueryLoadListener<ParseObject>() {
            public void onLoading() {
                // Trigger any "loading" UI
                pDialog.show();
            }

            public void onLoaded(List<ParseObject> objects, Exception e) {
                // Execute any post-loading logic, hide "loading" UI
                pDialog.dismiss();
            }
        });

        listView.setAdapter(offersAdapter);
        offersAdapter.loadObjects();

        //Set click listener for each item in the listview (objects from parsqueryadapter) and pass the objectId of the choosen parse.com object to detail view
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> scheduleAdapter, View myView, int myItemInt, long mylng) {
                ParseObject selectedFromList = (ParseObject) (listView.getItemAtPosition(myItemInt));
                Log.i("objectId", selectedFromList.getObjectId());
                //pass intent to detail view
                Intent showOfferDetail = new Intent(MyOffers.this, offer_detail.class);
                showOfferDetail.putExtra("objectId", selectedFromList.getObjectId());
                showOfferDetail.putExtra("title", selectedFromList.getString("title"));
                showOfferDetail.putExtra("descriptionText", selectedFromList.getString("description"));
                startActivity(showOfferDetail);
            }
        });

    }


//    //options action bar menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    //start the new activity for each option in menu with an intent
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//
//            case R.id.logout:
//                ParseUser.logOut();
//                // Start and intent for the dispatch activity
//                Intent intent = new Intent(MyOffers.this, DispatchActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                return true;
//            case R.id.event_choose:
//
//                Intent chooser = new Intent(getApplicationContext(), EventChooserActivity.class);
//                chooser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(chooser);
//                return true;
//            case R.id.event_schedule:
//
//                Intent scheduleIntent = new Intent(getApplicationContext(), EventScheduleActivity.class);
//                scheduleIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(scheduleIntent);
//                return true;
//
//            case R.id.my_offers:
//
//                Intent my_offers = new Intent(getApplicationContext(), MyOffers.class);
//                my_offers.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(my_offers);
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

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle("My Offers");
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