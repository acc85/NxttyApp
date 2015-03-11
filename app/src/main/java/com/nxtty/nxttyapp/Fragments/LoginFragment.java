package com.nxtty.nxttyapp.Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
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
import com.nxtty.nxttyapp.models.Profile;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

/**
 * Created by Raymond on 16/02/2015.
 */
public class LoginFragment extends Fragment {

    private View loginLogo;
    private boolean importPressed,exportPressed;
    private CheckBox showPasswordCheckBox, rememberMeCheckBox;
    private ProgressDialog progressDialog;

    private EditText loginEmailField,loginPasswordField,loginSecretKeyField;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View loginFragmentLayout = inflater.inflate(R.layout.login_layout,null);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        loginLogo = loginFragmentLayout.findViewById(R.id.login_logo);
        loginFragmentLayout.findViewById(R.id.login_do_not_have_account_text).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!((StartActivity)getActivity()).isDisallowClicking()) {
                    ((StartActivity) getActivity()).setDisallowClicking(true);
                    showRegisterFragment();
                }
            }
        });

        showPasswordCheckBox = (CheckBox)loginFragmentLayout.findViewById(R.id.login_show_password_check_box);

        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    loginPasswordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                else
                    loginPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        });

        loginFragmentLayout.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fieldsFilled()){
                    ((StartActivity)getActivity()).hideKeyboard();
                    if(Utilities.getInstance().isNetworkConnected()){
                        BackendServicesUtility.loginOrRegisterAccount(getSecretPhrase(), createLoginResponseHandler());
                    }
                }
            }
        });

        loginFragmentLayout.findViewById(R.id.login_import_details_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity)getActivity()).hideKeyboard();
                if(!importPressed) {
                    importPressed = true;
                    if (Utilities.getInstance().getBackupFileList().size() > 0)
                        animateAndShowBackupFileListFragment();
                    else{
                        Toast.makeText(getActivity(),R.string.no_backup_data_text, Toast.LENGTH_LONG).show();
                        importPressed = false;
                    }
                }
            }
        });

        loginFragmentLayout.findViewById(R.id.login_export_details_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity)getActivity()).hideKeyboard();
                if(fieldsFilled()) {
                    if (!exportPressed) {
                        exportPressed = true;
                        exportAnimateFadeOutAndScaleDown();
                    }
                }
            }
        });

        loginEmailField = (EditText)loginFragmentLayout.findViewById(R.id.login_email_field);
        loginPasswordField = (EditText)loginFragmentLayout.findViewById(R.id.login_password_field);
        loginSecretKeyField = (EditText)loginFragmentLayout.findViewById(R.id.login_secret_key_field);

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end,Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };



        loginSecretKeyField.setFilters(new InputFilter[] { filter });

        loginSecretKeyField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    v.clearFocus();
                    ((StartActivity)getActivity()).hideKeyboard();
                    return true;
                }
                return false;
            }
        });

        rememberMeCheckBox = (CheckBox)loginFragmentLayout.findViewById(R.id.login_remember_me_check_box);
        return loginFragmentLayout;
    }


    public String getSecretPhrase(){
        return getEmailAddress()+"_"+getPassword()+"_"+getSecretKey();
    }


    public void clearFieldErrors(){
        loginEmailField.setError(null);
        loginPasswordField.setError(null);
        loginSecretKeyField.setError(null);
    }

    public void importDataIntoFields(JSONObject jsonObject){
        try {
            loginEmailField.setText(jsonObject.getString(Constants.EMAIL));
            loginPasswordField.setText(jsonObject.getString(Constants.PASSWORD));
            loginSecretKeyField.setText(jsonObject.getString(Constants.SECRETKEY));
            clearFieldErrors();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getEmailAddress(){
        return loginEmailField.getText().toString();
    }

    public String getPassword(){
        return loginPasswordField.getText().toString();
    }

    public String getSecretKey(){
        return loginSecretKeyField.getText().toString();
    }


    public boolean fieldsFilled(){
        boolean fieldFilled = true;
        if(loginEmailField.getText().toString().isEmpty()){
            loginEmailField.setError("Please enter your email address");
            fieldFilled = false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(loginEmailField.getText().toString()).matches()){
            loginEmailField.setError("Not a valid Email Address");
            fieldFilled = false;
        }
        if(loginPasswordField.getText().toString().isEmpty()){
            loginPasswordField.setError("Please enter your password");
            fieldFilled = false;
        }
        if(loginSecretKeyField.getText().toString().isEmpty()){
            loginSecretKeyField.setError("Please enter a secret key");
            fieldFilled = false;
        }
        return fieldFilled;
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Animator animator = super.onCreateAnimator(transit,  enter, nextAnim);
        if(((StartActivity)getActivity()).isStarting()) {
            ((StartActivity)getActivity()).setStarting(false);
            animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.empty);
        }
        else {
            if (nextAnim != 0) {
                animator = AnimatorInflater.loadAnimator(getActivity(), nextAnim);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loginLogo.setVisibility(View.GONE);
                        ((StartActivity) getActivity()).setDisableBackButton(true);
                        if (!enter) {
                            ((StartActivity) getActivity()).getLogo().setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (enter) {
                            loginLogo.setVisibility(View.VISIBLE);
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
            } else {
                if (!enter) {
                    animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_out_slide_out_from_left_to_right);
                } else {
                    animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_in_scale_up);
                }
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        loginLogo.setVisibility(View.GONE);
                        ((StartActivity) getActivity()).setDisableBackButton(true);
                        if (!enter) {
                            ((StartActivity) getActivity()).getLogo().setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (enter) {
                            loginLogo.setVisibility(View.VISIBLE);
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
        }
        return animator;
    }


    public void showBackupFileListFragment(){
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_bottom_to_top, R.animator.slide_out_from_top_to_bottom,R.animator.slide_in_from_left_to_right,R.animator.slide_out_from_top_to_bottom)
                .add(R.id.start_activity_container, new BackupFileListFragment(),Constants.BACKUPFILELISTFRAGMENT)
                .addToBackStack(Constants.BACKUPFILELISTFRAGMENT)
                .commit();
    }

    public void showRegisterFragment(){
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in_slide_in_from_right_to_left, R.animator.fade_out_scale_down, R.animator.fade_in_scale_up, R.animator.fade_out_slide_out_from_left_to_right)
                .replace(R.id.start_activity_container, new RegisterFragment(), Constants.REGISTERFRAGMENT)
                .addToBackStack(Constants.REGISTERFRAGMENT)
                .commit();
    }


    public AsyncHttpResponseHandler createLoginResponseHandler(){
        return new AsyncHttpResponseHandler() {
            String keyValue,publicKey,str;

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Logging in");
                progressDialog.show();
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
                        BackendServicesUtility.checkSubscription(keyValue, publicKey, createcCheckSubscriptionResponseHandler(keyValue,publicKey));
                    } else {
                        Toast.makeText(getActivity(), "You are not connected", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "An error occurred while registering. Please try again", Toast.LENGTH_LONG).show();
                    try {
                        loginAnimateFadeIndAndScaleUp();
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
                    JSONObject jobject = new JSONObject(str);
                    int status = jobject.getInt("status");
                    if (status == 0) {
                    }
                    loginAnimateFadeIndAndScaleUp();
                    Toast.makeText(getActivity(), "An error occurred while registering. Please try again", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public AsyncHttpResponseHandler createcCheckSubscriptionResponseHandler(final String keyValue, final String publicKey){
        return new AsyncHttpResponseHandler() {

            @Override
            public void onFinish() {
                super.onFinish();
                progressDialog.dismiss();
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
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Account is not registered")
                                .setMessage("This account has not yet registered. \nWould you like to Register?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showCreateAliasFragment(keyValue,publicKey);
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
                    } else {
                        SharedPref.getInstance().setNxtAccountId(keyValue);
                        SharedPref.getInstance().setRememberMe(rememberMeCheckBox.isChecked());
                        if(SharedPref.getInstance().getRememberMe()){
                            System.out.println("uuid:"+Utilities.getInstance().generateUUID());
                            Profile.addRegisterDataToTable(
                                    Utilities.getInstance().encryptDatabaseDataToString(getEmailAddress(), Utilities.getInstance().generateUUID()),
                                    Utilities.getInstance().encryptDatabaseDataToString(getPassword(), Utilities.getInstance().generateUUID()),
                                    Utilities.getInstance().encryptDatabaseDataToString(getSecretKey(), Utilities.getInstance().generateUUID()));
                        }
                        ((StartActivity)getActivity()).startMainActivityFinishThisActivity();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        };
    }


    public void showCreateAliasFragment(String accountId, String publicKey) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in_slide_in_from_right_to_left, R.animator.fade_out_scale_down, R.animator.fade_in_scale_up, R.animator.fade_out_slide_out_from_left_to_right)
                .replace(R.id.start_activity_container, CreateAliasFragment.build(accountId,publicKey,getEmailAddress(),getPassword(),getSecretKey()), Constants.CREATEALIASFRAGMENT)
                .addToBackStack(Constants.CREATEALIASFRAGMENT)
                .commit();
    }

    public void loginAnimateFadeIndAndScaleUp() throws Exception{
        getView().setAlpha(0.5f);
        getView().setScaleX(0.7f);
        getView().setScaleY(0.7f);

        getView().animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setStartDelay(150)
                .start();

    }

    /**
     * Set Import Button to true or false. This is to prevent the fragment from running method to display BackupFileListFragment since view is given an animator listener,
     * the listener is permanent.
     *
     * @param bool
     */
    public void setImportButtonPressed(boolean bool){
        importPressed = bool;
    }

    public void setExportButtonPressed(boolean bool){
        exportPressed = bool;
    }
    public void exportAnimateFadeOutAndScaleDown(){
        getView().animate()
                .alpha(0.5f)
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (exportPressed) {
                            showExportDialogFragment();
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {

                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }


    public void showExportDialogFragment(){
        ExportDetailsFragment exportDetailsFragment = new ExportDetailsFragment();
        String registerData = Utilities.getInstance().createStringFromRegisterData(getEmailAddress(),getPassword(),getSecretKey());
        exportDetailsFragment.setRegisterData(registerData);
        exportDetailsFragment.setEmailAddress(getEmailAddress());
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_bottom_to_top, R.animator.slide_out_from_top_to_bottom,0, R.animator.slide_out_from_top_to_bottom)
                .add(R.id.start_activity_container, exportDetailsFragment, Constants.EXPORTDETAILSFRAGMENT)
                .addToBackStack(Constants.EXPORTDETAILSFRAGMENT)
                .commit();
    }

    /**
     * Animate Fragment to scale down fade out. After animation is done run method to display BackupFileListFragment
     */
    public void animateAndShowBackupFileListFragment(){
        getView().animate()
            .alpha(0.5f)
            .scaleX(0.7f)
            .scaleY(0.7f)
            .setInterpolator(new AccelerateDecelerateInterpolator())
            .setListener(new Animator.AnimatorListener() {
                     @Override
                     public void onAnimationStart(Animator animation) {
                         if (importPressed) {
                             showBackupFileListFragment();
                         }
                     }

                     @Override
                     public void onAnimationEnd(Animator animation) {

                     }

                     @Override
                     public void onAnimationCancel(Animator animation) {

                     }

                     @Override
                     public void onAnimationRepeat(Animator animation) {

                     }
                 }
            ).start();
    }
}
