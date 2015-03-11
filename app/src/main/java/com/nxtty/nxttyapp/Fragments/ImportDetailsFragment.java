package com.nxtty.nxttyapp.Fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
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

import com.nxtty.nxttyapp.Constants;
import com.nxtty.nxttyapp.R;
import com.nxtty.nxttyapp.StartActivity;
import com.nxtty.nxttyapp.Utilities;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by Raymond on 17/02/2015.
 */
public class ImportDetailsFragment extends Fragment {

    private EditText digitOne,digitTwo,digitThree,digitFour;
    private Button importDetailsAcceptButton;
    private File file;
    boolean deleteActive;


    public static ImportDetailsFragment builder(File file){
        ImportDetailsFragment importDialogFragment = new ImportDetailsFragment();
        importDialogFragment.setFile(file);
        return importDialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View exportDetailsFragmentLayout = inflater.inflate(R.layout.code_dialog_layout,null);
        ((StartActivity)getActivity()).setStarting(false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        Button importDetailsCancelButton = (Button)exportDetailsFragmentLayout.findViewById(R.id.code_dialog_cancel_button);
        importDetailsAcceptButton = (Button)exportDetailsFragmentLayout.findViewById(R.id.code_dialog_accept_button);

        importDetailsCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity)getActivity()).hideKeyboard();
                getFragmentManager().popBackStack();
            }
        });

        importDetailsAcceptButton.setEnabled(false);
        importDetailsAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((StartActivity)getActivity()).hideKeyboard();
                LoginFragment loginFragment = (LoginFragment)getFragmentManager().findFragmentByTag(Constants.LOGINFRAGMENT);
                JSONObject jsonObject = Utilities.getInstance().decryptFileToJsonObject(file, getCode());
                if(jsonObject != null) {
                    if (loginFragment != null)
                        loginFragment.importDataIntoFields(jsonObject);
                    BackupFileListFragment backupFileListFragment = (BackupFileListFragment) getFragmentManager().findFragmentByTag(Constants.BACKUPFILELISTFRAGMENT);
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(R.animator.slide_out_from_top_to_bottom, R.animator.slide_out_from_top_to_bottom)
                            .remove(getFragment())
                            .remove(backupFileListFragment)
                            .commit();
                    getFragmentManager().popBackStackImmediate();
                    getFragmentManager().popBackStackImmediate();
                }
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
                    System.out.println("set to delete false");
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
                if(checkFieldsFilled()){
                    importDetailsAcceptButton.setEnabled(true);
                }
                return false;
            }
        });


        digitThree.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                EditText eView = (EditText) view;
                if (deleteActive) {
                    System.out.println("set to delete false");
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
        String filePath = file.getAbsolutePath();
        outState.putString("filePath", filePath);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        if(savedInstanceState != null && savedInstanceState.getString("filePath")!=null){
            file = new File(savedInstanceState.getString("filePath"));
        }
        super.onViewStateRestored(savedInstanceState);
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

    public void setAcceptButtonEnabled(boolean enable){
        if(enable){
            importDetailsAcceptButton.setEnabled(enable);
            importDetailsAcceptButton.setTextColor(getResources().getColor(R.color.red));
        }else{
            importDetailsAcceptButton.setEnabled(false);
            importDetailsAcceptButton.setTextColor(getResources().getColor(R.color.fadedRed));
        }
    }

    public void setFile(File file){
        this.file = file;
    }

    public String getCode(){
        return digitOne.getText().toString()+digitTwo.getText().toString()+digitThree.getText().toString()+digitFour.getText().toString();
    }

    @Override
    public Animator onCreateAnimator(int transit, final boolean enter, int nextAnim) {
        Animator animator = super.onCreateAnimator(transit, enter, nextAnim);
        if(nextAnim != 0){
                animator = AnimatorInflater.loadAnimator(getActivity(),nextAnim);
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if(!enter) {
                            BackupFileListFragment backupFileListFragment = (BackupFileListFragment) getFragmentManager().findFragmentByTag(Constants.BACKUPFILELISTFRAGMENT);
                            if (backupFileListFragment != null)
                                backupFileListFragment.backupListFragmentAnimateFadeIndAndScaleUp();
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //Required to make sure no accidental press of the same item in the listview twice, thus opening the fragment twice. Once fragment is loaded the
                        //previous fragment click facility is release.
                        BackupFileListFragment backupFileListFragment = (BackupFileListFragment) getFragmentManager().findFragmentByTag(Constants.BACKUPFILELISTFRAGMENT);
                        if (backupFileListFragment != null)
                            backupFileListFragment.setListItemPressed(false);
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
            if(enter)
                animator = AnimatorInflater.loadAnimator(getActivity(),R.animator.empty);
            else
                animator = AnimatorInflater.loadAnimator(getActivity(),R.animator.fade_out_slide_out_from_left_to_right);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if(!enter) {
                        BackupFileListFragment backupFileListFragment = (BackupFileListFragment) getFragmentManager().findFragmentByTag(Constants.BACKUPFILELISTFRAGMENT);
                        if (backupFileListFragment != null)
                            backupFileListFragment.backupListFragmentAnimateFadeIndAndScaleUp();
                    }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //Required to make sure no accidental press of the same item in the listview twice, thus opening the fragment twice. Once fragment is loaded the
                    //previous fragment click facility is release.
                    BackupFileListFragment backupFileListFragment = (BackupFileListFragment) getFragmentManager().findFragmentByTag(Constants.BACKUPFILELISTFRAGMENT);
                    if (backupFileListFragment != null)
                        backupFileListFragment.setListItemPressed(false);
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
