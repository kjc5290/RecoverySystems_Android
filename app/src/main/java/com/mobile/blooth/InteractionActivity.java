package com.mobile.blooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseImageView;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by KevinCostello on 7/22/15.
 */
public class InteractionActivity extends Activity {
    Interaction interaction;
    String type;
    ProgressDialog pDialog;
    VideoView videoview;
    ParseImageView imageView;
    WebView webView;
    ParseFile interactionFile;
    String webLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interaction_activity);

        videoview = (VideoView)findViewById((R.id.videoView));
        imageView = (ParseImageView)findViewById((R.id.image));

        webView  = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);

        // Create a progressbar
        pDialog = new ProgressDialog(InteractionActivity.this);
        // Set progressbar title
        pDialog.setTitle("Event Content");
        // Set progressbar message
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        // Show progressbar
        pDialog.show();


        type = new String();
        type = getIntent().getExtras().getString("type");

        final String objectId = getIntent().getExtras().getString("objectId");

        //Log.i("interaction", objectId);
        Log.i("InteractionActivity", "Interaction Object : " + objectId);
        Log.i("InteractionActivity", "Type is: " + type);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Interaction");
        query.getInBackground(objectId, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    // object will be your game score
                    interaction = (Interaction) object;

                    if (interaction.getFile() != null) {

                        pDialog.show();
                        ParseFile file = interaction.getFile();
                        interactionFile = interaction.getFile();
                        file.getDataInBackground(new GetDataCallback() {
                            public void done(byte[] data, ParseException e) {
                                if (e == null) {
                                    // data has the bytes for the resume
                                    final File file;
                                    String filePath = null;
                                    final String fileName = "interactionTemp";
                                    try {

                                        file = File.createTempFile(fileName, null, getApplicationContext().getCacheDir());
                                        FileOutputStream fos = new FileOutputStream(file);
                                        fos.write(data);
                                        fos.close();
                                        filePath = file.getAbsolutePath();
                                        Log.e("FilePath", filePath);

                                        videoview.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                            public void onCompletion(MediaPlayer mp) {
                                                file.delete(); // get rid of temp file on completion
                                            }
                                        });

                                    } catch (IOException error) {
                                        // Error while creating file
                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
                                    }

                                    if (type.equalsIgnoreCase("video")) {
                                        videoview.setVisibility(View.VISIBLE);
                                        try {

                                            // Start the MediaController
                                            MediaController mediacontroller = new MediaController(InteractionActivity.this);
                                            mediacontroller.setAnchorView(videoview);
                                            // Get the URL from String VideoURL

                                            videoview.setMediaController(mediacontroller);
                                            videoview.setVideoPath(filePath);

                                        } catch (Exception error) {
                                            Log.e("Error", error.getMessage());
                                            e.printStackTrace();
                                        }

                                        videoview.requestFocus();
                                        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                            // Close the progress bar and play the video
                                            public void onPrepared(MediaPlayer mp) {
                                                pDialog.dismiss();
                                                //setContentView(videoview);
                                                videoview.start();
                                            }
                                        });


                                    }

                                if (type.equalsIgnoreCase("image")) {

                                    Toast.makeText(getApplicationContext(), "image", Toast.LENGTH_SHORT).show();
                                    imageView.setVisibility(View.VISIBLE);
                                    if (interactionFile != null){
                                    imageView.setParseFile(interactionFile);
                                    }else {
                                        Toast.makeText(getApplicationContext(), "Null image file", Toast.LENGTH_LONG).show();
                                    }
                                    imageView.loadInBackground(new GetDataCallback() {
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

                                } else {
                                    // something went wrong with getData
                                }
                            }
                        });
                    }

                    if (type.equalsIgnoreCase("website")) {
                        Toast.makeText(getApplicationContext(), "Called from website", Toast.LENGTH_SHORT).show();
                        //Log.i("Interaction", interaction.getWebLink());

                        webView.setWebViewClient(new WebViewClient() {
                            @Override
                            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                // TODO show you progress image
                                super.onPageStarted(view, url, favicon);
                                pDialog.show();
                            }

                            @Override
                            public void onPageFinished(WebView view, String url) {
                                // TODO hide your progress image
                                super.onPageFinished(view, url);
                                pDialog.dismiss();
                            }

                            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
                            }
                        });
                        webView.loadUrl(interaction.getWebLink());
                        setContentView(webView);

                    }

                    if (type.equalsIgnoreCase("offer")) {
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Offers");
                        query.getInBackground(interaction.get("offerId").toString(), new GetCallback<ParseObject>() {
                            public void done(final ParseObject object1, ParseException e) {
                                new AlertDialog.Builder(InteractionActivity.this)
                                        .setTitle("You received an offer " + object1.get("title"))
                                        .setMessage("Once you redeem this offer you will not be able to access it again! Do you want to add this offer to your profile?")
                                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {

                                                ParseUser user = ParseUser.getCurrentUser();
                                                ParseQuery<ParseUser> userTest = ParseUser.getQuery();
                                                userTest.getInBackground(user.getObjectId(), new GetCallback<ParseUser>() {
                                                    public void done(ParseUser userCurrent, ParseException e) {
                                                        ArrayList<String> offerArray = (ArrayList<String>) userCurrent.get("offersArray");
                                                        offerArray.add(object1.getObjectId());
                                                        userCurrent.put("offersArray", offerArray);
                                                        userCurrent.saveInBackground();
                                                        Toast.makeText(getApplicationContext(), "Added " + object1.get("title")+ " to My Offers", Toast.LENGTH_LONG).show();
                                                        // call the redeem view
                                                        Intent redeemOffer = new Intent(InteractionActivity.this, MyOffers.class);
                                                        startActivity(redeemOffer);
                                                    }
                                                });


                                            }
                                        })
                                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                // do nothing
                                                Intent redeemOffer = new Intent(InteractionActivity.this, DispatchActivity.class);
                                                startActivity(redeemOffer);
                                            }
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        });
                    }

                } else {
                    // something went wrong
                }
            }
        });

    }

}