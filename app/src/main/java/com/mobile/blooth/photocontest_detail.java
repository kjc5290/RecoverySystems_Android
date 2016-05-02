package com.mobile.blooth;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by KevinCostello on 7/17/15.
 */
public class photocontest_detail extends AppCompatActivity {

    private String detailObjectId;
    private ParseObject object;
    private Toolbar toolbar;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photocontest_detail1);
        setToolbar();


        pDialog = new ProgressDialog(photocontest_detail.this);
        pDialog.setMessage("Loading...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        // get the objectidiD for object form intent
        Bundle bundle=this.getIntent().getExtras();
        //use the stirng to query parse
        detailObjectId=bundle.getString("objectId");

        final ParseQuery<ParseObject> query = ParseQuery.getQuery("Social");
        query.whereEqualTo("objectId", detailObjectId);
        query.include("createdBy");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> eventList, ParseException e) {
                object = eventList.get(0);
                //description
                TextView description = (TextView) findViewById(R.id.description);
                description.setText(object.getString("caption"));

                // Add the title view
//                TextView userName = (TextView) findViewById(R.id.user);
//                userName.setText(object.getString("createdBy.name"));

                TextView numVotes = (TextView) findViewById(R.id.numVotes);
                numVotes.setText(object.get("votes").toString()+" Likes");

                //image
                ParseImageView image = (ParseImageView) findViewById(R.id.image);
                ParseFile imageFile = object.getParseFile("image");
                if (imageFile != null) {
                    image.setParseFile(imageFile);
                    image.loadInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                pDialog.dismiss();
                            }else {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        });


        final TextView button = (TextView) findViewById(R.id.likeButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                if (button.isEnabled()== true) {
                    button.setEnabled(false);
                    object.increment("votes", 1);
                    object.saveInBackground();
                }

            }
        });

    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle("BloothEvent");
            setSupportActionBar(toolbar);
        }
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
