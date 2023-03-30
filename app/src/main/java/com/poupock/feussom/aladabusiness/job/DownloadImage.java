package com.poupock.feussom.aladabusiness.job;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.poupock.feussom.aladabusiness.util.Business;
import com.poupock.feussom.aladabusiness.util.User;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class DownloadImage extends AsyncTask<Void, Void, Void> {

    String tag = DownloadImage.class.getSimpleName();
    Context context;
    String url;

    public DownloadImage(Context context, String path){
        this.context = context;
        this.url = path;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            String path = saveToInternalStorage(Picasso.get().load(url).get());
            User.storePath(path, context);
            Log.i(tag, "The file path is "+path);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(tag, "The IO Exception : "+e.toString());
        }
        return null;
    }



    private String saveToInternalStorage(Bitmap bitmapImage){
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString() + "/alada-business/";
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();

        File file = new File(dir, "ALD_profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file.getAbsolutePath();
    }
}
