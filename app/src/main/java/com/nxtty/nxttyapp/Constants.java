package com.nxtty.nxttyapp;

/**
 * Created by Raymond on 14/02/2015.
 */
public class Constants {

    public static String SPLASHSCREENFRAGMENT = "splash_screen_fragment";
    public static String LOGINFRAGMENT = "login_fragment";
    public static String EXPORTDETAILSFRAGMENT = "export_details_fragment";
    public static String IMPORTDETAILSFRAGMENT = "import_details_fragment";
    public static String BACKUPFILELISTFRAGMENT = "backup_file_list_fragment";
    public static String EXPORTDETAILSDIALOGFRAGMENT = "export_details_dialog_fragment";
    public static String IMPORTDETAILSDIALOGFRAGMENT = "import_details_dialog_fragment";
    public static String REGISTERFRAGMENT = "register_fragment";
    public static String CREATEALIASFRAGMENT = "create_alias_fragment";
    public static String SETTINGFRAGMENT = "settings_fragment";
    public static String PROFILEFRAGMENT = "profile_fragment";


    //Shared Preferences Constants
    public static String NXTTYAPPSHAREDPREFERENCES = "nxtty_App_SharedPreferences";
    public static String SECRETPASSPHRASE = "secretPassPhrase";
    public static String NXTACCOUNTID = "nxt_account_id";
    public static String REMEMBERME = "remember_me";
    public static String LOGINEMAILADDRESS = "login_email_address";
    public static String LOGINPASSWORD = "login_password";
    public static String LOGINSECRETKEY = "login_secret_key";
    public static String UPDATINGPROFILE = "updating_profile";
    public static String GCMREGISTERID = "gcm_register_id";

    //Encrypted Data JsonKeys
    public static String EMAIL = "email";
    public static String PASSWORD = "password";
    public static String SECRETKEY = "secret_key";
    public static String ISVALID = "is_valid";

    //BackEnd Service Parameters
    public static String REQUESTTYPE="requestType";
    public static String GETACCOUNTID="getAccountId";
    public static String SECRETPHRASE = "secretPhrase";
    public static String KEY = "key";

    public static String ParamKey = "12qwUA8waM6oKYyxzVyGLUjtv";

    public static final String gcm_sender_id = "1081324162538";

    /** Authentication URL */
    public static String baseUrl = "https://nxt2.nxtty.com:7876/nxt";
    public static String TipUrl = "http://128.199.211.69/1nxttycoinsender.php";

    /**Global Chat URL**/
    public static String nxtCoinURL = "http://128.199.189.226/nxt.php";

    /**Image Server**/
    public static String imageServer = "https://nxtopen.s3.amazonaws.com/";




    /** Private Messaging server **/
    public static String private_messaging_base_url = "http://188.226.245.191:8080/nxt/";
    public static String subscriberUrl = private_messaging_base_url+"subscriber/";
    public static String getUserDetailsUrl = subscriberUrl+"get_subscriber";
    public static String private_update_user_details_simple = private_messaging_base_url+"subscriber/update_subscriber_settings_short";
    public static String private_update_user_details = private_messaging_base_url+"subscriber/update_subscriber_settings";


    /** Open Chat server **/
    public static String open_chat_base_url = "http://128.199.248.197:8080/nxt/";
    public static String update_user_details = open_chat_base_url+"subscriber/update_subscriber_settings";
    public static String update_user_details_simple = open_chat_base_url+"subscriber/update_subscriber_settings_short";

    /** Open Chat Images server **/
    public static String open_chat_images_url =  "https://nxtopen.s3.amazonaws.com/";

    /** Private Messaging Images Server **/
    public static String private_messaging_image_base_url = "https://nxtmessaging.s3.amazonaws.com/";

    /** NxtId to UserName Link Url **/
    public static String link_nxtId_to_username_base_url = "http://188.226.245.191:8080/nxt/subscriber/";

}
