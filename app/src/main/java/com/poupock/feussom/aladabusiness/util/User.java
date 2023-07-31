package com.poupock.feussom.aladabusiness.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(tableName = "users")
public class User {

    public static final String MOBILE_PRINTER = "MOBILE_PRINTER";
    @Ignore
    private static final String tag = User.class.getSimpleName();
    public static final String LAST_SYNC_TIME = "LAST_SYNC";
    private static final String PRINTER_EPSON_MODEL_TARGET = "PRINTER_EPSON_MODEL_TARGET";
    private static final String PRINTER_OPTION = "PRINTER_OPTION";
    public static final String EPSON = "EPSON_PRINTER";
    public static final String I_POS_PRINTER = "IPOS_PRINTER";
    private static final String SALE_OPTION = "SALE_OPTION";

    public static final String SALE_DIRECT_MODE = "DIRECT_SALE_MODE";
    public static final String SALE_COURSE_MODE = "COURSE_SALE_MODE";
    private static final String PRINTER_BT_ADDRESS = "PRINTER_BLUETOOTH";

    @PrimaryKey(autoGenerate = true)
    int id;
    String role_id;
    String name;
    String identifier;
    String local_avatar;
    String server_avatar;
    private String pin;
    private String password;
    private String created_at;
    private String email;
    private String phone;
    @Ignore
    private List<Business> businesses;
    @Ignore
    private List<Business> owned_businesses;
    @Ignore
    List<Role> roles;
    @Ignore
    private static final String CONNECTED_USER = "CONNECTED_USER";
    private static final String USER_FCM = "Firebase-cloud-message-token";
    @Ignore
    private static final String USER_TOKEN = "TOKEN";
    @Ignore
    private static final String PROFILE_PATH = "PATH_PROFILE";
    @Ignore
    private UserPivot pivot;


    public static boolean storeConnectedUser(User user, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(CONNECTED_USER, new Gson().toJson(user));
        editor.apply();
        return editor.commit();
    }

    public static boolean storeToken(String token, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(USER_TOKEN, token);
        editor.apply();
        return editor.commit();
    }

    public static boolean storePath(String path, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(PROFILE_PATH, path);
        editor.apply();
        return editor.commit();
    }

    public static boolean storeLastSync(String time, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(LAST_SYNC_TIME, time);
        editor.apply();
        return editor.commit();
    }

    public static boolean disconnectConnectedUser(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(CONNECTED_USER,"");
        editor.apply();
        return editor.commit();
    }

//    public static User getRegisteredUser(Context context){
//        User user = null;
//        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name,Context.MODE_PRIVATE);
//        String userStoredString = preferences.getString(CONNECTED_USER,null);
//        try{ user = new Gson().fromJson(userStoredString, User.class); }
//        catch (JsonSyntaxException ex ) { Log.i(tag , "The User JSON exception is "+ex.toString());}
//        catch (NullPointerException ex) { Log.i(tag , "The User NULL exception is "+ex.toString());}
//
//        return user;
//    }
//

    @Ignore
    public User(int id) {
        this.id = id;
    }

    public User(int id, String role_id, String name, String identifier, String local_avatar, String server_avatar, String pin,
                String password, String created_at, String email, String phone) {
        this.id = id;
        this.role_id = role_id;
        this.name = name;
        this.identifier = identifier;
        this.local_avatar = local_avatar;
        this.server_avatar = server_avatar;
        this.pin = pin;
        this.password = password;
        this.email = email;
        this.created_at = created_at;
        this.phone = phone;
    }

    @Ignore
    public User(int id, String role_id, String name, String identifier, String local_avatar, String server_avatar,
                String pin, String password, List<Role> roles, String phone, String email) {
        this.id = id;
        this.role_id = role_id;
        this.name = name;
        this.identifier = identifier;
        this.local_avatar = local_avatar;
        this.server_avatar = server_avatar;
        this.pin = pin;
        this.password = password;
        this.roles = roles;
        this.email = email;
        this.phone = phone;
    }

