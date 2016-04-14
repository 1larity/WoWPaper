package com.digitale.wowpaper;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.test.ActivityInstrumentationTestCase2;
import android.test.ApplicationTestCase;
import android.test.ServiceTestCase;
import android.test.UiThreadTest;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import org.junit.Test;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.digitale.wowpaper.MainActivity.PrefsDB;
import static com.digitale.wowpaper.MainActivity.mCharacterName;
import static com.digitale.wowpaper.MainActivity.mDatabase;
import static com.digitale.wowpaper.MainActivity.mRealmAdapter;
import static com.digitale.wowpaper.MainActivity.mRealmID;
import static com.digitale.wowpaper.MainActivity.mWoWRegionID;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ActivityInstrumentationTestCase2<MainActivity> {
    public static WoWDatabase PrefsDB;
    public MainActivity activity;
    ArrayList<Realm> mRealmList =new ArrayList<>();
    public  ApplicationTest() {
        super(MainActivity.class);
        try {
            setUp();
            test_register_test();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
@UiThreadTest
    public void test_register_test() throws Exception {
        //TESTS IN HERE
    int a = 5;
    int b = 10;
    int result = a + b;
        GetFeedTask realmlistAsyncTask = new GetFeedTask(activity,GetFeedTask.REALMLIST);
        realmlistAsyncTask.execute(GetFeedTask.REALMLIST);
    assertEquals(15, result);
    }

    public void setUp() throws Exception{
        activity=new MainActivity();
        PrefsDB = new WoWDatabase(activity.getBaseContext());

    }
    /**
     * @return The {@link Context} of the test project.
     */
    private  Context getTestContext()
    {
        try
        {
            Method getTestContext = ServiceTestCase.class.getMethod("getTestContext");
            return (Context) getTestContext.invoke(this);
        }
        catch (final Exception exception)
        {
            exception.printStackTrace();
            return null;
        }
    }

}