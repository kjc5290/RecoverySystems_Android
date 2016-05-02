package com.mobile.blooth.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.mobile.blooth.ActivityFeed;
import com.mobile.blooth.DispatchActivity;
import com.mobile.blooth.MapsActivity;
import com.mobile.blooth.MyOffers;
import com.mobile.blooth.OfferStore;
import com.mobile.blooth.PhotoContestActivity;
import com.mobile.blooth.R;
import com.mobile.blooth.SurveyActivity;
import com.mobile.blooth.Adapter.eventScheduleAdapter;
import com.mobile.blooth.event_Detail;
import com.mobile.blooth.favScheduleAdapter;
import com.mobile.blooth.my_profile;
import com.parse.ParseObject;
import com.parse.ParseQueryAdapter.OnQueryLoadListener;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by KevinCostello on 6/15/15.
 */
public class EventScheduleActivity extends AppCompatActivity {

    private eventScheduleAdapter scheduleAdapter;
    private com.mobile.blooth.favScheduleAdapter favScheduleAdapter;
    private ListView listView;
    ProgressDialog pDialog;
    private Boolean favVisible;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private ListView leftDrawerList;
    private ArrayAdapter<String> navigationDrawerAdapter;
    private String[] leftSliderData = {"Home", "Change Event", "My Offers", "Offer Store", "My Profile", "Event Map", "Event Surveys", "Photo Contest", "Activity Feed", "Favourite","Logout"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setToolbar();
        //set the listview general full screen layout from activity_main.xml
        listView = (ListView) findViewById(R.id.listView2);
        favVisible = Boolean.FALSE;

        leftDrawerList = (ListView) findViewById(R.id.left_drawer);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        navigationDrawerAdapter=new ArrayAdapter<String>( EventScheduleActivity.this, android.R.layout.simple_list_item_1, leftSliderData);
        leftDrawerList.setAdapter(navigationDrawerAdapter);
        leftDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i){
                    case 0:
                        toolbar.setTitle("Event Schedule");
                        listView.setAdapter(scheduleAdapter);
                        scheduleAdapter.loadObjects();

                        break;
                    case 1:
                        Intent chooser = new Intent(EventScheduleActivity.this, EventChooserActivity.class);
                        chooser.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(chooser);
                        break;
                    case 2:
                        Intent my_offers = new Intent(EventScheduleActivity.this, MyOffers.class);

                        startActivity(my_offers);

                        break;
                    case 3:
                        Intent storeOffers = new Intent(getApplicationContext(), OfferStore.class);

                        startActivity(storeOffers);
                        break;
                    case 4:
                        Intent profile = new Intent(getApplicationContext(), my_profile.class);

                        startActivity(profile);
                        break;
                    case 5:
                        Intent eventMap = new Intent(getApplicationContext(), MapsActivity.class);

                        startActivity(eventMap);
                        break;

                    case 6:
                        Intent eventSurveys = new Intent(getApplicationContext(), SurveyActivity.class);

                        startActivity(eventSurveys);
                        break;

                    case 7:
                        Intent photocontest = new Intent(getApplicationContext(), PhotoContestActivity.class);

                        startActivity(photocontest);
                        break;

                    case 8:
                        Intent activityFeed = new Intent(getApplicationContext(), ActivityFeed.class);

                        startActivity(activityFeed);
                        break;

                    case 9:
                        toolbar.setTitle("Favourites");
                        listView.setAdapter(favScheduleAdapter);
                        favScheduleAdapter.loadObjects();

                        break;

                    case 10:
                        ParseUser.logOut();
                        // Start and intent for the dispatch activity
                        Intent intent = new Intent(EventScheduleActivity.this, DispatchActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        break;

                }
            }
        });


        initDrawer();

        pDialog = new ProgressDialog(EventScheduleActivity.this);
        // Set progressbar title
        // Set progressbar message
        //pDialog.setTitle("Activity Feed");
        pDialog.setMessage("Loading..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();

        final SharedPreferences prefs = this.getSharedPreferences("com.mobile.blooth", Context.MODE_PRIVATE);

//        //set the title
//        getSupportActionBar().setTitle(prefs.getString("currentEvent", "No Event Selected") + " " + "Schedule");
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));


        //check if event id is set
        String kEventId = prefs.getString("objectId", "No Event Selected");
        Log.i("EventID", kEventId);

        //check if the user has selected a event
        if(prefs.getString("currentEvent", "No Event Selected").equalsIgnoreCase("No Event Selected") || ParseUser.getCurrentUser().get("eventId") == null)
        {
            Intent intent = new Intent(EventScheduleActivity.this, EventChooserActivity.class);
            startActivity(intent);
            finish();
        }
        else
        {
            Toast.makeText(getApplicationContext(), prefs.getString("currentEvent", "No Event Selected"), Toast.LENGTH_SHORT).show();
        }

        //start the ParseQueryAdapter for the view/data - https://parse.com/docs/android/api/com/parse/ParseQueryAdapter.html
        scheduleAdapter = new eventScheduleAdapter(this, kEventId);
        favScheduleAdapter = new favScheduleAdapter(this, kEventId);

        scheduleAdapter.addOnQueryLoadListener(new OnQueryLoadListener<ParseObject>() {
            public void onLoading() {
                // Trigger any "loading" UI
                pDialog.show();
            }

            public void onLoaded(List<ParseObject> objects, Exception e) {
                // Execute any post-loading logic, hide "loading" UI
                if(e == null){
                    pDialog.dismiss();
                }



            }
        });

        favScheduleAdapter.addOnQueryLoadListener(new OnQueryLoadListener<ParseObject>() {
            public void onLoading() {
                // Trigger any "loading" UI
                pDialog.show();
            }

            public void onLoaded(List<ParseObject> objects, Exception e) {
                // Execute any post-loading logic, hide "loading" UI
                pDialog.dismiss();
            }
        });

        // Initialize ListView and set initial to the view's parsequeryadapter eventScheduleAdapter for the view layout and data

        listView.setAdapter(scheduleAdapter);
        scheduleAdapter.loadObjects();

        //Set click listener for each item in the listview (objects from parsqueryadapter) and pass the objectId of the choosen parse.com object to detail view
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> scheduleAdapter, View myView, int myItemInt, long mylng) {
                ParseObject selectedFromList = (ParseObject) (listView.getItemAtPosition(myItemInt));
                Log.i("objectId", selectedFromList.getObjectId());
                //pass intent to detail view
                Intent search = new Intent(EventScheduleActivity.this, event_Detail.class);
                search.putExtra("objectId", selectedFromList.getObjectId());
                startActivity(search);
            }
        });


    }


    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle("Event Schedule");
            setSupportActionBar(toolbar);
        }

    }

    private void initDrawer() {

        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}

