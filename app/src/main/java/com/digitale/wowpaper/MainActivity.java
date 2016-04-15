package com.digitale.wowpaper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.design.widget.TabLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 */
public class MainActivity extends AppCompatActivity implements AsyncResponse {
    /**
     * Debug members
     */
    public static final boolean DEBUG = false;
    private boolean localDebug = false;
    private static final String TAG = "MAINACTIVITY";
    /**
     * API key required to query blizzard server
     */
    public static final String API = "5hpb65p224yrhe8bgrsf34hhna2bq6xr";
    /**
     * UI members
     */
    public static UI ui = new UI();
    private static SectionsPagerAdapter mSectionsPagerAdapter;
    public static String mWoWRegionID;
    public static String mCharacterName;
    public static int mPlatform = Logger.ANDROID;
    public ViewPager mViewPager;
    public static MainActivity mActivity;
    public static CharactersFragment mCharactersFragment;
    public static RealmListFragment realmListFragment;

    /**
     * Data members
     */
    public static Database mDatabase = new Database();
    public static ArrayList<Realm> mRealmList = new ArrayList<>();
    static String mRealmID;
    public static WoWDatabase PrefsDB;
    public static String[] testSubjects = {"Ameelia", "Amerith", "Mistprowler", "Neara", "Nefratiti", "Elventhongs",
            "Elvenhunt", "Canielia", "Bagone", "Presbyter", "Elvenhunt", "Grimshanks"};
    static ArrayList<WoWCharacter> mCharacters = new ArrayList<>();

    /**
     * Logic members
     */
    private Handler mRefreshHandler = new Handler();
    public static RealmAdapter mRealmAdapter;
    public static CharactersAdapter mCharactersAdapter;
    public static GalleryAdapter mGalleryAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init database helper
        PrefsDB = new WoWDatabase(this);

        prefsLoad();
        if (true) {
            launchSplash();
        }
        mRealmAdapter = new RealmAdapter(this, mRealmList);
        mCharactersAdapter = new CharactersAdapter(this, mCharacters);
        mGalleryAdapter = new GalleryAdapter(this, mCharacters);
        mActivity = this;
        ui.onActivityCreateSetTheme(this);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(final int i, final float v, final int i2) {
            }

            @Override
            public void onPageSelected(final int i) {
                FragmentNotifier fragment = (FragmentNotifier) mSectionsPagerAdapter.instantiateItem(mViewPager, i);
                if (fragment != null) {
                    fragment.fragmentVisible();
                }
            }

            @Override
            public void onPageScrollStateChanged(final int i) {
            }
        });

        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        if (mTabLayout != null) {
            mTabLayout.setupWithViewPager(mViewPager);
        }
        if (DEBUG) {
            Log.i(TAG, "INITIAL DATA LOAD");
        }
        refreshData();

        //  initRefreshHandler();
    }

    //setup and launch splash screen
    private void launchSplash() {
        Intent intent = new Intent(MainActivity.this, SplashActivity.class);
        intent.putExtra("SKIN", String.valueOf(ui.getSkinID()));
        MainActivity.this.startActivity(intent);
    }

    /**
     * create handler to refresh data every 10 seconds
     */
    private void initRefreshHandler() {
        //runnable to periodically update data and invalidate display
        Runnable runnable = new Runnable() {
            public void run() {
                refreshData();
                //repost runnable to execute again in the future
                mRefreshHandler.postDelayed(this, 10000);
            }

        };
        mRefreshHandler.postDelayed(runnable, 10000);
    }

    /**
     * get data and refresh UI
     */
    private void refreshData() {
        //check if realmlist has data
        GetFeedTask realmlistAsyncTask = new GetFeedTask(this, GetFeedTask.SQLREALMLIST);
        realmlistAsyncTask.delegate = this;
        realmlistAsyncTask.execute(GetFeedTask.SQLREALMLIST);

        GetFeedTask characterListAsyncTask = new GetFeedTask(this, GetFeedTask.SQLCHARACTERLIST);
        characterListAsyncTask.delegate = this;
        characterListAsyncTask.execute(GetFeedTask.SQLCHARACTERLIST);
    }

    @Override
    //this overrides the implemented method from asyncTask
    public void processFinish(int output, Object data) {
        Logger.writeLog(TAG, "Main Activity got notification feedtask done " + output, localDebug);
        //receive the result fired from async class
        //of onPostExecute(result) method.
        if (output==GetFeedTask.SQLREALMLIST) {
            mRealmList.clear();
            mRealmList.addAll((Collection<? extends Realm>) data);
            sortRealmlist();
            if(mRealmList.isEmpty()) {
                Logger.writeLog(TAG,"Realm query was empty, scanning web.",localDebug);
                //no realm data in database probably first run
                //so lets pull data from web
                GetFeedTask realmlistAsyncTask = new GetFeedTask(this, GetFeedTask.REALMLIST);
                //this to set delegate/listener back to this class
                realmlistAsyncTask.delegate = this;
                //execute getting the realmlist
                realmlistAsyncTask.execute(GetFeedTask.REALMLIST);
            }else{
                //there was data in the database update UI elements
              mRealmAdapter.notifyDataSetChanged();
            }

        } else if (output==GetFeedTask.REALMLIST) {
            //got realmlist data, update UI
            mRealmList.clear();
            mRealmList.addAll((Collection<? extends Realm>) data);
          sortRealmlist();
            mRealmAdapter.notifyDataSetChanged();
            mRealmID = mRealmList.get(0).getName();
            realmListFragment.setServerTextDisplay();
            prefsSave();
            //we had to get realmlist from web so now we need to put it in SQLite
            GetFeedTask setRealmlistAsyncTask = new GetFeedTask();
            setRealmlistAsyncTask.delegate = this;
            //execute setting the realmlist
            setRealmlistAsyncTask.execute(GetFeedTask.SETREALMLIST);

        }else if (output==GetFeedTask.CHARACTER) {
            //we have a character, get its images from web
            GetFeedTask characterImageAsyncTask = new GetFeedTask(this, GetFeedTask.CHARACTERIMAGE);
            characterImageAsyncTask.currentCharacter=mDatabase.getCharacter();
            characterImageAsyncTask.delegate=this;
            characterImageAsyncTask.execute(GetFeedTask.CHARACTERIMAGE);

        }else if (output==GetFeedTask.SQLCHARACTERLIST) {
            //update adapter datsource
            mCharacters.clear();
            mCharacters.addAll((Collection<? extends WoWCharacter>) data);
            //image data has changed, tell UI about it
            MainActivity.mCharactersAdapter.notifyDataSetChanged();
            MainActivity.mGalleryAdapter.notifyDataSetChanged();

        }else if (output==GetFeedTask.DELETERECORD) {
            GetFeedTask characterListAsyncTask = new GetFeedTask(GetFeedTask.SQLCHARACTERLIST);
            characterListAsyncTask.delegate = this;
            characterListAsyncTask.execute(GetFeedTask.SQLCHARACTERLIST);

        }else if (output==GetFeedTask.CHARACTERIMAGE) {
            //refresh character list data
            GetFeedTask characterListAsyncTask = new GetFeedTask(this, GetFeedTask.SQLCHARACTERLIST);
            //this to set delegate/listener back to this class
            characterListAsyncTask.delegate = this;
            characterListAsyncTask.execute(GetFeedTask.SQLCHARACTERLIST);

        }else if (output==GetFeedTask.FAVOURITEREALM) {
            //change to realmlist re-sort data and refreshUI
            sortRealmlist();
            MainActivity.mRealmAdapter.notifyDataSetChanged();

        }
    }
