package com.nxtty.nxttyapp.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nxtty.nxttyapp.R;

/**
 * Created by Raymond on 17/02/2015.
 */
public class ExportDetailsDialogFragment extends DialogFragment {

    View dialogView;
    EditText digitOne;
    EditText digitTwo;
    EditText digitThree;
    EditText digitFour;
    boolean deleteActive;

    public void onStart() {
        System.out.println("dialog start");
        super.onStart();
        AlertDialog d = (AlertDialog) getDialog();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        if (d != null) {
            Button positiveButton = (Button) d.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    digitOne = (EditText) dialogView.findViewById(R.id.digitOne);
                    digitTwo = (EditText) dialogView.findViewById(R.id.digitTwo);
                    digitThree = (EditText) dialogView.findViewById(R.id.digitThree);
                    digitFour = (EditText) dialogView.findViewById(R.id.digitFour);

                    if (digitOne.getText().length() < 1 || digitTwo.getText().length() < 1 || digitThree.getText().length() < 1 || digitFour.getText().length() < 1) {
                        Toast warningToast = Toast.makeText(getActivity(), "The code must be 4 Digits", Toast.LENGTH_LONG);
                        warningToast.show();
                    } else {
                        String code = digitOne.getText().toString() + digitTwo.getText().toString() + digitThree.getText().toString() + digitFour.getText().toString();

                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(dialogView.getWindowToken(), 0);
                        dismiss();
                    }
                }
            });
        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        System.out.println("dialog create");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getActivity().getLayoutInflater().inflate(R.layout.export_code_dialog_layout, null);
        digitOne = (EditText) dialogView.findViewById(R.id.digitOne);
        digitTwo = (EditText) dialogView.findViewById(R.id.digitTwo);
        digitThree = (EditText) dialogView.findViewById(R.id.digitThree);
        digitFour = (EditText) dialogView.findViewById(R.id.digitFour);
        deleteActive = false;

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
            }
        });

        builder.setMessage("Please Create Export Code").setView(dialogView)
                .setPositiveButton("Export",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int d) {

                            }
                        })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
