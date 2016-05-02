package com.mobile.blooth;

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
import android.widget.Button;
import android.widget.Toast;

import com.mobile.blooth.Activity.EventChooserActivity;
import com.mobile.blooth.Activity.EventScheduleActivity;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by KevinCostello on 7/21/15.
 */
public class ActivityFeed extends AppCompatActivity {

    ParseObject object;
    Button facebookBtn;
    Button twitterBtn;
    Button visitWeb;
    ProgressDialog pDialog;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        setToolbar();
        pDialog = new ProgressDialog(ActivityFeed.this);
        // Set progressbar title
        // Set progressbar message
        //pDialog.setTitle("Activity Feed");
        pDialog.setMessage("Loading..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        visitWeb = (Button) findViewById(R.id.websiteBtn);
        facebookBtn = (Button) findViewById(R.id.facebookBtn);
        twitterBtn = (Button) findViewById(R.id.twitterBtn);


        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);

        //set the title
//        getSupportActionBar().setTitle("Activity Feed");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));


        //check if event id is set
        String kEventId = prefs.getString("objectId", "No Event Selected");
        Log.i("EventID", kEventId);

        //check if the user has selected a event
        if(prefs.getString("currentEvent", "No Event Selected").equalsIgnoreCase("No Event Selected") || ParseUser.getCurrentUser().get("eventId") == null)
        {
            Intent intent = new Intent(ActivityFeed.this, EventChooserActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), prefs.getString("currentEvent", "No Event Selected"), Toast.LENGTH_SHORT).show();
        }

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Events");
        query.whereEqualTo("objectId",kEventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> eventList, ParseException e) {
                object = eventList.get(0);

                twitterBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent showWeb = new Intent(getApplicationContext(), activityFeed_detail.class);
                        showWeb.putExtra("url", object.getString("twitterLink"));
                        startActivity(showWeb);
                    }
                });
                facebookBtn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent showWeb = new Intent(getApplicationContext(), activityFeed_detail.class);
                        showWeb.putExtra("url", object.getString("facebookLink"));
                        startActivity(showWeb);
                    }
                });
                visitWeb.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent showWeb = new Intent(getApplicationContext(), activityFeed_detail.class);
                        showWeb.putExtra("url", object.getString("websiteLink"));
                        startActivity(showWeb);
                    }
                });

                ParseImageView image = (ParseImageView) findViewById(R.id.eventPhoto);
                ParseFile imageFile = object.getParseFile("thumbnail");
                if (imageFile != null) {
                    image.setParseFile(imageFile);
                    image.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e==null) {
                                pDialog.dismiss();
                            }else {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);
        if (toolbar != null) {
            toolbar.setTitle("Activity Feed");
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


//    //options action bar menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    //start the new activity for each option in menu with an intent
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//
//            case R.id.logout:
//
//                ParseUser.logOut();
//
//                // Start and intent for the dispatch activity
//                Intent intent = new Intent(ActivityFeed.this, DispatchActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                return true;
//            case R.id.event_choose:
//                Intent chooser = new Intent(ActivityFeed.this, EventChooserActivity.class);
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
//                Intent my_offers = new Intent(ActivityFeed.this, MyOffers.class);
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

}