public void sortRealmlist(){
    Collections.sort(mRealmList, new Comparator<Realm>() {
        @Override
        public int compare(Realm lhs, Realm rhs) {
            //have to reverse value of flag for correct sorting
            int lhsFav=1-lhs.getFavourite();
            int rhsFav=1-rhs.getFavourite();
            String lhsCompositeString=lhsFav+lhs.getName();
            String rhsCompositeString=rhsFav+rhs.getName();
            Logger.writeLog(TAG,"COMPARE LHS="+lhsCompositeString+" RHS "+rhsCompositeString+
                    " result"+lhsCompositeString.compareTo(rhsCompositeString),localDebug);
            return lhsCompositeString.compareTo(rhsCompositeString);
        }
    });
}
    @Override
    public void onDestroy() {
        super.onDestroy();
        PrefsDB.close();
    }

    @Override

    public void onResume() {
        super.onResume();
        //  initRefreshHandler();
    }

    @Override
    public void onPause() {
        super.onPause();  // Always call the superclass method first
        mRefreshHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int lSkinID;
        switch (item.getItemId()) {
            case R.id.hordeSkin:
                lSkinID = UI.THEME_HORDE;
                break;
            case R.id.allianceSkin:
                lSkinID = UI.THEME_ALLIANCE;
                break;
            default:
                lSkinID = UI.THEME_DEFAULT;
        }
        ui.setSkin(item, lSkinID, this);
        prefsSave();
        return super.onOptionsItemSelected(item);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            Fragment fragment = null;
            switch (position) {
                case 0:
                    fragment = new RealmListFragment();
                    break;
                case 1:
                    fragment = new CharactersFragment();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment createdFragment = (Fragment) super.instantiateItem(container, position);
            // save the appropriate reference depending on position
            switch (position) {
                case 0:
                    realmListFragment = (RealmListFragment) createdFragment;
                    break;
                case 1:
                    mCharactersFragment = (CharactersFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Character\nSearch";
                case 1:
                    return "Character\nGallery";
            }
            return null;
        }
    }

    /**
     * Load Preferences
     */
    public void prefsLoad() {
        SharedPreferences lPrefs = getPreferences(MODE_PRIVATE);
        int lSkinID = lPrefs.getInt("mSkinID", 0);
        ui.setSkin(lSkinID, this);
        //set current regionID from prefs, or default to first region in stock DB
        mWoWRegionID = lPrefs.getString("mRegionID", MainActivity.PrefsDB.getCurrentRegionURL(0));
        //set current realmID from prefs, or default to null
        mRealmID = lPrefs.getString("mRealmID", "");
        if (DEBUG) Log.d(TAG, "LOADING PREFS SkinID " + lSkinID +
                ", Region " + mWoWRegionID +
                ", Realm" + mRealmID);
    }

    /**
     * Save Preferences
     */
    public void prefsSave() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mSkinID", ui.getSkinID());
        ed.putString("mRegionID", mWoWRegionID);
        ed.putString("mRealmID", mRealmID);
        ed.apply();
        Logger.writeLog(TAG, "SAVING PREFS SkinID " + ui.getSkinID() +
                ", Region " + mWoWRegionID +
                ", Realm" + mRealmID, localDebug);
    }

    public static Context getContext() {
        return mActivity.getApplicationContext();
    }

}
