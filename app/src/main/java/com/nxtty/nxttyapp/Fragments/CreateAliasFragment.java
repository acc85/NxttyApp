package com.nxtty.nxttyapp.Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nxtty.nxttyapp.BackendServicesUtility;
import com.nxtty.nxttyapp.Constants;
import com.nxtty.nxttyapp.R;
import com.nxtty.nxttyapp.SharedPref;
import com.nxtty.nxttyapp.StartActivity;
import com.nxtty.nxttyapp.Utilities;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Raymond on 23/02/2015.
 */
public class CreateAliasFragment extends Fragment {

    View create_alias_logo;
    String accountId, publicKey, email, password, secretKey;
    boolean rememberMeChecked;
    EditText aliasField;
    ProgressDialog progressDialog;


    public static CreateAliasFragment build(String accountId, String publicKey, String email, String password, String secretKey) {
        CreateAliasFragment createAliasFragment = new CreateAliasFragment();
        createAliasFragment.setAccountId(accountId);
        createAliasFragment.setPublicKey(publicKey);
        createAliasFragment.setEmail(email);
        createAliasFragment.setPassword(password);
        createAliasFragment.setSecretKey(secretKey);
        return createAliasFragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View createAliasFragmentView = inflater.inflate(R.layout.create_alias_layout, null);
        create_alias_logo = createAliasFragmentView.findViewById(R.id.create_alias_logo);
        ((StartActivity)getActivity()).setStarting(false);
        aliasField = (EditText) createAliasFragmentView.findViewById(R.id.create_alias_alias_field);

        createAliasFragmentView.findViewById(R.id.create_alias_submit_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subscribeUser();
            }
        });


        return createAliasFragmentView;
    }

    public boolean isRememberMeChecked() {
        return rememberMeChecked;
    }

    public void setRememberMeChecked(boolean rememberMeChecked) {
        this.rememberMeChecked = rememberMeChecked;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getAccountId() {
        return this.accountId;
    }

    private void subscribeUser() {
        RequestParams params = new RequestParams();
        params.put("nxtID", accountId);
        params.put("name_alias", aliasField.getText());
        //New parameter
        params.put("city", "");
        params.put("gender", "");
        params.put("school", "");
        params.put("deviceID", "");
        params.put("deviceType", "Android");
        params.put("key", Constants.ParamKey);


        // send post request for user login
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(Constants.private_messaging_base_url + "subscriber/subscribe", params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Creating Alias");
                progressDialog.show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String str, errorDetails = "";
                JSONObject object = null;
                boolean subscribeSuccessful = false;
                progressDialog.cancel();
                try {
                    str = new String(responseBody, "UTF-8");
                    object = new JSONObject(str);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                try {
                    subscribeSuccessful = object.getBoolean("status");
                    errorDetails = object.getString("errorDetail");
                } catch (JSONException e) {
                }

                if (subscribeSuccessful) {
                    SharedPref.getInstance().setNxtAccountId(accountId);
                    SharedPref.getInstance().setRememberMe(rememberMeChecked);
                    if (SharedPref.getInstance().getInstance().getRememberMe()) {
                        SharedPref.getInstance().setEmailAddress(Utilities.base64EncodeToString(email));
                        SharedPref.getInstance().setPassword(Utilities.base64EncodeToString(password));
                        SharedPref.getInstance().setSecretKey(Utilities.base64EncodeToString(secretKey));
                    }

                    BackendServicesUtility.initialiseNewAccountTransaction(accountId, publicKey);
                    ((StartActivity) getActivity()).startMainActivityFinishThisActivity();

                } else {
                    if (errorDetails.isEmpty())
                        Toast.makeText(getActivity(), "Error occurred registering alias. Please try again.", Toast.LENGTH_LONG).show();
                    else {
                        Toast.makeText(getActivity(), errorDetails, Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String str = "";
                try {
                    str = new String(responseBody, "UTF-8");

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
                try {
                    JSONObject jobject = new JSONObject(str);
                    int status = jobject.getInt("status");
                    if (status == 0) {

                    }
                } catch (JSONException ex) {
                    // TODO Auto-generated catch block
                    ex.printStackTrace();
                }
            }


        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("accountId",accountId);
        outState.putString("email",email);
        outState.putString("password",password);
        outState.putString("secretKey",secretKey);
        outState.putBoolean("rememberMeChecked", rememberMeChecked);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            if(savedInstanceState.containsKey("accountId")){
                accountId = savedInstanceState.getString("accountId");
            }
            if(savedInstanceState.containsKey("email")){
                email = savedInstanceState.getString("email");
            }
            if(savedInstanceState.containsKey("password")){
                password = savedInstanceState.getString("password");
            }
            if(savedInstanceState.containsKey("secretKey")){
                secretKey = savedInstanceState.getString("secretKey");
            }
            if(savedInstanceState.containsKey("rememberMeChecked")){
                rememberMeChecked = savedInstanceState.getBoolean("rememberMeChecked");
            }
        }
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Animator animator = super.onCreateAnimator(transit, enter, nextAnim);
        if (nextAnim != 0) {
            animator = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    create_alias_logo.setVisibility(View.INVISIBLE);
                    ((StartActivity) getActivity()).setDisableBackButton(true);
                    if (!enter) {
                        ((StartActivity) getActivity()).getLogo().setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (enter) {
                        create_alias_logo.setVisibility(View.VISIBLE);
                        ((StartActivity) getActivity()).getLogo().setVisibility(View.GONE);
                    }
                    ((StartActivity) getActivity()).setDisableBackButton(false);
                    ((StartActivity) getActivity()).setDisallowClicking(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else{
            if(enter)
                animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.empty);
            else
                animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_out_slide_out_from_left_to_right);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    create_alias_logo.setVisibility(View.INVISIBLE);
                    ((StartActivity) getActivity()).setDisableBackButton(true);
                    if (!enter) {
                        ((StartActivity) getActivity()).getLogo().setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (enter) {
                        create_alias_logo.setVisibility(View.VISIBLE);
                        ((StartActivity) getActivity()).getLogo().setVisibility(View.GONE);
                    }
                    ((StartActivity) getActivity()).setDisableBackButton(false);
                    ((StartActivity) getActivity()).setDisallowClicking(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return animator;
    }

}
