package com.nxtty.nxttyapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.view.View;

/**
 * Created by Raymond on 04/03/2015.
 */
public class ProfileActivity extends BaseActivity {

    private int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.settings_layout);

        super.onCreate(savedInstanceState);
    }


//    public void openCamera(View view){
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (intent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//        }
//    }
//
//   @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            mImageView.setImageBitmap(imageBitmap);
//        }
//    }
}
