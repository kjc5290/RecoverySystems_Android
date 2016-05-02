package com.mobile.blooth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by KevinCostello on 6/18/15.
 */
public class webSurveyDetail extends Activity {

    private WebView webView;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView  = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        Bundle bundle = getIntent().getExtras();
        String objectId = bundle.getString("objectId");
        ParseQuery query = new ParseQuery("Surveys");
        query.whereEqualTo("objectId", objectId);
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(final List<ParseObject> surveys, ParseException e) {
                if (e == null && surveys.size() != 0) {
                    //Set custom MyListAdapter using list and custom xml
                    ParseObject object = surveys.get(0);
                    final ProgressDialog dialog = new ProgressDialog(webSurveyDetail.this);
                    dialog.setMessage(getString(R.string.loading));

                    //Set custom MyListAdapter using list and custom xml
                    webView.setWebViewClient(new WebViewClient() {
                        @Override
                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                            // TODO show you progress image
                            super.onPageStarted(view, url, favicon);
                            dialog.show();
                        }

                        @Override
                        public void onPageFinished(WebView view, String url)
                        {
                            // TODO hide your progress image
                            super.onPageFinished(view, url);
                            dialog.dismiss();
                        }
                        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                            Toast.makeText(getApplicationContext(), description, Toast.LENGTH_SHORT).show();
                        }
                    });
                    webView.loadUrl(object.getString("surveyURL"));
                    setContentView(webView);
                }
            }

            //String customHtml = "<html><body><h2>Greetings from JavaCodeGeeks</h2></body></html>";
            //webView.loadData(customHtml, "text/html", "UTF-8");

        });
    }
}
