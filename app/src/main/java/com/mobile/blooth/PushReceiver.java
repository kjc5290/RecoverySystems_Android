package com.mobile.blooth;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by KevinCostello on 7/31/15.
 */
public class PushReceiver extends ParsePushBroadcastReceiver{

    String offerId;
    ParseObject offerObject;
    JSONObject jObject;
    NotificationManager notificationManager;

    @Override
    public void onReceive(final Context context, Intent intent) {

        Bundle extras = intent.getExtras();

       notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        String message = extras != null ? extras.getString("com.parse.Data")
                : "";

        Log.e("message ", " " + message);



        try {
            if (message != null && !message.equals("")) {

                jObject = new JSONObject(message);

                if (jObject.has("offerId") && jObject.getString("offerId")!= null) {
                    final String title = jObject.getString("offerId");
                    final Intent cIntent = new Intent(context, MyOffers.class);
                    offerId = jObject.getString("offerId");
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    ParseUser user = ParseUser.getCurrentUser();
                    query.whereEqualTo("objectId", user.getObjectId());
                    Log.e("push offer: ",  offerId);
                    query.getFirstInBackground(new GetCallback<ParseUser>() {
                        @Override
                        public void done(final ParseUser parseUser, ParseException e) {
                            //Is relationship exists?
                            ArrayList<String> offerArray = (ArrayList<String>) parseUser.get("offersArray");
                            offerArray.add(offerId);
                            parseUser.put("offersArray", offerArray);
                            parseUser.saveInBackground();
                            String string = "You just recieved an offer from the event!";
                            sendNotif(cIntent, title, string, context);
                        }
                    });

                }

                if (jObject.has("alert") && jObject.getString("alert") != null) {

                        Intent cIntent = new Intent(context, DispatchActivity.class);
                        String messagePush = jObject.getString("alert");

                        sendNotif(cIntent, context.getResources().getString(R.string.app_name), messagePush, context);

                }


            }

        }

        catch (JSONException e) {
            e.printStackTrace();
        }

    }
    protected void sendNotif(Intent activityIntent, String title, String content, Context context){

        PendingIntent pIntent = PendingIntent.getActivity(context.getApplicationContext(), 0, activityIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder n  = new Notification.Builder(context.getApplicationContext())
                .setContentTitle(title)
                .setContentText(content)
                .setSmallIcon(R.drawable.bloothiconnotif)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 1000, 1000, 1000, 1000})
                .setPriority(2)
                .setTicker(content)
                ;

        notificationManager.notify(0, n.build());

    }


}

