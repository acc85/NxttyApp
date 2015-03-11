package com.nxtty.nxttyapp.CustomViews;

import android.content.Context;
import android.net.Uri;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nxtty.nxttyapp.Constants;
import com.nxtty.nxttyapp.R;
import com.nxtty.nxttyapp.Utilities;
import com.nxtty.nxttyapp.models.Profile;

import java.io.File;

/**
 * Created by Raymond on 04/03/2015.
 */
public class ProfilePhotoPreference extends Preference {

    private View view;
    private String uri;

    public ProfilePhotoPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public ProfilePhotoPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProfilePhotoPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ProfilePhotoPreference(Context context) {
        super(context);
    }


    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        this.view = view;
        ImageView profileImage = (ImageView)view.findViewById(R.id.profilePhoto);
        Profile profile = Profile.getProfile();
        String avatarUrl = profile.profileImageUrl;
        ImageLoader.getInstance().displayImage(avatarUrl, profileImage, Utilities.getInstance().getCircularImageOptions());
    }

    @Override
    public void setLayoutResource(int layoutResId) {
        super.setLayoutResource(layoutResId);

    }

    public void notifyChanges(){
        notifyChanged();
    }

    public View getView(){
        return this.view;
    }

}
