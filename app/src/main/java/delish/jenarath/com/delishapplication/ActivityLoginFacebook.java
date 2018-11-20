package delish.jenarath.com.delishapplication;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Created by admin on 15/11/2018 AD.
 */

public class ActivityLoginFacebook extends AppCompatActivity {

    CallbackManager callbackManager;
    String firstname, lastname;
    private LoginButton LoginButton;

    TextView txtEmail;
    public static final String PROFILE_USER_ID = "USER_ID";
    public static final String PROFILE_FIRST_NAME = "PROFILE_FIRST_NAME"; public static final String PROFILE_LAST_NAME = "PROFILE_LAST_NAME"; public static final String PROFILE_IMAGE_URL = "PROFILE_IMAGE_URL";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_facebook);
        FacebookSdk.sdkInitialize(getApplicationContext());

        callbackManager = CallbackManager.Factory.create();
        LoginButton = (LoginButton)findViewById(R.id.login_button);

        Button btnFace = (Button) findViewById(R.id.button1);
        btnFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(ActivityLoginFacebook.this, MainActivity.class);
//                startActivity(i);
                LoginButton.performClick();
            }
        });



        LoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override public void onSuccess(LoginResult loginResult)
            {
                String userLoginId = loginResult.getAccessToken().getUserId();
                Intent facebookIntent = new Intent(ActivityLoginFacebook.this, MainActivity.class);
                Profile mProfile = Profile.getCurrentProfile();
                String firstName = mProfile.getFirstName();
                String lastName = mProfile.getLastName();
                String userId = mProfile.getId().toString();
                String profileImageUrl = mProfile.getProfilePictureUri(96, 96).toString();
                facebookIntent.putExtra(PROFILE_USER_ID, userId); facebookIntent.putExtra(PROFILE_FIRST_NAME, firstName); facebookIntent.putExtra(PROFILE_LAST_NAME, lastName); facebookIntent.putExtra(PROFILE_IMAGE_URL, profileImageUrl);
                startActivity(facebookIntent);

            }
            @Override public void onCancel() { }
            @Override public void onError(FacebookException error) { }});
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onResume() {
        super.onResume();

        AppEventsLogger.activateApp(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        AppEventsLogger.deactivateApp(this);
    }


}