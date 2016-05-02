package com.mobile.blooth;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.mobile.blooth.Activity.EventChooserActivity;
import com.mobile.blooth.Activity.EventScheduleActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by KevinCostello on 6/30/15.
 */
public class PhotoContestActivity extends AppCompatActivity {
    private photoContestAdapter photoAdapter;
    private GridView gridView;
    private static final int CAMERA_REQUEST = 1888;
    private static final int RESULT_LOAD_IMAGE = 1889;
    ProgressDialog pDialog;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_contest);
        pDialog = new ProgressDialog(PhotoContestActivity.this);
        // Set progressbar title
        // Set progressbar message
        //pDialog.setTitle("Activity Feed");
        pDialog.setMessage("Loading..");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();
        setToolbar();
        //set the listview general full screen layout from activity_main.xml
        gridView = (GridView) findViewById(R.id.gridview);

        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);

        //set the title
//        getSupportActionBar().setTitle("Event PhotoContest");
//
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));


        //check if event id is set
        String kEventId = prefs.getString("objectId", "No Event Selected");
        Log.i("EventID", kEventId);

        //check if the user has selected a event
        if (prefs.getString("currentEvent", "No Event Selected").equalsIgnoreCase("No Event Selected")) {
            Intent intent = new Intent(PhotoContestActivity.this, EventChooserActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), prefs.getString("currentEvent", "No Event Selected"), Toast.LENGTH_SHORT).show();
        }

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Social");
        query.whereEqualTo("eventId", kEventId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> photoList, ParseException e) {
                if (e == null) {
                    //Set custom MyListAdapter using list and custom xml
                    photoAdapter = new photoContestAdapter(getApplicationContext(), photoList);
                    gridView.setAdapter(photoAdapter);
                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
       // photoAdapter = new photoContestAdapter(this, kEventId);

        //gridView.setAdapter(photoAdapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> scheduleAdapter, View myView, int myItemInt, long mylng) {
                ParseObject selectedFromList = (ParseObject) (gridView.getItemAtPosition(myItemInt));
                Log.i("objectId", selectedFromList.getObjectId());
                //pass intent to detail view
                Intent showPhotoDetail = new Intent(PhotoContestActivity.this, photocontest_detail.class);
                showPhotoDetail.putExtra("objectId", selectedFromList.getObjectId().toString());
                startActivity(showPhotoDetail);
            }
        });

        pDialog.dismiss();
     }
    });
    }


//
//    //options action bar menu
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_main, menu);
//        inflater.inflate(R.menu.photocontest_menu, menu);
//        return true;
//    }
//
//    //start the new activity for each option in menu with an intent
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle item selection
//        switch (item.getItemId()) {
//
//            case R.id.action_camera:
//                new AlertDialog.Builder(PhotoContestActivity.this)
//                        .setTitle("Where do you want to add your photo from?")
//                        .setPositiveButton(R.string.camera, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                            }
//                        })
//                        .setNegativeButton(R.string.gallery, new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                // do nothing
//                                Intent intent = new Intent(
//                                        Intent.ACTION_PICK,
//                                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                                intent.setType("image/*");
//                                startActivityForResult(intent, RESULT_LOAD_IMAGE);
//
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_info)
//                        .show();
//                return true;
//
//            case R.id.logout:
//                ParseUser.logOut();
//                // Start and intent for the dispatch activity
//                Intent intent = new Intent(getApplicationContext(), DispatchActivity.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                return true;
//
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
//
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Write a description for this photo");

            // Set up the input
            final EditText input = new EditText(this);
            // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            // Set up the buttons
            builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), input.getText().toString(), //change this to make the and save the social object
                            Toast.LENGTH_SHORT).show();
                    Uri selectedImage = data.getData();
                    Bitmap bitmap;
                    Bitmap ThumbImage;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    try
                    {
                        //do some stuff here
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        ThumbImage = ThumbnailUtils.extractThumbnail(bitmap, 600, 600);
                        Bitmap finalMap = RotateBitmap(ThumbImage,90);

                        finalMap.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    }
                    catch(Exception e)
                    {

                        Toast.makeText(getApplicationContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }


                    byte[] byteArray = stream.toByteArray();
                    ParseFile newFile2 = new ParseFile("photocontestupload.png", byteArray);
                    ParseObject newObject = new ParseObject("Social");
                    newObject.put("caption",input.getText().toString());
                    newObject.put("image", newFile2);
                    newObject.put("createdBy", ParseUser.getCurrentUser());
                    newObject.put("eventId", ParseUser.getCurrentUser().getString("eventId"));
                    newObject.put("votes", 0);
                    newObject.saveInBackground();


                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Write a description for this photo");

        // Set up the input
            final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

        // Set up the buttons
            builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(getApplicationContext(), input.getText().toString(), //change this to make the and save the social object
                            Toast.LENGTH_SHORT).show();
                    Uri selectedImage = data.getData();
                    Bitmap bitmap;
                    Bitmap ThumbImage;
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    try {
                        //do some stuff here
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectedImage);
                        ThumbImage = ThumbnailUtils.extractThumbnail(bitmap, 600, 600);
                        //Bitmap finalMap = RotateBitmap(ThumbImage, 90);

                        ThumbImage.compress(Bitmap.CompressFormat.PNG, 100, stream);

                    } catch (Exception e) {

                        Toast.makeText(getApplicationContext(), e.getMessage(), //change this to make the and save the social object
                                Toast.LENGTH_SHORT).show();
                    }


                    byte[] byteArray = stream.toByteArray();
                    ParseFile newFile2 = new ParseFile("photocontestupload.png", byteArray);
                    ParseObject newObject = new ParseObject("Social");
                    newObject.put("caption", input.getText().toString());
                    newObject.put("image", newFile2);
                    newObject.put("createdBy", ParseUser.getCurrentUser());
                    newObject.put("eventId", ParseUser.getCurrentUser().getString("eventId"));
                    newObject.put("votes", 0);
                    newObject.saveInBackground();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);
        if (toolbar != null) {
            toolbar.setTitle("Event Photo Contest");
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
