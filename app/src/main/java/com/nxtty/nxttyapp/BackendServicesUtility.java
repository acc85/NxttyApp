package com.nxtty.nxttyapp;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nxtty.nxttyapp.Fragments.ProfileFragment;
import com.nxtty.nxttyapp.models.Profile;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/**
 * Created by Raymond on 22/02/2015.
 */
public class BackendServicesUtility extends AsyncHttpClient {

    private Activity activity;

    private static BackendServicesUtility ourInstance = new BackendServicesUtility();

    public static BackendServicesUtility getInstance() {
        return ourInstance;
    }

    private BackendServicesUtility() {
    }


    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public static void initialiseNewAccountTransaction(final String keyValue, final String publicKey) {
        RequestParams params = new RequestParams();
        params.put("recipient", keyValue);
        params.put("recipientPublicKey", publicKey);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.nxtCoinURL, params, new AsyncHttpResponseHandler() {

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
                    System.out.println("result is:"+object.toString());
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    public static void updateUserProfileData(String keyValue, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        SharedPref.getInstance().setUpdatingProfile(true);
        RequestParams params = new RequestParams();
        params.put("nxtID", keyValue);
        params.put("key", Constants.ParamKey);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.getUserDetailsUrl , params, asyncHttpResponseHandler != null ? asyncHttpResponseHandler: new AsyncHttpResponseHandler() {

            @Override
            public void onFinish() {
                SharedPref.getInstance().setUpdatingProfile(false);
                super.onFinish();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str = "";
                JSONObject object = null;
                try {
                    str = new String(responseBody, "UTF-8");
                    System.out.println("success:"+str);
                    object = new JSONObject(str);
                    Profile profile = Profile.getCurrentProfile();
                    profile.nxtAccountId = object.getString("nxtAccountId");
                    profile.registrationTimeStamp = object.getLong("registrationDate");
                    profile.name = object.getString("nameAlias");
                    profile.city = object.getString("city");
                    profile.school = object.getString("school");
                    profile.gender = object.getString("gender");
                    profile.profileImageUrl = Constants.private_messaging_image_base_url+object.getString("avatar");
                    profile.status = object.getString("status");
                    profile.deleteMessageTime = object.getInt("deletePlanId");
                    profile.save();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String str = "";
                JSONObject object = null;
                try {
                    str = new String(responseBody, "UTF-8");
                    System.out.println("failed:"+str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void setGoogleCloudId() throws IOException {
        final Profile profile = Profile.getProfile();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getInstance().activity);
                try {
                    profile.deviceId = gcm.register(Constants.gcm_sender_id);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profile.save();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        RequestParams requestParams = new RequestParams();
                        requestParams.put("nxtID", profile.nxtAccountId);
                        requestParams.put("deviceID", profile.deviceId);
                        requestParams.put("deviceType", "Android");
                        requestParams.put("key", Constants.ParamKey);
                        requestParams.put("deletePlanID",0);

                        new AsyncHttpClient().post(Constants.private_update_user_details_simple,requestParams, new AsyncHttpResponseHandler() {

                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String str = "";
                                JSONObject object = null;
                                try {
                                    str = new String(responseBody, "UTF-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                updateUserProfileData(profile.nxtAccountId,null);
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                String str = "";
                                JSONObject object = null;
                                try {
                                    str = new String(responseBody, "UTF-8");
                                    System.out.println("failed set gid:" + str);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    public static void checkSubscription(String keyValue, final String publicKey, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put("nxtID", keyValue);
        params.put("key", Constants.ParamKey);
        new AsyncHttpClient().post(Constants.private_messaging_base_url + "subscriber/issubscribed", params,asyncHttpResponseHandler);
    }


    public static void loginOrRegisterAccount(String secretPhrase, AsyncHttpResponseHandler asyncHttpResponseHandler) {
        RequestParams params = new RequestParams();
        params.put(Constants.REQUESTTYPE, Constants.GETACCOUNTID);
        params.put(Constants.SECRETPHRASE, secretPhrase);
        params.put(Constants.KEY, Constants.ParamKey);
        new AsyncHttpClient().post(Constants.baseUrl, params, asyncHttpResponseHandler);
    }
}
