package com.digitale.wowpaper;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.util.ByteArrayBuffer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Rich on 07/04/2016.
 */
public class ImageDownloader  {
    private static final String TAG ="IMAGEDOWNLOADER" ;

    public byte[] getLogoImage(String url) {
        byte[] photo = new byte[0];
        try {
            URL imageUrl = new URL(url);
            URLConnection ucon = imageUrl.openConnection();
            InputStream is = ucon.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(500);
            int current = 0;
              while ((current = bis.read()) != -1) {
                baf.append((byte) current);

            }
            photo = baf.toByteArray();
            Log.d(TAG,"photo length" + photo);
        } catch (Exception e) {
            Log.e(TAG, "Error: " + e.toString());
        }
        return photo;
    }
}

