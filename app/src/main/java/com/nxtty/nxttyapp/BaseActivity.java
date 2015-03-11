package com.nxtty.nxttyapp;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;

import com.activeandroid.ActiveAndroid;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Raymond on 08/03/2015.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    public void init(){
        ActiveAndroid.initialize(this);
        Utilities.getInstance().setActivity(this);
        SharedPref.getInstance().setContext(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO).build();
        ImageLoader.getInstance().init(config);
    }

}
