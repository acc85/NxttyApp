package com.nxtty.nxttyapp.Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
public class RegisterFragment extends Fragment {

    private View registerLogo;
    private boolean registerPressed;
    private CheckBox showPasswordCheckBox, rememberMeCheckBox;
    private ProgressDialog progressDialog;

    private EditText registerEmailField, registerPasswordConfirmField, registerPasswordField, registerSecretKeyField;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View registerFragmentLayout = inflater.inflate(R.layout.register_layout, null);
        registerLogo = registerFragmentLayout.findViewById(R.id.register_logo);
        View registerButton = registerFragmentLayout.findViewById(R.id.register_button);

        registerEmailField = (EditText) registerFragmentLayout.findViewById(R.id.register_email_field);
        registerPasswordField = (EditText) registerFragmentLayout.findViewById(R.id.register_password_field);
        registerPasswordConfirmField = (EditText) registerFragmentLayout.findViewById(R.id.register_password_confirm_field);
        registerSecretKeyField = (EditText) registerFragmentLayout.findViewById(R.id.register_secret_key_field);

        showPasswordCheckBox = (CheckBox) registerFragmentLayout.findViewById(R.id.register_show_password_check_box);

        showPasswordCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    registerPasswordField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    registerPasswordConfirmField.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else {
                    registerPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    registerPasswordConfirmField.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        final CheckBox backupDetailsCheckBox = (CheckBox) registerFragmentLayout.findViewById(R.id.register_backup_details_check_box);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View v) {
                ((StartActivity) getActivity()).hideKeyboard();
                if(Utilities.getInstance().isNetworkConnected()){
                    if (fieldsFilled()) {
                        if (backupDetailsCheckBox.isChecked()) {
                            registerPressed = true;
                            animateFadeOutAndScaleDown();
                        }else{
                            BackendServicesUtility.loginOrRegisterAccount(getSecretPhrase(), createRegisterAsyncHttpResponseHandler());
                        }
                    }
                }
            }
        });

        InputFilter filter = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))) {
                        return "";
                    }
                }
                return null;
            }
        };
        registerSecretKeyField.setFilters(new InputFilter[]{filter});

        registerSecretKeyField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    v.clearFocus();
                    ((StartActivity) getActivity()).hideKeyboard();
                    return true;
                }
                return false;
            }
        });
        TextView loginButton = (TextView) registerFragmentLayout.findViewById(R.id.register_have_account_text);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((StartActivity) getActivity()).isDisallowClicking()) {
                    ((StartActivity) getActivity()).setDisallowClicking(true);
                    showLoginFragment();
                }
            }
        });

        rememberMeCheckBox = (CheckBox)registerFragmentLayout.findViewById(R.id.register_remember_me_check_box);

        return registerFragmentLayout;
    }



    public String getSecretPhrase(){
        return getEmailAddress()+"_"+getPassword()+"_"+getSecretKey();
    }

    public String getEmailAddress() {
        return registerEmailField.getText().toString();
    }

    public String getPassword() {
        return registerPasswordField.getText().toString();
    }

    public String getSecretKey() {
        return registerSecretKeyField.getText().toString();
    }

    public boolean fieldsFilled() {
        boolean fieldFilled = true;
        if (registerEmailField.getText().toString().isEmpty()) {
            registerEmailField.setError("Please enter a email address");
            fieldFilled = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(registerEmailField.getText().toString()).matches()) {
            registerEmailField.setError("Not a valid Email Address");
            fieldFilled = false;
        }
        if (registerPasswordField.getText().toString().isEmpty()) {
            registerPasswordField.setError("Please enter a password");
            fieldFilled = false;
        }
        if (registerPasswordConfirmField.getText().toString().isEmpty()) {
            registerPasswordConfirmField.setError("Please confirm password");
            fieldFilled = false;
        } else if (!registerPasswordConfirmField.getText().toString().equalsIgnoreCase(registerPasswordField.getText().toString())) {
            registerPasswordConfirmField.setError("Confirmation does not match password");
        }
        if (registerSecretKeyField.getText().toString().isEmpty()) {
            registerSecretKeyField.setError("Please enter a secret key");
            fieldFilled = false;
        }
        return fieldFilled;
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Animator animator = super.onCreateAnimator(transit, enter,nextAnim);
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
                        registerLogo.setVisibility(View.GONE);
                        ((StartActivity) getActivity()).setDisableBackButton(true);
                        if (!enter) {
                            ((StartActivity) getActivity()).getLogo().setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (enter) {
                            registerLogo.setVisibility(View.VISIBLE);
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
                if (((StartActivity) getActivity()).isFromMain()) {
                    ((StartActivity) getActivity()).setFromMain(false);
                } else {

                    if (!enter) {
                        animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_out_slide_out_from_left_to_right);
                    } else {
                        animator = AnimatorInflater.loadAnimator(getActivity(), R.animator.fade_in_scale_up);
                    }
                    animator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            registerLogo.setVisibility(View.GONE);
                            ((StartActivity) getActivity()).setDisableBackButton(true);
                            if (!enter) {
                                ((StartActivity) getActivity()).getLogo().setVisibility(View.VISIBLE);
                            }

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            if (enter) {
                                registerLogo.setVisibility(View.VISIBLE);
                                if (getActivity() != null) {
                                    ((StartActivity) getActivity()).getLogo().setVisibility(View.GONE);
                                    ((StartActivity) getActivity()).setDisableBackButton(false);
                                    ((StartActivity) getActivity()).setDisallowClicking(false);
                                }
                            }

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
        }

        return animator;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void animateFadeOutAndScaleDown() {
        getView().animate()
                .alpha(0.5f)
                .scaleX(0.7f)
                .scaleY(0.7f)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (registerPressed) {
                            registerPressed = false;
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


    public void animateFadeIndAndScaleUp() throws Exception {
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
    public void showExportDialogFragment() {
        ExportDetailsFragment exportDetailsFragment = new ExportDetailsFragment();
        String registerData = Utilities.getInstance().createStringFromRegisterData(getEmailAddress(), getPassword(), getSecretKey());
        exportDetailsFragment.setRegisterData(registerData);
        exportDetailsFragment.setEmailAddress(getEmailAddress());
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.slide_in_from_bottom_to_top, R.animator.slide_out_from_top_to_bottom, 0, R.animator.slide_out_from_top_to_bottom)
                .add(R.id.start_activity_container, exportDetailsFragment, Constants.EXPORTDETAILSFRAGMENT)
                .addToBackStack(Constants.EXPORTDETAILSFRAGMENT)
                .commit();
    }

    public void showLoginFragment() {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in_slide_in_from_right_to_left, R.animator.fade_out_scale_down, R.animator.fade_in_scale_up, R.animator.fade_out_slide_out_from_left_to_right)
                .replace(R.id.start_activity_container, new LoginFragment(), Constants.LOGINFRAGMENT)
                .addToBackStack(Constants.LOGINFRAGMENT)
                .commit();
    }

    public void showCreateAliasFragment(String accountId, String publicKey) {
        getFragmentManager().beginTransaction()
                .setCustomAnimations(R.animator.fade_in_slide_in_from_right_to_left, R.animator.fade_out_scale_down, R.animator.fade_in_scale_up, R.animator.fade_out_slide_out_from_left_to_right)
                .replace(R.id.start_activity_container, CreateAliasFragment.build(accountId,publicKey,getEmailAddress(),getPassword(),getSecretKey()), Constants.CREATEALIASFRAGMENT)
                .addToBackStack(Constants.CREATEALIASFRAGMENT)
                .commit();
    }


    public AsyncHttpResponseHandler createRegisterAsyncHttpResponseHandler(){
        return new AsyncHttpResponseHandler() {
            String keyValue,publicKey,str;

            @Override
            public void onStart() {
                super.onStart();
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setMessage("Registering");
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
                        BackendServicesUtility.checkSubscription(keyValue, publicKey, createCheckSubscriptionResponseHandler(keyValue,publicKey));
                    } else {
                        Toast.makeText(getActivity(), "You are not connected", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "An error occurred while registering. Please try again", Toast.LENGTH_LONG).show();
                    try {
                        animateFadeIndAndScaleUp();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String str = "";
                try{
                    str = new String(responseBody, "UTF-8");
                    JSONObject jobject = new JSONObject(str);
                    int status = jobject.getInt("status");
                    animateFadeIndAndScaleUp();
                    Toast.makeText(getActivity(), "An error occurred while registering. Please try again", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    progressDialog.cancel();
                    Toast.makeText(getActivity(),"Sorry but there seems to be a problem, please try again", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        };
    }

    public AsyncHttpResponseHandler createCheckSubscriptionResponseHandler(final String keyValue, final String publicKey){
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
                        showCreateAliasFragment(keyValue, publicKey);
                    } else {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Already Registered")
                                .setMessage("This account has already been registered. \nWould you like to Login?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SharedPref.getInstance().setRememberMe(rememberMeCheckBox.isChecked());
                                        if(SharedPref.getInstance().getRememberMe()){
                                            Profile.addRegisterDataToTable(
                                                    Utilities.getInstance().encryptDatabaseDataToString(getEmailAddress(), Utilities.getInstance().generateUUID()),
                                                    Utilities.getInstance().encryptDatabaseDataToString(getPassword(), Utilities.getInstance().generateUUID()),
                                                    Utilities.getInstance().encryptDatabaseDataToString(getSecretKey(), Utilities.getInstance().generateUUID()));
                                        }
                                        ((StartActivity) getActivity()).startMainActivityFinishThisActivity();
                                    }
                                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create()
                                .show();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.cancel();
                Toast.makeText(getActivity(),"Sorry but there seems to be a problem, please try again", Toast.LENGTH_LONG).show();
            }
        };
    }
}
