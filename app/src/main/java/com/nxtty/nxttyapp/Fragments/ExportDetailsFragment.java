package com.nxtty.nxttyapp.Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.nxtty.nxttyapp.BackendServicesUtility;
import com.nxtty.nxttyapp.Constants;
import com.nxtty.nxttyapp.R;
import com.nxtty.nxttyapp.StartActivity;
import com.nxtty.nxttyapp.Utilities;

/**
 * Created by Raymond on 17/02/2015.
 */
public class ExportDetailsFragment extends Fragment {

    private EditText digitOne,digitTwo,digitThree,digitFour;
    private Button exportDetailsAcceptButton;
    private boolean deleteActive, registerButtonPressed;
    private String registerData;
    private String emailAddress;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View exportDetailsFragmentLayout = inflater.inflate(R.layout.code_dialog_layout,null);
        ((StartActivity)getActivity()).setStarting(false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Button exportDetailsCancelButton = (Button)exportDetailsFragmentLayout.findViewById(R.id.code_dialog_cancel_button);
        exportDetailsAcceptButton = (Button)exportDetailsFragmentLayout.findViewById(R.id.code_dialog_accept_button);

        exportDetailsAcceptButton.setEnabled(false);

        exportDetailsCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity)getActivity()).hideKeyboard();
