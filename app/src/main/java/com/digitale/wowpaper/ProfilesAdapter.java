package com.digitale.wowpaper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Rich on 13/03/2016.
 * Custom list adaptor for commentary details.
 */
public class ProfilesAdapter extends BaseAdapter {

    private ArrayList<WoWCharacter> mData;
        private static LayoutInflater inflater = null;

        public ProfilesAdapter(Context context, ArrayList<WoWCharacter> data) {
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
                itemView = (RelativeLayout) inflater.inflate(R.layout.character_list_row,  parent,false);
            }else {
                itemView = (RelativeLayout) convertView;
            }
            TextView textCharacterName = (TextView) itemView.findViewById(R.id.cCharacterName);
            textCharacterName.setText(mData.get(position).getName());
            TextView textDetail = (TextView) itemView.findViewById(R.id.cCharacterDetails);
            //get list of regions
            Cursor regionCursor=MainActivity.PrefsDB.getRegions();
            //iterate through cursor until the regionID matches the character's
            while (mData.get(position).getRegion()!= regionCursor.getInt(0)){
                regionCursor.moveToNext();
            }

            textDetail.setText(mData.get(position).getRealm()+
                                " "+ regionCursor.getString(1));
            ImageView avatarImage = (ImageView) itemView.findViewById(R.id.imageListAvatar);
            Bitmap bmp = BitmapFactory.decodeByteArray(mData.get(position).getAvatar(), 0, mData.get(position).getAvatar().length);
            avatarImage.setImageBitmap(bmp);
            return itemView;
        }

    }


