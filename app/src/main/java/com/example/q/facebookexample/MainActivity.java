package com.example.q.facebookexample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import org.json.JSONObject;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    final int showContactPermission = 100;       // for permission checking final value
    final int showGalleryPermission = 101;       // for permission checking final value


    private CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void facebookLoginButtonGone() {
        Button facebookLoginButton = (Button) findViewById(R.id.facebookLoginButton);
        TextView loginNoticeTextView = (TextView) findViewById(R.id.loginNoticeTextView);
        facebookLoginButton.setVisibility(View.GONE);
        loginNoticeTextView.setVisibility(View.GONE);
    }


    public void abcButtonGone() {
        LinearLayout logicLinearLayout = (LinearLayout) findViewById(R.id.logicLinearLayout);
        logicLinearLayout.setVisibility(View.GONE);
    }


    @Override
    protected void onResume() {
        if (AccessToken.getCurrentAccessToken() != null) { // check facebook logged in state
            facebookLoginButtonGone();  // facebook logged in state, invisible facebook login button
        } else {
            abcButtonGone();        // else make A, B, C button invisible
        }
        super.onResume();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    public void facebookLoginOnClick(View view) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            @Override
            public void onSuccess(final LoginResult result) {

                GraphRequest request;
                request = GraphRequest.newMeRequest(result.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject user, GraphResponse response) {
                        if (response.getError() != null) {

                        } else {
                            Log.i("TAG", "user: " + user.toString());
                            Log.i("TAG", "AccessToken: " + result.getAccessToken().getToken());
                            setResult(RESULT_OK);

                            Intent i = new Intent(MainActivity.this, MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("test", "Error: " + error);
                //finish();
            }

            @Override
            public void onCancel() {
                //finish();
            }
        });
    }

    /** override onRequestPermissionResult for permission checking final value
     * if requestCode is MY_PERMISSION_REQUEST_READ_CONTACTS check the grantResults it is granted or not
     * if granted showAddress function call else show text No Permission */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case showContactPermission: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showContact(getCurrentFocus()); // permission was granted, yay! Do the contacts-related task you need to do.
                }
                else {      // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(this, "No Permissions ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case showGalleryPermission: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showGallery(getCurrentFocus()); // permission was granted, yay! Do the contacts-related task you need to do.
                }
                else {      // permission denied, boo! Disable the functionality that depends on this permission.
                    Toast.makeText(this, "No Permissions ", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void showContact(View view) {
        Intent intent = new Intent(this, DisplayContactActivity.class);
        // check there is the permission READ_CONTACTS if there is not show pop-up for request Permission, otherwise startActivity showAddress intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, showContactPermission);
            }
            else if(checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, showContactPermission);
            }
            else if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, showContactPermission);
            }
            else{
                startActivity(intent);
            }
        }
        else {
            startActivity(intent);
        }
    }

    public void showGallery(View view){
        Intent intent = new Intent(this, DisplayGalleryActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, showGalleryPermission);
        }
        else{
            startActivity(intent);
        }
    }
}
