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
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;
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
            RelativeLayout itemView;
            final int lposition=position;
            if (convertView == null) {
                itemView = (RelativeLayout) inflater.inflate(R.layout.realm_list_row,  parent,false);
            }else {
                itemView = (RelativeLayout) convertView;
            }
            TextView textRealmName = (TextView) itemView.findViewById(R.id.cRealmName);
            textRealmName.setText(mData.get(position).getName());
            //listview click event
            textRealmName.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    mRealmID = mRealmList.get(lposition).getName();
                    Log.i(TAG, "POSITION " + lposition + " server ID " + mRealmID);
                    MainActivity.realmListFragment.setServerTextDisplay();
                    SharedPreferences prefs = MainActivity.realmListFragment.getActivity().getPreferences(MODE_PRIVATE);
                    SharedPreferences.Editor ed = prefs.edit();
                    ed.putString("mRealmID", mRealmID);
                    ed.apply();
                }
            });


//            CheckBox cbFavourite = (CheckBox) itemView.findViewById(R.id.checkBoxRealmFavourite);
//            if(mData.get(position).getFavourite()==1) {
//                cbFavourite.setChecked(true);
//            } else      cbFavourite.setChecked(false);

            return itemView;
        }

    }


