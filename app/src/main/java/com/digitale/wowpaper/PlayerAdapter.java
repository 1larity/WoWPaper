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
 * Created by Rich on 13/03/2016.
 * Custom list adaptor for commentary details.
 * TODO need to add list view holder to improve scroll performance
 */
public class PlayerAdapter extends BaseAdapter {
        Context context;
    private ArrayList<Player> mData;
        private static LayoutInflater inflater = null;

        public PlayerAdapter(Context context, ArrayList<Player> data) {
            this.context = context;
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
            if (convertView == null) {
                itemView = (RelativeLayout) inflater.inflate(R.layout.player_list_row,  parent,false);
            }
            else {
                itemView = (RelativeLayout) convertView;
            }
            //set basic player data

            TextView textHeading = (TextView) itemView.findViewById(R.id.cPlayerName);
            textHeading.setText(mData.get(position).getName());
            TextView textDetail = (TextView) itemView.findViewById(R.id.cNumber);
            textDetail.setText(mData.get(position).getJerseyNumber());
            TextView textRole = (TextView) itemView.findViewById(R.id.cRole);
            textRole.setText(mData.get(position).getPosition());


            return itemView;
        }


}


