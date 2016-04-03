package com.digitale.wowpaper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by Rich on 13/03/2016.
 * Custom list adaptor for commentary details.
 */
public class StandingsAdapter extends BaseAdapter {

    private ArrayList<Standing> mData;
        private static LayoutInflater inflater = null;

        public StandingsAdapter(Context context, ArrayList<Standing> data) {
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
                itemView = (RelativeLayout) inflater.inflate(R.layout.standing_list_row,  parent,false);
            }else {
                itemView = (RelativeLayout) convertView;
            }
            ListView seasonListView = (ListView) itemView.findViewById(R.id.realmListView);


            TextView textLeagueName = (TextView) itemView.findViewById(R.id.cTeamName);
            textLeagueName.setText(mData.get(position).getTeamName());
            TextView textDetail = (TextView) itemView.findViewById(R.id.cTeamDetails);
            textDetail.setText("Points "+mData.get(position).getPoints()+
                                ". Wins "+ mData.get(position).getWins()+
                                ". Losses "+mData.get(position).getLosses()+
                                ". Draws "+mData.get(position).getDraws()
                                );
            TextView textTime = (TextView) itemView.findViewById(R.id.cRank);
            textTime.setText(String.valueOf(mData.get(position).getPosition()));
            return itemView;
        }

    }


