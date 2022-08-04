package com.poupock.feussom.aladabusiness.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "users")
public class User {

    @Ignore private static final String tag = User.class.getSimpleName();
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
    @Ignore private List<Business> businesses;
    @Ignore private List<Business> owned_businesses;
    @Ignore List<Role> roles;
    @Ignore private static final String CONNECTED_USER = "CONNECTED_USER";
    @Ignore private static final String USER_TOKEN = "TOKEN";

    public static boolean storeConnectedUser(User user , Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(CONNECTED_USER,new Gson().toJson(user));
        editor.apply();
        return editor.commit();
    }

    public static boolean storeToken(String token , Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(Constant._Preference_name, Context.MODE_PRIVATE).edit();
        editor.putString(USER_TOKEN,token);
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

    public static String getToken(Context context){
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name,Context.MODE_PRIVATE);
        return preferences.getString(USER_TOKEN,null);
    }

    public static User currentUser(Context context) {
        User user = null;
        SharedPreferences preferences = context.getSharedPreferences(Constant._Preference_name,Context.MODE_PRIVATE);
        String userStoredString = preferences.getString(CONNECTED_USER,null);
        try{ user = new Gson().fromJson(userStoredString, User.class); }
        catch (JsonSyntaxException ex ) { Log.i(tag , "The User JSON exception is "+ex.toString());}
        catch (NullPointerException ex) { Log.i(tag , "The User NULL exception is "+ex.toString());}

        return user;
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
}
