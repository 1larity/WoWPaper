package com.digitale.wowpaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by Rich on 02/04/2016.
 * Custom list adaptor for realm details.
 */
public class RealmAdapter extends BaseAdapter {

    private ArrayList<Realm> mData;
        private static LayoutInflater inflater = null;

        public RealmAdapter(Context context, ArrayList<Realm> data) {
            this.mData = data;
//            for (Season currentSeason : this.mData) {
//                System.out.println("ADAPTOR Realms " + currentSeason.getCaption());
//            }
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
            if (convertView == null) {
                itemView = (RelativeLayout) inflater.inflate(R.layout.realm_list_row,  parent,false);
            }else {
                itemView = (RelativeLayout) convertView;
            }
            TextView textRealmName = (TextView) itemView.findViewById(R.id.cRealmName);
            textRealmName.setText(mData.get(position).getName());
            return itemView;
        }

    }


