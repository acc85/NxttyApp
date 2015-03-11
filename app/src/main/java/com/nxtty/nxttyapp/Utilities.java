package com.nxtty.nxttyapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.TypedValue;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.process.BitmapProcessor;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Raymond on 16/02/2015.
 */
public class Utilities {

    private Activity activity;

    private static Utilities ourInstance = new Utilities();

    public static Utilities getInstance() {
        return ourInstance;
    }

    private Utilities() {
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    public boolean isNetworkConnected(){
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null){
            Toast.makeText(activity,"You are not connected. Please connect to continue",Toast.LENGTH_LONG).show();
            // There are no active networks.
            return false;
        }
        else
            return true;
    }

    public float convertDpToPx(int value){
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, activity.getResources().getDisplayMetrics());
    }

    public ArrayList<File> getBackupFileList(){
        ArrayList<File> fileList = new ArrayList<>();
        File[] files = activity.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()).listFiles();
        for(int f = 0; f < files.length;f++){
            File file = files[f];
            if(file.getName().contains(".nxtty"))
                fileList.add(files[f]);
        }
        return fileList;
    }


    public void createBackupDataFile(byte[] outputByte,String email) {
        FileOutputStream outputStream;
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(("dd-MM-yyyy-HH-mm-ss"));
        String date = df.format(cal.getTime());
        String filename = email+"_"+date+".nxtty";
        File file = new File(activity.getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()),filename);
        try {
            outputStream = new FileOutputStream(file);
            outputStream.write(outputByte);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String base64EncodeToString(String value){
       return Base64.encodeToString(value.getBytes(), 0);
    }


    public static String base64Encode(byte[] value){
        return Base64.encodeToString(value, 0);
    }

    public static String base64DecodeToString(String value){
        String result = "";
        try {
            result = new String(Base64.decode(value, 0), "US-ASCII");
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public static String base64DecodeToString(byte[] value){
        String result = "";
        try {
            result = new String(Base64.decode(value, 0), "US-ASCII");
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }

    public String createStringFromRegisterData(String email, String password, String secretKey){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(Constants.ISVALID, true);
            jsonObject.put(Constants.EMAIL, email);
            jsonObject.put(Constants.PASSWORD, password);
            jsonObject.put(Constants.SECRETKEY, secretKey);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    public String generateUUID(){
        final TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(activity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        return deviceUuid.toString();
    }

    public DisplayImageOptions getCircularImageOptions(){
        return new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .showImageOnLoading(roundedDrawable(R.drawable.profile_image_placeholder))
                .showImageForEmptyUri(roundedDrawable(R.drawable.profile_image_placeholder))
                .showImageOnFail(roundedDrawable(R.drawable.profile_image_placeholder))
                .preProcessor(new BitmapProcessor() {
                    @Override
                    public Bitmap process(Bitmap bitmap) {
                        return getCroppedBitmap(bitmap,100);
                    }
                })
                .build();
    }

    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius){
        Bitmap sbmp;
        if(bmp.getWidth() != radius || bmp.getHeight() != radius)
            sbmp = Bitmap.createScaledBitmap(bmp, radius, radius, false);
        else
            sbmp = bmp;
        Bitmap output = Bitmap.createBitmap(sbmp.getWidth(), sbmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect (0,0,sbmp.getWidth(), sbmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(android.graphics.Color.parseColor("#BAB399"));
        canvas.drawCircle(sbmp.getWidth() / 2 + 0.7f, sbmp.getHeight() / 2 + 0.1f, sbmp.getWidth() / 2 + 0.1f, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);
        canvas.rotate(90.0f);
        return output;
    }

    public static Bitmap resizeBitmap(Bitmap bmp){
        Bitmap sbmp;
        if(bmp.getWidth() != 100 || bmp.getHeight() != 100)
            sbmp = Bitmap.createScaledBitmap(bmp, 100, 100, false);
        else
            sbmp = bmp;
        bmp.recycle();
        return sbmp;
    }


    public Drawable roundedDrawable(int resource){
        BitmapDrawable bitmapDrawable = (BitmapDrawable)activity.getResources().getDrawable(resource);
        Bitmap bitmap = bitmapDrawable.getBitmap();
        Bitmap roundedBitmap = getCroppedBitmap(bitmap,100);
        Drawable drawable = new BitmapDrawable(roundedBitmap);
        return drawable;
    }

    public byte[] encryptData(String data, String code){
        byte[] keyCode = code.getBytes();
        byte [] outputByte = new byte[0];
        SecretKeySpec dKey = new SecretKeySpec(keyCode, "Blowfish");
        try {
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.ENCRYPT_MODE, dKey);
            outputByte = cipher.doFinal(data.getBytes());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
        return outputByte;
    }

    public byte[] encryptBackupData(String registerData, String code){
        return encryptData(registerData,code);
    }



    public String decryptDataToString(String data, String code){
        String decodedString = new String( decryptData(base64DecodeToString(data), code));
        return decodedString;
    }

    public byte[] encryptDatabaseDataToString(String data, String code){
        return encryptData(data, code);
    }

    public String decryptDatabaseDataToString(byte[] data, String code){
        String decodedString = "";
        try {
            decodedString = new String(decryptData(data, code));
        }catch(Exception e){
            e.printStackTrace();
        }
        return decodedString;
    }

    public String decryptDataFromBase64ToString(String data, String code){
        return base64DecodeToString(decryptData(data,code));
    }

    public byte[] decryptData(String data, String code){
        byte [] inputByte = new byte[]{};
        try {
            byte[] mybytes = data.getBytes();
            System.out.println("mybytes are:"+mybytes);
            byte[] keyCode = code.getBytes();
            SecretKeySpec deKey = new SecretKeySpec(keyCode, "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, deKey);
            inputByte = cipher.doFinal(mybytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
            Toast.makeText(activity,"Incorrect Code",Toast.LENGTH_LONG).show();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
            Toast.makeText(activity,"Incorrect Code",Toast.LENGTH_LONG).show();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
            Toast.makeText(activity,"Incorrect Code",Toast.LENGTH_LONG).show();
        }
        return inputByte;
    }

    public byte[] decryptData(byte[] data, String code){
        byte [] inputByte = new byte[]{};
        try {
            byte[] mybytes = data;
            byte[] keyCode = code.getBytes();
            SecretKeySpec deKey = new SecretKeySpec(keyCode, "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, deKey);
            inputByte = cipher.doFinal(mybytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
            Toast.makeText(activity,"Incorrect Code",Toast.LENGTH_LONG).show();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
            Toast.makeText(activity,"Incorrect Code",Toast.LENGTH_LONG).show();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
            Toast.makeText(activity,"Incorrect Code",Toast.LENGTH_LONG).show();
        }
        return inputByte;
    }


    public String decryptDataToString(byte[] data, String code){
        String decodedString = "";
        try {
            byte[] keyCode = code.getBytes();
            SecretKeySpec deKey = new SecretKeySpec(keyCode, "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish");
            cipher.init(Cipher.DECRYPT_MODE, deKey);
            byte [] inputByte = cipher.doFinal(data);
            decodedString = new String(inputByte);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
        } catch (BadPaddingException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
            Toast.makeText(activity,"Incorrect Code",Toast.LENGTH_LONG).show();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
            Toast.makeText(activity,"Incorrect Code",Toast.LENGTH_LONG).show();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            System.out.println("error:"+e.getMessage());
            Toast.makeText(activity,"Incorrect Code",Toast.LENGTH_LONG).show();
        }
        return decodedString;
    }

    public JSONObject decryptFileToJsonObject(File file,String code){
        JSONObject jsonObject = null;
        try {
            FileInputStream FIS = new FileInputStream(file);
            byte[] mybytes = new byte[(int)file.length()];
            FIS.read(mybytes);
            FIS.close();
            String decodedString  = decryptDataToString(mybytes,code);
            jsonObject = new JSONObject(decodedString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("file error");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("input output error");
            Toast.makeText(activity,"File does not exist",Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
