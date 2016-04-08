package com.digitale.wowpaper;

/**
 * Fragment for league standings information
 * Created by Rich on 24/03/2016.
 */

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.lucasr.twowayview.TwoWayLayoutManager;
import org.lucasr.twowayview.widget.TwoWayView;

/**
 * Characters list/picker Fragment.
 */
public class CharactersFragment extends Fragment implements FragmentNotifier{
    View rootView = null;
    public CharactersFragment() {
    }
    @Override
    public void fragmentVisible() {
        //  do animation and frills in here
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Cursor wowCharacters;
        rootView = inflater.inflate(R.layout.fragment_character_list, container, false);
        //set up gallery of profile images
        TwoWayView profileGallery=(TwoWayView) rootView.findViewById(R.id.galleryView);
        profileGallery.setOrientation(TwoWayLayoutManager.Orientation.HORIZONTAL);
        profileGallery.setAdapter(MainActivity.mGalleryAdapter);


        //set up listview of characters in SQLite database
        ListView characterListView = (ListView) rootView.findViewById(R.id.leagueListView);
         characterListView.setAdapter(MainActivity.mCharactersAdapter);
        characterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = MainActivity.mDatabase.getCharacter().getThumbnail();
                System.out.println("TEAM HTTP" + str);
                //get team id from last part of URL
                str = str.substring(str.lastIndexOf("/") + 1, str.length());
                System.out.println("TEAM ID" + str);
                MainActivity.mTeamID = Integer.valueOf(str);
                MainActivity.mTeamIndex = position;
                System.out.println("POSITION" + position + " TEAM ID " + MainActivity.mTeamID);
                //   GetFeedTask teamAsyncTask = new GetFeedTask((MainActivity) getActivity());
                //   teamAsyncTask.execute(GetFeedTask.TEAM);
                //   GetFeedTask playersAsyncTask = new GetFeedTask((MainActivity) getActivity());
                //   playersAsyncTask.execute(GetFeedTask.PLAYERS);
                //     showTeamPage((MainActivity) getActivity());

            }
        });
        invalidateStandingsInfoView((MainActivity) getActivity());
        return rootView;
    }
    public  void invalidateStandingsInfoView(MainActivity activity) {
      //  TextView textCompetition = (TextView) rootView.findViewById(R.id.cCompetition);
       // textCompetition.setText(MainActivity.mDatabase.getLeague().getName());

    }
    private void showTeamPage(MainActivity activity) {
        activity.mViewPager.setCurrentItem(2);
    }
}
