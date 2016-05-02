package com.mobile.blooth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.SaveCallback;

import org.altbeacon.beacon.powersave.BackgroundPowerSaver;

public class Application extends android.app.Application {

    private static SharedPreferences preferences;

    private static ConfigHelper configHelper;
    private BackgroundPowerSaver backgroundPowerSaver;

    public Application() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        backgroundPowerSaver = new BackgroundPowerSaver(this);
        //ParseObject.registerSubclass(ParseClassName.class); - register any local subclasses
        Parse.enableLocalDatastore(this);
        //ParseFacebookUtils.initialize(this);
        ParseObject.registerSubclass(Beacon.class);
        ParseObject.registerSubclass(Interaction.class);
        Parse.initialize(this, "vSG1sUHBHr6u4yV98nhLpRhBd99TymLG7R9Pj7zK", "myMfTfqnMoboNwpU5vGWA2lD2pM6c5nShfZIGFzF");
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("Ms5sZGoxvOpRou2TfVRZGVvBKNSjPg03ynlMjctz")
                .clientKey(null)
                .server("http://parseserver-ged4p-env.us-east-1.elasticbeanstalk.com/parse/") // The trailing slash is important
        .build()
        );

        preferences = getSharedPreferences("com.mobile.blooth", Context.MODE_PRIVATE);

        //parse.com config manger
        configHelper = new ConfigHelper();
        configHelper.fetchConfigIfNeeded();

        //parsepush

        ParsePush.subscribeInBackground("global", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });
    }

    public static ConfigHelper getConfigHelper() {
        return configHelper;
    }

}