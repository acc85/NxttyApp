package com.nxtty.nxttyapp.Fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.AbstractCursor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nxtty.nxttyapp.BackendServicesUtility;
import com.nxtty.nxttyapp.Constants;
import com.nxtty.nxttyapp.CustomViews.ProfilePhotoPreference;
import com.nxtty.nxttyapp.R;
import com.nxtty.nxttyapp.SharedPref;
import com.nxtty.nxttyapp.Utilities;
import com.nxtty.nxttyapp.models.Profile;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

/**
 * Created by Raymond on 04/03/2015.
 */
public class ProfileFragment extends PreferenceFragment {

    private Toolbar toolbar;
    private final int REQUEST_IMAGE_CAPTURE = 0;
    private ProfilePhotoPreference choosePhotoPref;
    private SwitchPreference avatarPref;
    private SwitchPreference privateChat;
    private static final int GALLERY_INTENT_CALLED = 1;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.profile_fragment_layout,null);

        addPreferencesFromResource(R.xml.profile_preferences);

        toolbar = (Toolbar)view.findViewById(R.id.settings_toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.inflateMenu(R.menu.main);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        toolbar.setTitle(getResources().getString(R.string.settings_profile));

        choosePhotoPref = (ProfilePhotoPreference) findPreference("profile_photo");

        choosePhotoPref.notifyDependencyChange(false);



        choosePhotoPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                new AlertDialog.Builder(getActivity()).setTitle("Select Option").setItems(R.array.choice_list, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            openCamera();
                        } else {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.complete_action)), GALLERY_INTENT_CALLED);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_CHOOSER);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);
                            }
                        }
                    }
                }).show();
                return false;
            }
        });


        avatarPref = (SwitchPreference)findPreference("avatar");
        avatarPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });

        privateChat = (SwitchPreference)findPreference("private_chat");
        privateChat.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                return false;
            }
        });

        return view;
    }

    public void openCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
        }
    }


    public void getImageFromIntentData(Intent data){
        Uri uri = data.getData();
        final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        getActivity().grantUriPermission(getActivity().getPackageName(), uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        String[] projection = {
                MediaStore.Images.Media.DATA
        };
        CursorLoader cursorLoader = new CursorLoader(getActivity(), data.getData(), projection, null, null, null);
        cursorLoader.startLoading();
        cursorLoader.registerListener(0, new android.support.v4.content.Loader.OnLoadCompleteListener<Cursor>() {
            @Override
            public void onLoadComplete(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
                if (data.moveToNext()) {
                    String uri = data.getString(0).toString();
                    File file = new File(uri);
                    try {
                        uploadImage(file);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }


    public void uploadImage(final File file) throws FileNotFoundException {
        final Profile profile = Profile.getProfile();
        final ProgressDialog progressdialog = new ProgressDialog(getActivity());
        progressdialog.setMessage("Uploading Image");
        final RequestParams requestParams = new RequestParams();
        requestParams.put("nxtID", profile.nxtAccountId);
        requestParams.put("deviceID", profile.deviceId);
        requestParams.put("key", Constants.ParamKey);

        requestParams.put("deletePlanID",0);
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                Bitmap resizedBitmap = Utilities.resizeBitmap(bitmap);
                FileOutputStream out = null;
                try {
                    String fileName = file.getName();
                    File file = new File(getActivity().getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()),fileName);
                    out = new FileOutputStream(file);
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG,100,out);
                    out.close();
                    requestParams.put("file", file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        new AsyncHttpClient().post(Constants.private_update_user_details, requestParams, new AsyncHttpResponseHandler() {
                            @Override
                            public void onStart(){
                                progressdialog.show();
                            }

                            @Override
                            public void onFinish(){
                                progressdialog.cancel();
                            }


                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                try {
                                    String string = new String(responseBody,"UTF8");
                                    Log.e(ProfileFragment.class.getName(),"Success:"+string);
                                    BackendServicesUtility.updateUserProfileData(profile.nxtAccountId, createProfileUpdateResponseHandler());
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                try {
                                    String string = new String(responseBody,"UTF8");
                                    Log.e(ProfileFragment.class.getName(),"Failure:"+string);
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

    public AsyncHttpResponseHandler createProfileUpdateResponseHandler(){
        return new AsyncHttpResponseHandler() {

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
                    choosePhotoPref.notifyChanges();
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
        };
    }


    @SuppressLint("NewApi")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == getActivity().RESULT_OK)
            switch(requestCode){
                case REQUEST_IMAGE_CAPTURE: {
                    getImageFromIntentData(data);
                    break;
                }
                case GALLERY_INTENT_CALLED: {
                    getImageFromIntentData(data);
                    break;
                }
                case GALLERY_KITKAT_INTENT_CALLED: {
                    getImageFromIntentData(data);
                    break;
                }
            }
    }
}
