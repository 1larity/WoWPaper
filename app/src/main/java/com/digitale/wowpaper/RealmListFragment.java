package com.digitale.wowpaper;

/**
 * Fragment for displaying realm list
 * Created by Rich on 02/04/2016.
 */

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
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

import static com.digitale.wowpaper.MainActivity.*;

/**
 * Realmlist Fragment.
 */
public class RealmListFragment extends Fragment implements FragmentNotifier{
    private static final String TAG ="REALM LIST FRAGMENT " ;
    private static final boolean DEBUG =true ;
    View rootView = null;
    private boolean localDebug=true;

    public RealmListFragment() {
    }

    @Override
    public void fragmentVisible() {
        // do animations in here
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         final Cursor wowRegions;
        rootView = inflater.inflate(R.layout.fragment_realm_list, container, false);
       final TextView serverText= (TextView) rootView.findViewById(R.id.labelSelectedServer);
        ListView seasonListView = (ListView) rootView.findViewById(R.id.realmListView);
            seasonListView.setAdapter(mRealmAdapter);
        //listview override scrollview
        seasonListView.setOnTouchListener(new View.OnTouchListener() {
            // Setting on Touch Listener for handling the touch inside ScrollView
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });

        //region spinner setup
        Spinner spinnerRegion=(Spinner) rootView.findViewById(R.id.spinnerRegion);
        wowRegions = PrefsDB.getRegions();
        SpinnerAdapter adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                wowRegions,
                new String[] {"geo_zone_name"},
                new int[] {android.R.id.text1},0);
        spinnerRegion.setAdapter(adapter);
        Log.i(TAG, "SETSPINNER" + mWoWRegionID + "REG NAME" + PrefsDB.getRegionIDFromURL(mWoWRegionID)+
                " INDEX "+ PrefsDB.getRegionIDFromURL(mWoWRegionID));
        spinnerRegion.setSelection( PrefsDB.getRegionIDFromURL(mWoWRegionID)-1, false);
        //spinner click event need to set global Region ID and update server list
        spinnerRegion.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //set global region ID
                        mWoWRegionID = PrefsDB.getCurrentRegionURL(position);
                        //refresh server list
                        GetFeedTask realmsAsyncTask = new GetFeedTask((MainActivity) getActivity(),GetFeedTask.REALMLIST);
                        realmsAsyncTask.delegate =(MainActivity) getActivity();
                        realmsAsyncTask.execute(GetFeedTask.REALMLIST);
                        if (DEBUG)
                            Log.i(TAG, " POSITION" + position + " Region URL " + mWoWRegionID);
                    }

                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
        //character text box setup
        final EditText searchEditText = (EditText) rootView.findViewById(R.id.editSearchText);
        searchEditText.setTextColor(UI.getmTextColourPrimary());
        //setup search button
        Button buttonSearch = (Button) rootView.findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                UI.hideKeyboardFrom(getContext(), v);
                String name=String.valueOf(searchEditText.getText());
                GetFeedTask characterDataAsyncTask = new GetFeedTask((MainActivity) getActivity(),GetFeedTask.CHARACTER);
                //set Feedtask character (search term)
                characterDataAsyncTask.currentCharacter.setName(name);
                characterDataAsyncTask.currentCharacter.setRealm( MainActivity.mRealmID);
                characterDataAsyncTask.currentCharacter.setRegion_id(MainActivity.PrefsDB.getRegionIDFromURL(MainActivity.mWoWRegionID));
                //set post execute callback
                characterDataAsyncTask.delegate = (MainActivity) getActivity();
                characterDataAsyncTask.execute(GetFeedTask.CHARACTER);

            }

        });

        //Test function to fill character database
        Button buttonFill = (Button) rootView.findViewById(R.id.buttonFill);
        buttonFill.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //hide keyboard
                UI.hideKeyboardFrom(getContext(), v);
                for(String currentCharacter:MainActivity.testSubjects) {
                    Logger.writeLog(TAG,"Inserting test subject "+currentCharacter,localDebug);
                    GetFeedTask characterDataAsyncTask = new GetFeedTask((MainActivity) getActivity(), GetFeedTask.CHARACTER);

                    characterDataAsyncTask.currentCharacter.setName(currentCharacter);
                    characterDataAsyncTask.currentCharacter.setRealm("Silvermoon");
                    characterDataAsyncTask.currentCharacter.setRegion_id(MainActivity.PrefsDB.getRegionIDFromURL(MainActivity.mWoWRegionID));
                    characterDataAsyncTask.delegate = (MainActivity) getActivity();
                    characterDataAsyncTask.execute(GetFeedTask.CHARACTER);
                    if (characterDataAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        });
        setServerTextDisplay();

        return rootView;
    }

    private void showLeaguePage(MainActivity activity) {
        activity.mViewPager.setCurrentItem(1);
    }

    public void setServerTextDisplay(){
        if (rootView != null) {
            TextView serverText = (TextView) rootView.findViewById(R.id.labelSelectedServer);
            serverText.setText(mRealmID);

        }
    }
    public void setAvatarDisplay(){
        if (rootView != null) {
            ImageView avatarImage = (ImageView) rootView.findViewById(R.id.imageAvatar);
            Bitmap bmp = BitmapFactory.decodeByteArray(mDatabase.character.getAvatar(), 0, mDatabase.character.getAvatar().length);
            avatarImage.setImageBitmap(bmp);

        }
    }
}