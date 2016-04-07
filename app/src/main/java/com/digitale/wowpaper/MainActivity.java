package com.digitale.wowpaper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

/**
 *
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Debug members
     */
    public static final boolean DEBUG = true;
    private static final String TAG ="WOWDB" ;
    /**
     * API key required to query blizzard server
     */
    public static final String API ="5hpb65p224yrhe8bgrsf34hhna2bq6xr" ;
    /**
     * UI members
     */
    public static UI ui=new UI();
    private static SectionsPagerAdapter mSectionsPagerAdapter;
    public static String mWoWRegionID;
    public static String mCharacterName;
    public ViewPager mViewPager;
    public static MainActivity mActivity;
    public StandingsFragment mStandingsFragment;
    public RealmListFragment realmListFragment;
    public TeamFragment mTeamFragment;
    public String avatarPostfix = "-avatar.jpg";
    public String profilePostfix= "-profilemain.jpg";
    /**
     * Data members
     */
    public static Database mDatabase=new Database();
    ArrayList <Realm> mRealmList =new ArrayList<>();
    static String mRealmID;
    //data source team id
    static int mTeamID=66;
    //internal database id
    public static int mTeamIndex;
    ArrayList <Standing>mStandings=new ArrayList<>();
    public  ArrayList<CommentaryListItem> mCommentaryList = new ArrayList<>();
    public  ArrayList<Player> mPlayerList = new ArrayList<>();
    /**
     * Logic members
     */
    private Handler mRefreshHandler = new Handler();
    public static RealmAdapter mRealmAdapter;
    public static StandingsAdapter mStandingsAdapter;
    public static PlayerAdapter mPlayerAdapter;


    public static WoWDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init database helper
        db = new WoWDatabase(this);
       prefsLoad();

        //load player portraits
        mRealmAdapter = new RealmAdapter(this, mRealmList);
        mStandingsAdapter = new StandingsAdapter(this,mStandings);
        mPlayerAdapter =new PlayerAdapter(this, mPlayerList);
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
        if(DEBUG){
            Log.i(TAG,"INITIAL DATA LOAD");
        }
        refreshData();

      //  initRefreshHandler();
    }
    //setup and launch splash screen
    private void launchSplash() {
        Intent intent = new Intent(MainActivity.this,SplashActivity.class);
        intent.putExtra("SKIN",String.valueOf(ui.getSkinID()));
        MainActivity.this.startActivity(intent);
    }
     /**
     * create handler to refresh data every 10 seconds
     */
    private void initRefreshHandler(){
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
        GetFeedTask realmlistAsyncTask = new GetFeedTask(this,GetFeedTask.REALMLIST);
       realmlistAsyncTask.execute(GetFeedTask.REALMLIST);
        }
    @Override
    public void onDestroy() {
        super.onDestroy();
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
            case R.id.bournmouthSkin:
                lSkinID=UI.THEME_BOURNMOUTH;
                break;
            case R.id.evertonSkin:
                lSkinID=UI.THEME_EVERTON;
                break;
            default:
                lSkinID=UI.THEME_DEFAULT;
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
            switch(position) {
                case 0:
                    fragment= new RealmListFragment();
                    break;
                case 1:
                    fragment= new StandingsFragment();
                    break;
                case 2:
                    fragment= new TeamFragment();
                    break;
            }
            return fragment;
        }
        @Override
        public int getCount() {
            return 3;
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
                    mStandingsFragment = (StandingsFragment) createdFragment;
                    break;
                case 2:
                    mTeamFragment = (TeamFragment) createdFragment;
                    break;
            }
            return createdFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Leagues";
                case 1:
                    return "League Teams";
                case 2:
                    return "Team";
            }
            return null;
        }
    }
    /**
     * Load Preferences
     */
    public void prefsLoad( ) {
        SharedPreferences lPrefs = getPreferences(MODE_PRIVATE);
        int lSkinID = lPrefs.getInt("mSkinID", 0);
        ui.setSkin(lSkinID,this);
        //set current regionID from prefs, or default to first region in stock DB
        mWoWRegionID=lPrefs.getString("mRegionID",MainActivity.db.getCurrentRegionURL(0));
        //set current realmID from prefs, or default to null
        mRealmID=lPrefs.getString("mRealmID","");
        if (DEBUG) Log.d(TAG, "LOADING PREFS SkinID " + lSkinID+
                            ", Region "+mWoWRegionID+
                            ", Realm"+mRealmID);
    }
    /**
     * Save Preferences
     */
    public  void prefsSave() {
        SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = mPrefs.edit();
        ed.putInt("mSkinID", ui.getSkinID());
        ed.putString("mRegionID",mWoWRegionID);
        ed.putString("mRealmID",mRealmID);
        ed.apply();
        if (DEBUG) Log.d(TAG,"SAVING PREFS SkinID " +  ui.getSkinID()+
                ", Region "+mWoWRegionID+
                ", Realm"+mRealmID);
    }
    public static Context getContext(){
        return mActivity.getApplicationContext();
    }

}