//                getFragmentManager().beginTransaction()
//                        .setCustomAnimations(R.animator.slide_out_from_top_to_bottom, R.animator.slide_out_from_top_to_bottom)
//                        .remove(getFragment())
//                        .commit();
                getFragmentManager().popBackStack();
            }

        });

        exportDetailsAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity)getActivity()).hideKeyboard();
                registerButtonPressed = true;
                byte[] bytes = Utilities.getInstance().encryptBackupData(registerData,getCode());
                Utilities.getInstance().createBackupDataFile(bytes,emailAddress);
                getFragmentManager().popBackStack();
            }
        });

        digitOne = (EditText) exportDetailsFragmentLayout.findViewById(R.id.digitOne);
        digitTwo = (EditText) exportDetailsFragmentLayout.findViewById(R.id.digitTwo);
        digitThree = (EditText) exportDetailsFragmentLayout.findViewById(R.id.digitThree);
        digitFour = (EditText) exportDetailsFragmentLayout.findViewById(R.id.digitFour);




        digitOne.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                deleteActive = false;
                return false;
            }
        });

        digitTwo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                deleteActive = false;
                return false;
            }
        });

        digitThree.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                deleteActive = false;
                return false;
            }
        });

        digitFour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                deleteActive = false;
                return false;
            }
        });

        digitTwo.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                EditText eView = (EditText) view;
                if (deleteActive) {
                    deleteActive = false;
                    return true;
                } else if (i == 67) {
                    if (eView.getText().length() < 1) {
                        deleteActive = true;
                        digitOne.getText().clear();
                        digitOne.requestFocus();
                        return true;
                    } else {
                        digitTwo.getText().clear();
                        deleteActive = true;
                        return true;
                    }
                }
                return false;
            }
        });


        digitThree.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                EditText eView = (EditText) view;
                if (deleteActive) {
                    deleteActive = false;
                    return true;
                } else if (i == 67) {
                    if (eView.getText().length() < 1) {
                        deleteActive = true;
                        digitTwo.getText().clear();
                        digitTwo.requestFocus();
                        return true;
                    } else {
                        digitThree.getText().clear();
                        deleteActive = true;
                        return true;
                    }
                }
                return false;
            }
        });

        digitFour.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                EditText eView = (EditText) view;
                if (deleteActive) {
                    deleteActive = false;
                    return true;
                } else if (i == 67) {
                    if (eView.getText().length() < 1) {
                        deleteActive = true;
                        digitThree.getText().clear();
                        digitThree.requestFocus();
                        return true;
                    } else {
                        digitFour.getText().clear();
                        deleteActive = true;
                        return true;
                    }
                }
                return false;
            }

        });


        digitOne.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    digitTwo.requestFocus();
                }
                setAcceptButtonEnabled(checkFieldsFilled());
            }
        });


        digitTwo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    digitThree.requestFocus();
                }
                setAcceptButtonEnabled(checkFieldsFilled());
            }
        });

        digitThree.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() > 0) {
                    digitFour.requestFocus();
                }
                setAcceptButtonEnabled(checkFieldsFilled());
            }
        });

        digitFour.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setAcceptButtonEnabled(checkFieldsFilled());
            }
        });

        return exportDetailsFragmentLayout;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("registerData",registerData);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if(savedInstanceState!= null && savedInstanceState.getString("registerData")!=null){
           registerData = savedInstanceState.getString("registerData");
        }
        super.onViewStateRestored(savedInstanceState);
    }

    public void setAcceptButtonEnabled(boolean enable){
        if(enable){
            exportDetailsAcceptButton.setEnabled(enable);
            exportDetailsAcceptButton.setTextColor(getResources().getColor(R.color.red));
        }else{
            exportDetailsAcceptButton.setEnabled(false);
            exportDetailsAcceptButton.setTextColor(getResources().getColor(R.color.fadedRed));
        }
    }

    public boolean checkFieldsFilled(){
        if(digitOne.getText().toString().isEmpty() ||
                digitTwo.getText().toString().isEmpty() ||
                digitThree.getText().toString().isEmpty() ||
                digitFour.getText().toString().isEmpty()){
            return false;
        }else
            return true;

    }


    public String getCode(){
        return digitOne.getText().toString()+digitTwo.getText().toString()+digitThree.getText().toString()+digitFour.getText().toString();
    }


    public void setRegisterData(String registerData){
        this.registerData = registerData;
    }

    public void setEmailAddress(String emailAddress){
        this.emailAddress = emailAddress;
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Animator animator = super.onCreateAnimator(transit, enter, nextAnim);
        if(nextAnim != 0){
            animator = AnimatorInflater.loadAnimator(getActivity(),nextAnim);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(!enter){
                        RegisterFragment registerFragment = (RegisterFragment)getFragmentManager().findFragmentByTag(Constants.REGISTERFRAGMENT);
                        LoginFragment loginFragment = (LoginFragment)getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT);
                        if(registerFragment!=null)
                            try {
                                registerFragment.animateFadeIndAndScaleUp();
                                if(registerButtonPressed){
                                    registerButtonPressed = false;
                                    BackendServicesUtility.loginOrRegisterAccount(registerFragment.getSecretPhrase(),registerFragment.createRegisterAsyncHttpResponseHandler());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        if(loginFragment!= null) {
                            try {
                                loginFragment.loginAnimateFadeIndAndScaleUp();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    LoginFragment loginFragment = (LoginFragment) getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT);
                    if (loginFragment != null)
                        loginFragment.setExportButtonPressed(false);
                    if(enter) {
                        digitOne.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(digitOne, InputMethodManager.SHOW_IMPLICIT);
                    }

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }else{
            if(!enter){
                animator = AnimatorInflater.loadAnimator(getActivity(),R.animator.slide_out_from_top_to_bottom);
            }else{
                animator = AnimatorInflater.loadAnimator(getActivity(),R.animator.empty);
            }
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(!enter){
                        RegisterFragment registerFragment = (RegisterFragment)getFragmentManager().findFragmentByTag(Constants.REGISTERFRAGMENT);
                        LoginFragment loginFragment = (LoginFragment)getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT);
                        if(registerFragment!=null)
                            try {
                                registerFragment.animateFadeIndAndScaleUp();
                                if(registerButtonPressed){
                                    registerButtonPressed = false;
                                    BackendServicesUtility.loginOrRegisterAccount(registerFragment.getSecretPhrase(),registerFragment.createRegisterAsyncHttpResponseHandler());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        if(loginFragment!= null) {
                            try {
                                loginFragment.loginAnimateFadeIndAndScaleUp();
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                        }

                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    LoginFragment loginFragment = (LoginFragment) getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT);
                    if (loginFragment != null)
                        loginFragment.setExportButtonPressed(false);
                    if(enter) {
                        digitOne.requestFocus();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.showSoftInput(digitOne, InputMethodManager.SHOW_IMPLICIT);
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
        return animator;
    }

    public Fragment getFragment(){
        return this;
    }
}
