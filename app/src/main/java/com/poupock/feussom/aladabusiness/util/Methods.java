package com.poupock.feussom.aladabusiness.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.poupock.feussom.aladabusiness.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Methods {

    private static final int REQUEST_STORAGE_PERMISSION_CODE = 0X5565;
    public static final int REQUEST_LOCATION_PERMISSION_CODE = 0x5560;
    private static final String TAG = Methods.class.getSimpleName();

    public static void hideKeyBoard(Activity activity){
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /**
     * helper to retrieve the path of an image URI
     */
    public static String getPath(Uri uri, Activity activity) {
        // just some safety built in
        if( uri == null ) {
            // TODO perform some logging or show user feedback
            return null;
        }
        // try to retrieve the image from the media store first
        // this will only work for images selected from gallery
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = activity.managedQuery(uri, projection, null, null, null);
        if( cursor != null ){
            int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String path = cursor.getString(column_index);
            cursor.close();
            return path;
        }
        // this is our fallback here
        return uri.getPath();
    }

    public static String saveImage(Bitmap bitmap, Context context) {
        String fullPath = null;
        String folderName = context.getString(R.string.app_name).replace(" ","_");
        try{
            if (android.os.Build.VERSION.SDK_INT >= 29) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName);
                values.put(MediaStore.Images.Media.IS_PENDING, true);
                // RELATIVE_PATH and IS_PENDING are introduced in API 29.

                Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    saveImageToStream(bitmap, context.getContentResolver().openOutputStream(uri));
                    values.put(MediaStore.Images.Media.IS_PENDING, false);
                    context.getContentResolver().update(uri, values, null, null);
                    return getRealPathFromURI(uri, context.getContentResolver());
                }

            } else {
                File directory = new File(Environment.getExternalStorageDirectory().toString() +"/" + folderName);
                // getExternalStorageDirectory is deprecated in API 29

                if (!directory.exists()) {
                    directory.mkdirs();
                }
                String fileName = "IMG_ALADA_" + System.currentTimeMillis() + ".png";
                File file = new File(directory, fileName);
                saveImageToStream(bitmap, new FileOutputStream(file));
                ContentValues values = setImageContentDetails();
                values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());
                // .DATA is deprecated in API 29
                context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                fullPath = file.getAbsolutePath();

            }
        }catch (FileNotFoundException ex){
            Log.i(TAG, "Unable to find the file : "+ex.toString());
        }catch (NullPointerException ex){
            Log.e(TAG, "A null object found : "+ex.toString());
        }

        return fullPath;
    }

    public static String getRealPathFromURI(Uri uri, ContentResolver resolver) {
        String path = "";
        if (resolver != null) {
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(idx);
                cursor.close();
            }
        }
        return path;
    }

    private static ContentValues  setImageContentDetails() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        return values;
    }

    private static void saveImageToStream(Bitmap bitmap, OutputStream outputStream) {
        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Uri storedImageAndGetUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage,
            "ALADA-Business/IMG_ALADA_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }


    public static boolean runtimeWritePermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity.getApplicationContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE_PERMISSION_CODE);
            return true;
        } else return false;
    }


    public static boolean runtimeLocationPermissions(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(activity.getApplicationContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION_CODE);
            return true;
        } else return false;
    }

    public static AlertDialog setProgressDialog(Context context) {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(context);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(context);
        tvText.setText(R.string.loading);
        tvText.setTextColor(Color.parseColor("#000000"));
        tvText.setTextSize(20);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setView(ll);

        AlertDialog dialog = builder.create();
        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setAttributes(layoutParams);
        }
        return dialog;
    }

    public static String getCurrentTimeStamp() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-DD hh:mm:ss");
        return (sdf.format(new Date()));
    }

    public static TextWatcher createTextWatcher(TextInputLayout inputLayout){
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    public static String generateCode() {
        return new Date().getTime()+"";
    }

    @NonNull
    public static List<MenuItem> filterMenuItems(List<MenuItem> menuItems, String text) {
        List<MenuItem> menuItemList = new ArrayList<>();
        if(menuItems != null && text != null){
            for (int i=0; i<menuItems.size(); i++){
                if(menuItems.get(i).getTitle().trim().toLowerCase().contains(text.toLowerCase()))
                    menuItemList.add(menuItems.get(i));
            }
        }
        return menuItemList;
    }

    public static int processStatus(int status) {
        if(status == Constant.STATUS_OPEN) return R.string.opened;
        else if(status == Constant.STATUS_CLOSED) return R.string.closed;
        else if(status == Constant.STATUS_SUCCESS) return R.string.success;
        else if(status == Constant.STATUS_ON_GOING) return R.string.on_going;
        else if(status == Constant.STATUS_PAYMENT_PENDING) return R.string.payment_pending;
        else  return R.string.unknown;
    }

    public static String formatTime(String created_at) {
        if (created_at != null){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MMM-dd hh:mm:ss");
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss yyyy-MMM-dd");
            try {
                return sdf1.format(Objects.requireNonNull(sdf.parse(created_at)));

            } catch (ParseException e) {
                e.printStackTrace();
                return "";
            }
        }
        else {
            return "";
        }

    }

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,15}$", Pattern.CASE_INSENSITIVE);

    public static boolean validate(String emailStr) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
}
