package com.digitale.wowpaper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.lucasr.twowayview.widget.TwoWayView;

import java.util.ArrayList;

/**
 * Created by Rich on 13/03/2016.
 * Custom list adaptor for commentary details.
 */
public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.SimpleViewHolder> {

    private static final String TAG = "GALLERY ADAPTOR ";
    private ArrayList<WoWCharacter> mData;
   // private final TwoWayView mRecyclerView;
  //  private static LayoutInflater inflater = null;
    private final Context mContext;
  //  private final int mLayoutId;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {
        public final ImageView imageProfile;

        public SimpleViewHolder(View view) {
            super(view);
            imageProfile = (ImageView) view.findViewById(R.id.imageProfile);
        }
    }

    public GalleryAdapter(Context context,  ArrayList<WoWCharacter> data) {
        this.mData = data;
        mContext = context;
//        inflater = (LayoutInflater) context
//                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    //    mRecyclerView = recyclerView;
    //    mLayoutId = layoutId;
    }


    @Override
    public int getItemCount() {
        return mData.size();
    }

//        @Override
//        public Object getItem(int position) {
//            return mData.get(position);
//        }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(mContext).inflate(R.layout.gallery_item, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        Bitmap bmp = BitmapFactory.decodeByteArray(mData.get(position).getProfilemain(), 0, mData.get(position).getProfilemain().length);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        holder.imageProfile.setLayoutParams(params);
        holder.imageProfile.setImageBitmap(bmp);
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LinearLayout itemView;
//
//        if (convertView == null) {
//            itemView = (LinearLayout) inflater.inflate(R.layout.gallery_item, parent, false);
//        } else {
//            itemView = (LinearLayout) convertView;
//        }
//        try {
//
//            ImageView profileImage = (ImageView) itemView.findViewById(R.id.imageProfile);
//            Bitmap bmp = BitmapFactory.decodeByteArray(mData.get(position).getProfilemain(), 0, mData.get(position).getProfilemain().length);
//            profileImage.setImageBitmap(bmp);
//        } catch (NullPointerException e) {
//            Log.e(TAG, "Gallery error");
//            e.printStackTrace();
//        }
//        return itemView;
//    }

}


