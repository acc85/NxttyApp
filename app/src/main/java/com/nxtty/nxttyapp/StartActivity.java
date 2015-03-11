package com.nxtty.nxttyapp;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nxtty.nxttyapp.Fragments.RegisterFragment;
import com.nxtty.nxttyapp.models.Profile;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by Raymond on 16/02/2015.
 */
public class StartActivity extends BaseActivity {


    private View logo;
    private boolean disableBackButton, disallowClicking;
    private Point centerPoint;
    private boolean fromMain, starting;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        starting = true;
        ActiveAndroid.initialize(this);
        SharedPref.getInstance().setContext(getApplicationContext());
        setContentView(R.layout.start_activity_layout);
        logo = findViewById(R.id.start_activity_logo);
        Utilities.getInstance().setActivity(this);
        BackendServicesUtility.getInstance().setActivity(this);
        centerPoint = new Point();
        getWindowManager().getDefaultDisplay().getSize(centerPoint);
        centerPoint.set(centerPoint.x/2,centerPoint.y/2);
        progressBar = (ProgressBar)findViewById(R.id.start_activity_progressDialog);
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
    }


    public boolean isStarting() {
        return starting;
    }

    public void setStarting(boolean starting) {
        this.starting = starting;
    }

    public void hideKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public void showKeyboard(){
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInputFromInputMethod(getCurrentFocus().getWindowToken(), 0);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    public View getLogo(){
        return logo;
    }

    @Override
    protected void onStart() {
        if(!(getFragmentManager().getBackStackEntryCount() > 0)) {
            Bundle bundle = getIntent().getExtras();
            if (bundle != null && bundle.getString("from").equalsIgnoreCase(MainActivity.class.getSimpleName())) {
                fromMain = true;
                SharedPref.getInstance().setRememberMe(false);
                showRegisterFragmentFromMainActivity();
            } else if (SharedPref.getInstance().getRememberMe() && Profile.getCount() > 0) {
                BackendServicesUtility.loginOrRegisterAccount(getSecretPhrase(), createLoginResponseHandler());
            } else {
                if (getFragmentManager().findFragmentByTag(Constants.REGISTERFRAGMENT) == null && getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT) == null) {
                    animateInRegisterScreen();
                }
            }
        }
        super.onStart();
    }

    public String getSecretPhrase(){
        Profile profile = Profile.getProfile();
        return Utilities.getInstance().decryptDatabaseDataToString(profile.email, Utilities.getInstance().generateUUID())+"_"+
        Utilities.getInstance().decryptDatabaseDataToString(profile.password, Utilities.getInstance().generateUUID())+"_"+
        Utilities.getInstance().decryptDatabaseDataToString(profile.secretKey, Utilities.getInstance().generateUUID());
    }

    public boolean isFromMain() {
        return fromMain;
    }


    public void setFromMain(boolean fromMain) {
        this.fromMain = fromMain;
    }

    public AsyncHttpResponseHandler createLoginResponseHandler(){
        return new AsyncHttpResponseHandler() {
            String keyValue,publicKey,str;

            @Override
            public void onStart() {
                super.onStart();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    str = new String(responseBody, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JSONObject object = null;
                try {
                    object = new JSONObject(str);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    keyValue = object.getString("account");
                    publicKey = object.getString("publicKey");
                } catch (JSONException e) {
                    e.printStackTrace();
                    // TODO Auto-generated catch block
                }

                if (keyValue != "") {
                    if (Utilities.getInstance().isNetworkConnected()) {
                        BackendServicesUtility.checkSubscription(keyValue,publicKey, createResponseHandler(keyValue));
                    } else {
                        Toast.makeText(getApplicationContext(), "You are not connected", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "An error occurred while registering. Please try again", Toast.LENGTH_LONG).show();
                    try {
                        showRegisterFragmentFromMainActivity();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String str = "";
                try {
                    str = new String(responseBody, "UTF-8");
                    JSONObject object = new JSONObject(str);
                    int status = object.getInt("status");
                    if (status == 0) {

                    }
                    showRegisterFragment();
                    Toast.makeText(getApplicationContext(), "An error occurred while registering. Please try again", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                    animateInRegisterScreen();
                }
            }
        };
    }


    public AsyncHttpResponseHandler createResponseHandler(final String keyValue){
        return new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = "";
                JSONObject object = null;
                try {
                    str = new String(responseBody, "UTF-8");
                    object = new JSONObject(str);
                    boolean result = object.getBoolean("status");
                    if (!result) {
                    } else {
                        BackendServicesUtility.setGoogleCloudId();
//                        BackendServicesUtility.updateUserProfileData(keyValue);
                        startMainActivityFinishThisActivity();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        };
    }


    public void checkSubscription(final String keyValue, final String publicKey) {
        BackendServicesUtility.checkSubscription(keyValue,publicKey,createResponseHandler(keyValue));
    }


    @Override
    public void onBackPressed() {
        if(!disableBackButton)
            super.onBackPressed();

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void startMainActivityFinishThisActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent, new Bundle());
        overridePendingTransition(R.animator.activity_fade_out_slide_in_from_half_right,R.animator.activity_fade_out_scale_down);
        finish();
    }

    public void setDisableBackButton(boolean bool){
        disableBackButton = bool;
    }

    public boolean isDisableBackButton(){
        return disableBackButton;
    }

    public void setDisallowClicking(boolean bool){
        disallowClicking = bool;
    }

    public boolean isDisallowClicking(){
        return disallowClicking;
    }

    public void animateInRegisterScreen(){

        final float x = Utilities.getInstance().convertDpToPx(30);
        final float y = Utilities.getInstance().convertDpToPx(15);
        final ImageView emptyLogo = (ImageView)findViewById(R.id.start_center_emtpy_activity_logo);
        emptyLogo.setVisibility(View.VISIBLE);
        logo.setAlpha(0);
        logo.setVisibility(View.VISIBLE);
        logo.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    logo.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    logo.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                emptyLogo.animate()
                    .x(x)
                    .y(y)
                    .setStartDelay(200)
                    .setDuration(400)
                    .setInterpolator(new AccelerateDecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            logo.animate()
                                    .alpha(1f)
                                    .setListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            emptyLogo.setVisibility(View.GONE);
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    })
                                    .start();
                            showRegisterFragment();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                .start();
            }
        });
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    public void showRegisterFragmentFromMainActivity(){
        starting = false;
        logo.setVisibility(View.VISIBLE);
        getFragmentManager().beginTransaction()
                .replace(R.id.start_activity_container, new RegisterFragment(), Constants.REGISTERFRAGMENT)
                .commit();
    }



    public void showRegisterFragment(){
        starting = false;
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in, R.animator.fade_out, R.animator.slide_in_from_left_to_right, R.animator.fade_out)
                .replace(R.id.start_activity_container, new RegisterFragment(), Constants.REGISTERFRAGMENT)
                .commit();
    }


}
