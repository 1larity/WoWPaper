package com.digitale.wowpaper;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
import static com.digitale.wowpaper.MainActivity.mCharactersFragment;
import static com.digitale.wowpaper.MainActivity.mRealmID;
import static com.digitale.wowpaper.MainActivity.mRealmList;

/**
 * Created by Rich on 02/04/2016.
 * Custom list adaptor for realm details.
 */
public class RealmAdapter extends BaseAdapter {
    private static final String TAG ="REALM LIST ADAPTOR " ;
    private ArrayList<Realm> mData;
        private static LayoutInflater inflater = null;
    private boolean localDebug = true;

    public RealmAdapter(Context context, ArrayList<Realm> data) {
            this.mData = data;
            inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }



    @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int staticPosition=position;
            RelativeLayout itemView;
            if (convertView == null) {
                itemView = (RelativeLayout) inflater.inflate(R.layout.realm_list_row,  parent,false);
            }else {
                itemView = (RelativeLayout) convertView;
            }
            TextView textRealmName = (TextView) itemView.findViewById(R.id.cRealmName);
            textRealmName.setText(mData.get(position).getName());
            //listview click event
            textRealmName.setOnLongClickListener(new View.OnLongClickListener() {
                public boolean onLongClick(View view) {
                    mRealmID = mRealmList.get(staticPosition).getName();
                    Log.i(TAG, "POSITION " + staticPosition + " server ID " + mRealmID);
                    MainActivity.realmListFragment.setServerTextDisplay();
                    SharedPreferences prefs = MainActivity.realmListFragment.getActivity().getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("mRealmID", mRealmID);
                    ed.apply();
                    return true;
                }
            });


            CheckBox cbFavourite = (CheckBox) itemView.findViewById(R.id.checkBoxRealmFavourite);
            //disable listener while setting visual state of checkboxes
            cbFavourite.setOnCheckedChangeListener (null);
            if(mData.get(position).getFavourite()==1) {
                cbFavourite.setChecked(true);
            }else{
                cbFavourite.setChecked(false);
            }
            cbFavourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {

                        saveCBState(1,mData.get(staticPosition));
                    } else {

                        saveCBState(0,mData.get(staticPosition));
                    }

                }
            });

            return itemView;
        }
    private void saveCBState(int CBState,Realm realm){

        GetFeedTask realmFlipFavAsyncTask = new GetFeedTask((MainActivity) MainActivity.realmListFragment.getActivity(), GetFeedTask.FAVOURITEREALM);
       //update adaptor data source, so we dont have to get from the DB
        for(Realm currentRealm: MainActivity.mRealmList){
            if(realm.get_id()==currentRealm.get_id()){
            currentRealm.setFavourite(CBState);
            }
        }


        Logger.writeLog(TAG, "Flipping favourite status of realm "+realm.getName()+" ID " +
                realm.get_id() +     " to " + CBState, localDebug);
        //set feedtask working record to the ID we want to search for
        realmFlipFavAsyncTask.currentRealm.set_id(realm.get_id());
        //set feedtask working record to the Favourite status we wish to set
        realmFlipFavAsyncTask.currentRealm.setFavourite(CBState);
        realmFlipFavAsyncTask.delegate=(MainActivity) MainActivity.realmListFragment.getActivity();
        realmFlipFavAsyncTask.execute(GetFeedTask.FAVOURITEREALM);
    }

    }


