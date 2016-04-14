package com.digitale.wowpaper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.ArrayList;

import static com.digitale.wowpaper.MainActivity.mCharacterName;
import static com.digitale.wowpaper.MainActivity.mCharactersFragment;

/**
 * Created by Rich on 13/03/2016.
 * Custom list adaptor for commentary details.
 */
public class CharactersAdapter extends BaseAdapter {

    private static final String TAG = "CHARACTER ADAPTOR ";
    private boolean localDebug = true;
    private ArrayList<WoWCharacter> mData;
    private static LayoutInflater inflater = null;
    private Context mContext;

    public CharactersAdapter(Context context, ArrayList<WoWCharacter> data) {
        this.mData = data;
        this.mContext = context;
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
        final RelativeLayout itemView;
        final int staticPosition = position;
        if (convertView == null) {
            itemView = (RelativeLayout) inflater.inflate(R.layout.character_list_row, parent, false);
        } else {
            itemView = (RelativeLayout) convertView;
        }
        //Display character name
        TextView textCharacterName = (TextView) itemView.findViewById(R.id.cCharacterName);
        textCharacterName.setText(mData.get(position).getName());
        //Set up favourite checkbox display
        CheckBox cbFavourite = (CheckBox) itemView.findViewById(R.id.checkboxCharacterFavourite);
        if (mData.get(position).getFavourite() == 1) {
            cbFavourite.setChecked(true);
        } else cbFavourite.setChecked(false);

        //Display additional character details
        TextView textDetail = (TextView) itemView.findViewById(R.id.cCharacterDetails);
        //get list of regions
        Cursor regionCursor = MainActivity.PrefsDB.getRegions();
        //iterate through cursor until the regionID matches the character's
        while (mData.get(position).getRegion_id() != regionCursor.getInt(0)) {
            regionCursor.moveToNext();
        }

        textDetail.setText(mData.get(position).getRealm() +
                " " + regionCursor.getString(1));
        //Display avatar image
        ImageView avatarImage = (ImageView) itemView.findViewById(R.id.imageListAvatar);
        Bitmap bmp;
        if (mData.get(position).getProfilemain() != null) {
            bmp = BitmapFactory.decodeByteArray(mData.get(position).getAvatar(), 0, mData.get(position).getAvatar().length);
        } else {
            //  if image is null set it to error bitmap
            Drawable bitmapError = mContext.getResources().getDrawable(R.drawable.firstaid);
            bmp = ((BitmapDrawable) bitmapError).getBitmap();
        }

        avatarImage.setImageBitmap(bmp);
        //attach delete character event to button
        ImageButton deleteButton = (ImageButton) itemView.findViewById((R.id.buttonCharacterDelete));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteAlert(mContext, staticPosition);

            }

        });
        ImageButton refreshButton = (ImageButton) itemView.findViewById((R.id.buttonCharacterRefresh));
        refreshButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                GetFeedTask characterImageAsyncTask = new GetFeedTask((MainActivity) MainActivity.mCharactersFragment.getActivity(), GetFeedTask.CHARACTERIMAGE);
                //set Feedtask character (search term)

                Logger.writeLog(TAG, "refreshing images for character" + mData.get(staticPosition).get_id() +
                        " with region " + mData.get(staticPosition).getRegion_id() + " Realm " +
                        mData.get(staticPosition).get_id(), localDebug);
                characterImageAsyncTask.currentCharacter.set_id(mData.get(staticPosition).get_id());
                characterImageAsyncTask.currentCharacter.setName(mData.get(staticPosition).getName());
                characterImageAsyncTask.currentCharacter.setRealm(mData.get(staticPosition).getRealm());
                characterImageAsyncTask.currentCharacter.setRegion_id(mData.get(staticPosition).getRegion_id());
                characterImageAsyncTask.currentCharacter.setBattlegroup(mData.get(staticPosition).getBattlegroup());
                //set callback to mainactivity (we will need to update UI and post execute there)
                //    characterImageAsyncTask.delegate =(MainActivity)mCharactersFragment.getActivity();
                characterImageAsyncTask.execute(GetFeedTask.CHARACTERIMAGE);
            }

        });
        return itemView;
    }

    private void deleteAlert(Context context, final int lPosition) {

        final Dialog alert = new Dialog(context);
        alert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alert.setContentView(R.layout.alert_dialog_horde);

        TextView msg = (TextView) alert.findViewById(R.id.textContent);
        msg.setTextColor(MainActivity.ui.getmTextColourPrimary());
        msg.setText("Are your sure you want to remove " + mData.get(lPosition).getName() + " from the database?");

        TextView tv = (TextView) alert.findViewById(R.id.textTitle);
        tv.setTextColor(MainActivity.ui.getmTextColourPrimary());
        tv.setText("Delete character");

        Button buttonOK = (Button) alert.findViewById(R.id.buttonOK);
        Button buttonCancel = (Button) alert.findViewById(R.id.buttonCancel);
        buttonOK.setTextColor(MainActivity.ui.getmTextColourPrimary());
        buttonCancel.setTextColor(MainActivity.ui.getmTextColourPrimary());
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });
        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetFeedTask deleteAsyncTask = new GetFeedTask(mData.get(lPosition).get_id(), GetFeedTask.DELETERECORD);
                // deleteAsyncTast.delegate=MainActivity.
                deleteAsyncTask.execute(GetFeedTask.DELETERECORD);
                alert.dismiss();
            }
        });
        alert.show();
    }

}


