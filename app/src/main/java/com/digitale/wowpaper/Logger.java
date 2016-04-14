package com.digitale.wowpaper;

import android.util.Log;

/**
 * Created by Rich on 11/04/2016.
 */
public class Logger {
    public static final int ANDROID = 1;
    public static final int JAVA = 2;
    public static final int FILE = 3;

    public static void writeLog(String TAG, String message, boolean localDebug) {
        if (MainActivity.DEBUG && localDebug) {
            switch (MainActivity.mPlatform) {
                case ANDROID:
                    Log.d(TAG, message);
                    break;
                case JAVA:
                    System.out.println(TAG + " " + message);
                    break;
                case FILE:
                    //TODO filewriter
                    break;
            }
        }
    }
    public static void error(String TAG, String message) {

            switch (MainActivity.mPlatform) {
                case ANDROID:
                    Log.e(TAG, message);
                    break;
                case JAVA:
                    System.out.println(TAG + " ERROR " + message);
                    break;
                case FILE:
                    //TODO filewriter
                    break;
            }

    }
}
