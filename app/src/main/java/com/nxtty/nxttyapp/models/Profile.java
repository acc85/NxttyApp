package com.nxtty.nxttyapp.models;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;

import java.sql.Blob;

/**
 * Created by Raymond on 01/03/2015.
 */
@Table(name="Profile")
public class Profile extends Model {

    @Column(name="NxtAccountId", unique=true,onUniqueConflict = Column.ConflictAction.REPLACE )
    public String nxtAccountId;

    @Column(name="Email")
    public byte[] email;

    @Column(name="Password")
    public byte[] password;

    @Column(name="SecretKey")
    public byte[] secretKey;

    @Column(name="Name",unique = true,onUniqueConflict = Column.ConflictAction.REPLACE )
    public String name;

    @Column(name="RegistrationTimeStamp")
    public long registrationTimeStamp;

    @Column(name="City")
    public String city;

    @Column(name="School")
    public String school;

    @Column(name="Gender")
    public String gender;

    @Column(name="ProfileImageUrl")
    public String profileImageUrl;

    @Column(name="Status")
    public String status;

    @Column(name="DeleteMessageTime")
    public int deleteMessageTime;

    @Column(name="DeviceId")
    public String deviceId;

    public Profile(){
        super();
    }

    public Profile(String nxtAccountId, long registrationTimeStamp, String name, String city, String school, String gender, String profileImageUrl, String status, int deleteMessageTime) {
        super();
        this.nxtAccountId = nxtAccountId;
        this.registrationTimeStamp = registrationTimeStamp;
        this.name = name;
        this.city = city;
        this.school = school;
        this.gender = gender;
        this.profileImageUrl = profileImageUrl;
        this.status = status;
        this.deleteMessageTime = deleteMessageTime;
    }

    public Profile createProfileWithRegisterData(byte[] email, byte[] password, byte[] secretKey){
        this.nxtAccountId = "";
        this.email = email;
        this.password = password;
        this.secretKey = secretKey;
        this.registrationTimeStamp = 0L;
        this.name = "";
        this.city = "";
        this.school = "";
        this.gender = "";
        this.profileImageUrl = "";
        this.status = "";
        this.deleteMessageTime = 0;
        this.deviceId = "";
        return this;
    }

    public static Profile getUserByName(String name){
        return new Select()
                .from(Profile.class)
                .where("name = ?", name)
                .executeSingle();
    }

    public static Profile getUserByNxtAccountId(String nxtAccountId){
        return new Select()
                .from(Profile.class)
                .where("nxtAccountId = ?", nxtAccountId)
                .executeSingle();
    }

    public static int getCount(){
        return new Select()
                .from(Profile.class)
                .count();
    }

    public static void addRegisterDataToTable(byte[] email, byte[] password, byte[] secretKey){
        clearTable();
        Profile profile = new Profile();
        profile.createProfileWithRegisterData(email,password,secretKey);
        profile.save();
    }

    public static void clearTable(){
        new Delete().from(Profile.class).execute();
    }

    public static Profile getProfile(){
        return new Select()
                .from(Profile.class)
                .limit(1)
                .executeSingle();
    }


    public static Profile getCurrentProfile(){
        if(getCount() == 0){
            return new Profile();
        }else{
            return getProfile();
        }
    }

}