    @Ignore
    public User(int id, String role_id, String name, String identifier, String local_avatar, String server_avatar,
                String pin, String password, List<Role> roles, String phone, String email, List<Business> businesses,
                List<Business> owned_businesses) {
        this.id = id;
        this.role_id = role_id;
        this.name = name;
        this.identifier = identifier;
        this.local_avatar = local_avatar;
        this.server_avatar = server_avatar;
        this.pin = pin;
        this.password = password;
        this.roles = roles;
        this.email = email;
        this.phone = phone;
        this.owned_businesses = owned_businesses;
        this.businesses = businesses;
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE);
        return preferences.getString(USER_TOKEN, null);
    }

    public static String getPath(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE);
        return preferences.getString(PROFILE_PATH, null);
    }

    public static String getLastSyncTime(Context context) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE);
        return preferences.getString(LAST_SYNC_TIME, sdf.format(new Date()));
    }

    public static String getPrinterModelTarget(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE);
        return preferences.getString(PRINTER_EPSON_MODEL_TARGET, null);
    }

    public static User currentUser(Context context) {
        User user = null;
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE);
        String userStoredString = preferences.getString(CONNECTED_USER, null);
        try {
            user = new Gson().fromJson(userStoredString, User.class);
        } catch (JsonSyntaxException ex) {
            Log.i(tag, "The User JSON exception is " + ex.toString());
        } catch (NullPointerException ex) {
            Log.i(tag, "The User NULL exception is " + ex.toString());
        }

        return user;
    }

    public static String  getPrinterOption(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE);
        return preferences.getString(PRINTER_OPTION, null);
    }

    public static String  getPrinterBtAddress(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE);
        return preferences.getString(PRINTER_BT_ADDRESS, null);
    }

    public static String  getSaleMode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE);
        return preferences.getString(SALE_OPTION, SALE_COURSE_MODE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRole_id() {
        return role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRole(List<Role> role) {
        this.roles = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getLocal_avatar() {
        return local_avatar;
    }

    public void setLocal_avatar(String local_avatar) {
        this.local_avatar = local_avatar;
    }

    public String getServer_avatar() {
        return server_avatar;
    }

    public void setServer_avatar(String server_avatar) {
        this.server_avatar = server_avatar;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Business> getBusinesses() {
        return businesses;
    }

    public void setBusinesses(List<Business> businesses) {
        this.businesses = businesses;
    }

    public List<Business> getOwned_businesses() {
        return owned_businesses;
    }

    public void setOwned_businesses(List<Business> owned_businesses) {
        this.owned_businesses = owned_businesses;
    }

    public UserPivot getPivot() {
        return pivot;
    }

    public void setPivot(UserPivot pivot) {
        this.pivot = pivot;
    }

    public static boolean storeFCMToken(String token, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(USER_FCM, token);
        editor.apply();
        return editor.commit();
    }

    public static boolean storePrinterModelTarget(String printerModelTarget, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(PRINTER_EPSON_MODEL_TARGET, printerModelTarget);
        editor.apply();
        return editor.commit();
    }

    public static boolean storeMobilePrinterAddress(String printerBTAddress, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(PRINTER_BT_ADDRESS, printerBTAddress);
        editor.apply();
        return editor.commit();
    }

    public static boolean storePrinterOption(String option, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(PRINTER_OPTION, option);
        editor.apply();
        return editor.commit();
    }

    public static boolean storeSaleMode(String option, Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(SALE_OPTION, option);
        editor.apply();
        return editor.commit();
    }

    public static List<User> buildListFromObjects(List<Object> data) {
        List<User> values = new ArrayList<>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                values.add(getObjectFromObject(data.get(i)));
            }
        }
        return values;
    }

    public static User getObjectFromObject(Object data) {
        Gson gson = new Gson();
        return gson.fromJson(gson.toJson(data), User.class);
    }

}