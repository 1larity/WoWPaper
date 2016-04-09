package com.digitale.wowpaper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * UI control Class
 * Created by Rich on 12/03/2016.
 */
public class UI {
    //current skin identifier
    private int mSkinID=THEME_DEFAULT;
    public final static int THEME_DEFAULT = 0;
    public final static int THEME_HORDE = 1;
    public final static int THEME_ALLIANCE = 2;
    public  void setSkin(MenuItem item,int themeID, AppCompatActivity activity) {
        Toast.makeText(activity.getApplicationContext(), item + " skin Selected", Toast.LENGTH_LONG).show();
        mSkinID = themeID;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
    }
    public  void setSkin(int themeID, MainActivity mainActivity) {
        mSkinID = themeID;
    }

    /**
     * Set up UI elements
     * @param activity the calling activity
     */
    public  void onActivityCreateSetTheme(AppCompatActivity activity) {
        activity.setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        setTitleBarStyle(activity, toolbar);
        TabLayout mTabLayout = (TabLayout) activity.findViewById(R.id.tabs);
        setTabsTextColours(activity, mTabLayout);
        setTabsBackground(mTabLayout);
        }

        private void setTitleBarStyle(AppCompatActivity activity, Toolbar toolbar) {
        if (toolbar != null) {
            switch (this.mSkinID) {
                case THEME_DEFAULT:
                    activity.setTheme(R.style.AppTheme);
                    toolbar.setLogo(R.mipmap.ic_launcher);
                    break;
                case THEME_HORDE:
                    activity.setTheme(R.style.hordeTheme);
                    toolbar.setLogo(R.drawable.horde_icon);
                    toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.horde_bg_dark));
                    MainActivity.mTextColour=(ContextCompat.getColor(activity, R.color.horde_fg_light));
                    break;
                case THEME_ALLIANCE:
                    activity.setTheme(R.style.allianceTheme);
                    toolbar.setLogo(R.drawable.alliance_icon);
                    toolbar.setBackgroundColor(ContextCompat.getColor(activity, R.color.alliance_bg_dark));
                    MainActivity.mTextColour=(ContextCompat.getColor(activity, R.color.alliance_fg_light));
                    break;
            }

        }
    }
    /**
     * Set up colour of tabs according to skin selection
     * @param tabLayout the tab layout to apply to
     */
    private void setTabsBackground(TabLayout tabLayout) {
        for (int i = 0; i < tabLayout.getChildCount(); i++) {
            View v = tabLayout.getChildAt(i);
            switch (this.mSkinID){
                case UI.THEME_HORDE:
                    v.setBackgroundResource(R.drawable.tabs_horde);
                    break;
                case UI.THEME_ALLIANCE:
                    v.setBackgroundResource(R.drawable.tabs_alliance);
            }
        }
    }

    /**
     * Set up colour of tab text according to skin selection
     * @param activity the calling activity
     * @param mTabLayout the tablayout to apply to
     */
    private void setTabsTextColours(AppCompatActivity activity, TabLayout mTabLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (mTabLayout != null) {
                if(this.getSkinID()== UI.THEME_HORDE) {
                    mTabLayout.setTabTextColors(activity.getResources().getColorStateList(R.color.bournmouth_tab_selector, null));
                }else{
                    mTabLayout.setTabTextColors(activity.getResources().getColorStateList(R.color.tab_selector, null));
                }
            }
        } else {
            if (mTabLayout != null) {
                //method for older android versions
                //noinspection deprecation
                if(this.getSkinID()==UI.THEME_HORDE){
                    //noinspection deprecation
                    mTabLayout.setTabTextColors(activity.getResources().getColorStateList(R.color.bournmouth_tab_selector));
                }else{
                    //noinspection deprecation
                    mTabLayout.setTabTextColors(activity.getResources().getColorStateList(R.color.tab_selector));
                }}
        }
    }

    public int getSkinID(){
        return mSkinID;
    }
    public static void hideKeyboardFrom(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}