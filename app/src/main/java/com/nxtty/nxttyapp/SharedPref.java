package com.nxtty.nxttyapp;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Raymond on 16/02/2015.
 */
public class SharedPref extends Exception {

    private static Context context;
    private static SharedPreferences sharedPreferences;
    public static SharedPref instance = new SharedPref();

    public static SharedPref getInstance(){
        return instance;
    }

    public SharedPref(){
    }


    public void setContext(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(Constants.NXTTYAPPSHAREDPREFERENCES, context.MODE_ENABLE_WRITE_AHEAD_LOGGING);
    }

    public static String getNxtAccountId(){
        return sharedPreferences.getString(Constants.NXTACCOUNTID,"");
    }

    public static void setNxtAccountId(String nxtAccountId){
        sharedPreferences.edit().putString(Constants.NXTACCOUNTID,nxtAccountId).apply();
    }

    public static void setRememberMe(boolean bool){
        sharedPreferences.edit().putBoolean(Constants.REMEMBERME,bool).apply();
    }

    public static boolean getRememberMe(){
        return sharedPreferences.getBoolean(Constants.REMEMBERME,false);
    }

    public static void setEmailAddress(String emailAddress){
        sharedPreferences.edit().putString(Constants.LOGINEMAILADDRESS,emailAddress).apply();
    }

    public static String getEmailAddress(){
        return sharedPreferences.getString(Constants.LOGINEMAILADDRESS,"");
    }


    public static void setPassword(String password){
        sharedPreferences.edit().putString(Constants.LOGINPASSWORD,password).apply();
    }

    public static String getPassword(){
        return sharedPreferences.getString(Constants.LOGINPASSWORD,"");
    }


    public static void setSecretKey(String secretKey){
        sharedPreferences.edit().putString(Constants.LOGINSECRETKEY,secretKey).apply();
    }

    public static String getSecretKey(){
        return sharedPreferences.getString(Constants.LOGINSECRETKEY,"");
    }

    public static boolean isUpdatingProfile(){
        return sharedPreferences.getBoolean(Constants.UPDATINGPROFILE,false);
    }

    public static void setUpdatingProfile(boolean bool){
        sharedPreferences.edit().putBoolean(Constants.UPDATINGPROFILE,bool);
    }


    public static String getGcmRegisterId(){
        return sharedPreferences.getString(Constants.GCMREGISTERID,"");
    }

    public static void setGcmRegisterId(String id){
        sharedPreferences.edit().putString(Constants.GCMREGISTERID,id).apply();
    }
    public SharedPref(Throwable throwable) {
        super(throwable);
    }
}
