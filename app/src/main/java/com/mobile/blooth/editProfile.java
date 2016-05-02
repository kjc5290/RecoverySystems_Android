package com.mobile.blooth;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

/**
 * Created by KevinCostello on 6/29/15.
 */
public class editProfile extends AppCompatActivity {
    // UI references.

    private EditText nameEditText;
    private EditText companyEditText;
    private EditText emailEditText;
    private EditText positionEditText;
    private ImageView profile;
    private Uri mImageCaptureUri;
    private File file;
    private ParseUser currentUser;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_profile);
        setToolbar();
        currentUser = ParseUser.getCurrentUser();
//        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#000080")));

        // Set up the signup form.
        nameEditText = (EditText) findViewById(R.id.name_edit_text);
        companyEditText = (EditText) findViewById(R.id.company_edit_text);
        emailEditText = (EditText) findViewById(R.id.email_edit_text);
        positionEditText = (EditText) findViewById(R.id.position_edit_text);

        positionEditText.setText(currentUser.getString("title"));
        nameEditText.setText(currentUser.getString("name"));
        companyEditText.setText(currentUser.getString("company"));
        emailEditText.setText(currentUser.getString("email"));

        profile = (ImageView) findViewById(R.id.imageView);
        emailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == R.id.edittext_action_signup ||
                        actionId == EditorInfo.IME_ACTION_UNSPECIFIED) {
                    save();
                    return true;
                }
                return false;
            }
        });

        Button profile_picture = (Button) findViewById(R.id.profile_button);
        profile_picture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
            }
        });

        // Set up the submit button click handler
        Button mActionButton = (Button) findViewById(R.id.action_button);
        mActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                save(); // switch to save user
            }
        });
    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        SharedPreferences prefs = this.getSharedPreferences(
                "com.mobile.blooth", Context.MODE_PRIVATE);
        if (toolbar != null) {
            toolbar.setTitle("Edit Profile");
            setSupportActionBar(toolbar);
        }

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    profile.setImageURI(selectedImage);
                    file = new File(selectedImage.getPath());
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    profile.setImageURI(selectedImage);
                    file = new File(selectedImage.getPath());
                }
                break;
        }
    }

    private void save() {

        String name = nameEditText.getText().toString().trim();
        String company = companyEditText.getText().toString().trim();
        String position = positionEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();


        // Set up a progress dialog
        final ProgressDialog dialog = new ProgressDialog(editProfile.this);
        dialog.setMessage(getString(R.string.save));
        dialog.show();

        // Set up a new Parse user

        currentUser.setEmail(email);
        currentUser.put("name", name);
        currentUser.put("company", company);
        currentUser.put("title", position);
        if (file != null){
            currentUser.put("thumbnail", file);

        }
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                dialog.dismiss();
                if (e != null) {
                    // Show the error message
                    Toast.makeText(editProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    // Start an intent for the dispatch activity
                    Intent intent = new Intent(editProfile.this, my_profile.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });

    }
}
