package com.digitale.wowpaper;


import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Get JSON data from incrowd Website.
 * Needs to be run as seperate thread so UI thread doesn't get blocked.
 * params[0] The mode of the data fetch (SEASONS or STATS).
 * params[1] The URL of the data.
 */
class GetFeedTask extends AsyncTask<Integer, Void, TaskResult> {
    //enumerate task modes
    private static final int NODATA = -2;
    private static final int NETWORKFAIL = -1;
    public static final int REALMLIST = 1;
    public static final int CHARACTER = 2;
    public static final int SQLREALMLIST = 3;
    public static final int CHARACTERIMAGE = 4;
    public static final int SQLCHARACTERLIST = 5;
    public static final int CHARACTERPROFILE = 6;
    public static final int SQLIMAGESFORWALLPAPER = 7;
    public static final int DELETERECORD = 8;
    public static final int SETREALMLIST = 9;
    private static final String TAG = "GETFEEDTASK ";
    private boolean localDebug = true;
    public MainActivity activity;
    public WoWWallpaperService wallPaperService;
    private int mode;
    public WoWCharacter currentCharacter = new WoWCharacter();
    private long id;
    private ProgressDialog progressDialog;
    public AsyncResponse delegate = null;

    public GetFeedTask() {
    }

    public GetFeedTask(int mode) {
    }

    public GetFeedTask(MainActivity activity, int mode) {
        this.activity = activity;
        this.mode = mode;
    }

    public GetFeedTask(WoWWallpaperService wallPaperService, int mode) {

        this.wallPaperService = wallPaperService;
        this.mode = mode;
    }

    public GetFeedTask(long id, int mode) {
        this.mode = mode;
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    protected void onPreExecute() {
        switch (mode) {
            case SQLIMAGESFORWALLPAPER:
                break;
            case CHARACTER:
                progressDialog = ProgressDialog.show(activity,
                        "Please wait", "Downloading character data for " + currentCharacter.getName());
                break;
            case CHARACTERIMAGE:
                progressDialog = ProgressDialog.show(activity,
                        "Please wait", "Downloading images for " + currentCharacter.getName());
                break;
            case REALMLIST:
                progressDialog = ProgressDialog.show(activity,
                        "Please wait", "Downloading realms for " +
                                activity.PrefsDB.getRegionNameFromURL(activity.mWoWRegionID) +
                                " region.");
                break;
            case SQLREALMLIST:
                progressDialog = ProgressDialog.show(activity,
                        "Please wait", "Retrieving realms from database for " +
                                activity.PrefsDB.getRegionNameFromURL(activity.mWoWRegionID) +
                                " region.");
                break;
//
        }
    }

    /**
     * Background data retrieval task
     *
     * @param params first param is flag to indicate if feed is JSON array or object
     *               Second param is the data URL
     */
    @Override
    protected TaskResult doInBackground(Integer... params) {
        TaskResult results = new TaskResult();
        mode = params[0];
        // currentCharacter = MainActivity.mDatabase.getCharacter();
        String getURL = "";
        String profileURL = "";
        //formulate urls depending on mode
        switch (mode) {
            case SETREALMLIST:
                Logger.writeLog(TAG, "SQL Save realms " + id, localDebug);
                //add realms data to SQLite database
                long ID = MainActivity.PrefsDB.insertRealms(MainActivity.mDatabase.getRealms());
                break;
            case DELETERECORD:
                Logger.writeLog(TAG, "ABOUT TO DELETE A CHARACTER " + id, localDebug);
                MainActivity.PrefsDB.deleteRecord(id, WoWCharacter.CharacterRecord.TABLE_NAME);
                break;
            case CHARACTER:
                getURL = MainActivity.PrefsDB.getRegionURLfromID(currentCharacter.getRegion_id()) +
                        "/wow/character/" + currentCharacter.getRealm() + "/" +
                        currentCharacter.getName() +
                        "?locale=en_GB&apikey=" + MainActivity.API;
                System.out.println("getting character URL " + getURL);
                break;
            case CHARACTERIMAGE:
                Logger.writeLog(TAG, "getting character (id" +
                        currentCharacter.get_id() + ") avatar URL " + getURL, localDebug);
                //concat the URL for avatar images from character and
                getURL = MainActivity.PrefsDB.getCurrentImageRegionURL(currentCharacter.getRegion_id())
                        + MainActivity.PrefsDB.getCharacterImageURL(currentCharacter.get_id());
                profileURL = getURL.replaceAll("avatar.jpg", "profilemain.jpg");
                String bustURL = getURL.replaceAll("avatar.jpg", "profilemain.jpg");
                break;
//
            case REALMLIST:
                getURL = MainActivity.mWoWRegionID + "wow/realm/status?locale=en_GB&apikey=" + MainActivity.API;
                System.out.println("getting REALM LIST URL " + getURL);
                break;
            case SQLREALMLIST:
                //update mainactivity data cache/adaptor datasource with characterlist data
                MainActivity.mRealmList.clear();
                String query = Realm.RealmRecord.COLUMN_NAME_REGIONID + "='" + MainActivity.mWoWRegionID + "'";
                Cursor realmCursor = MainActivity.PrefsDB.getTableContents(Realm.RealmRecord.TABLE_NAME, query);
                realmCursor.moveToFirst();
                while (!realmCursor.isAfterLast()) {
                    //where current realm's region ID matches current region
                    Realm cursorRealm = new Realm();
                    cursorRealm.setName(realmCursor.getString(realmCursor.getColumnIndexOrThrow(Realm.RealmRecord.COLUMN_NAME_NAME)));
                    cursorRealm.setFavourite(realmCursor.getInt(realmCursor.getColumnIndexOrThrow(Realm.RealmRecord.COLUMN_NAME_FAVOURITE)));
                    cursorRealm.set_id(realmCursor.getInt(realmCursor.getColumnIndexOrThrow(Realm.RealmRecord.COLUMN_NAME_ID)));
                    // The Cursor is now set to the right position
                    MainActivity.mRealmList.add(cursorRealm);
                    realmCursor.moveToNext();
                }
                break;

            case SQLCHARACTERLIST:
                //update mainactivity data cache/adaptor datasource with characterlist data
                MainActivity.mCharacters.clear();
                Cursor charactersCursor = MainActivity.PrefsDB.getTableContents(WoWCharacter.CharacterRecord.TABLE_NAME, null);
                charactersCursor.moveToFirst();
                while (!charactersCursor.isAfterLast()) {
                    WoWCharacter cursorCharacter = new WoWCharacter();
                    cursorCharacter.setName(charactersCursor.getString(charactersCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_NAME)));
                    cursorCharacter.set_id(charactersCursor.getInt(charactersCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_ID)));
                    cursorCharacter.setRealm(charactersCursor.getString(charactersCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_REALM)));
                    cursorCharacter.setRegion_id(charactersCursor.getInt(charactersCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_REGION)));
                    cursorCharacter.setAvatar(charactersCursor.getBlob(charactersCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_AVATAR)));
                    cursorCharacter.setProfilemain(charactersCursor.getBlob(charactersCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_PROFILE)));
                    cursorCharacter.setBattlegroup(charactersCursor.getString(charactersCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP)));
                    // The Cursor is now set to the right position
                    MainActivity.mCharacters.add(cursorCharacter);
                    charactersCursor.moveToNext();
                }
                //charactersCursor.close();
                break;

            case SQLIMAGESFORWALLPAPER:
                //update wallpaper data cache/adaptor datasource with characterlist data
                WoWWallpaperService.mImages.clear();
                Cursor wallpaperCursor = WoWWallpaperService.wallpaperDB.getTableContents(WoWCharacter.CharacterRecord.TABLE_NAME, null);
                wallpaperCursor.moveToFirst();
                while (!wallpaperCursor.isAfterLast()) {
                    WoWCharacter cursorWallpaper = new WoWCharacter();
                    //we need character name, realm, avatar and profile images for wallpaper data
                    cursorWallpaper.setName(wallpaperCursor.getString(wallpaperCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_NAME)));
                    cursorWallpaper.setRealm(wallpaperCursor.getString(wallpaperCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_REALM)));
                    cursorWallpaper.setAvatar(wallpaperCursor.getBlob(wallpaperCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_AVATAR)));
                    cursorWallpaper.setProfilemain(wallpaperCursor.getBlob(wallpaperCursor.getColumnIndexOrThrow(WoWCharacter.CharacterRecord.COLUMN_NAME_PROFILE)));
                    // The Cursor is now set to the right position
                    WoWWallpaperService.mImages.add(cursorWallpaper);
                    wallpaperCursor.moveToNext();
                }
                wallpaperCursor.close();
                break;
        }
        //format URL to remove spaces
        getURL = getURL.replace(" ", "%20");
        //nasty deprecated stuff I need for my phone
        //HttpURLConnection is v buggy Eclair/Froyo
        HttpResponse response;
        HttpClient myClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(getURL);

        String str = null;
        byte[] image = new byte[0];
        byte[] profile = new byte[0];
        //are we gettting data from a String or an image
        try {
            //if process not cancelled and url is not empty
            if (!isCancelled() && !getURL.isEmpty()) {
                response = myClient.execute(get);
                //if we are getting a character image
                if (mode == CHARACTERIMAGE) {
                    //retrieve a bitmap
                    ImageDownloader imageDownloader = new ImageDownloader();
                    image = imageDownloader.getLogoImage(getURL);
                    profile = imageDownloader.getLogoImage(profileURL);
                } else {
                    //retrieve a string
                    str = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            }

            // pass data to database for saving to internal data structures
            if ((image.length >= 0 || str != null) && !(isCancelled())) {
                if (mode == CHARACTER) {
                    decodeCharacter(str);

                } else if (mode == REALMLIST) {
                    decodeRealms(str);
                } else if (mode == CHARACTERIMAGE) {
                    decodeCharacterImage(image, profile);
                }
                results.setMode(mode);
            }
        } catch (ClientProtocolException e) {
            results.setMode(NETWORKFAIL);
            e.printStackTrace();
        } catch (IOException e) {
            results.setMode(NETWORKFAIL);
            e.printStackTrace();
        } catch (DataNotFoundException e) {
            results.setMode(NODATA);
            results.setResultCode(String.valueOf(e.getMessage()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return results;
    }

    //check server supplied valid image then store
    private void decodeCharacterImage(byte[] image, byte[] profile) throws DataNotFoundException {


        //check for error response in data
//        if (str.contains("nok") && str.contains("status")) {
//            //data contains server error code, tell user and stop
//            throw new DataNotFoundException("Data error:- Cannot retrieve realm list for region "
//                    + activity.PrefsDB.getRegionNameFromURL(activity.mWoWRegionID));
//        } else {
        //response is ok, decode and store character JSON
        MainActivity.mDatabase.setAvatar(image, currentCharacter);
        MainActivity.mDatabase.setProfileImage(profile, currentCharacter);
//        }
    }

    //check server supplied valid data then store
    private void decodeRealms(String str) throws JSONException, DataNotFoundException {
        System.out.println("Realm DATA" + str);
        JSONObject jResponse = new JSONObject(str);
        //check for error response in data
        if (str.contains("nok") && str.contains("status")) {
            //data contains server error code, tell user and stop
            throw new DataNotFoundException("Data error:- Cannot retrieve realm list for region "
                    + MainActivity.PrefsDB.getRegionNameFromURL(MainActivity.mWoWRegionID));
        } else {
            //response is ok, decode and store character JSON
            MainActivity.mDatabase.realmFromJSON(str);
        }
    }

    //check server supplied valid data then store
    private void decodeCharacter(String str) throws JSONException, DataNotFoundException {
        System.out.println("Character DATA" + str);
        JSONObject jResponse = new JSONObject(str);
        //check for error response in data
        if (str.contains("status")) {
            //uh-oh server response contains error status
            if (jResponse.getString("status").contains("nok")) {
                //data contains server error code, tell user and stop
                throw new DataNotFoundException("Data error:- " + jResponse.getString("reason") +
                        " Check spelling and that this character is on " + MainActivity.mRealmID);
            }
        } else {
            //response is ok, decode and store character JSON
            long newId = MainActivity.mDatabase.characterFromJson(str);
            Logger.writeLog(TAG, "New character inserted into DB, ID=" + newId, localDebug);
        }
    }

    @Override
    protected void onPostExecute(TaskResult result) {
        System.out.println("REFRESHUI mode " + result.getMode());
        if (progressDialog != null) progressDialog.dismiss();
        if (result.getMode() == CHARACTER) {
            delegate.processFinish("CHARACTER");
            System.out.println("DB CHARACTER CONTENTS" + MainActivity.mDatabase.getCharacter().getName());
            //refresh character list data
            GetFeedTask characterListAsyncTask = new GetFeedTask(activity, GetFeedTask.SQLCHARACTERLIST);
            characterListAsyncTask.execute(GetFeedTask.SQLCHARACTERLIST);
            Logger.writeLog(TAG, "Got character data from web", localDebug);

        } else if (result.getMode() == SQLIMAGESFORWALLPAPER) {
            WoWWallpaperService.mImageCache.clear();
            Drawable bitmapError = activity.getContext().getResources().getDrawable(R.drawable.firstaid);
            Bitmap mutableBitmap;
            for (WoWCharacter currentCharacter : WoWWallpaperService.mImages) {
                Logger.writeLog(TAG, "wallpaper Cacheing " + currentCharacter.getName(), localDebug);
                if (currentCharacter.getProfilemain() != null) {
                    mutableBitmap = BitmapFactory.decodeByteArray(currentCharacter.getProfilemain(), 0, currentCharacter.getProfilemain().length);
                } else {
                    mutableBitmap = ((BitmapDrawable) MainActivity.bitmapError).getBitmap();
                }
                mutableBitmap = mutableBitmap.copy(Bitmap.Config.RGB_565, true);
                mutableBitmap = wallPaperService.getResizedBitmap(mutableBitmap, 0, wallPaperService.getScreenMetrics().getHeight());
                WoWWallpaperService.mImageCache.add(mutableBitmap);
                //   mutableBitmap.recycle();
            }
            Logger.writeLog(TAG, "Got wallpaper images from SQLite.", localDebug);

        } else if (result.getMode() == NETWORKFAIL) {
            //Oh Google! Enforced statics in abstract classes make baby jesus cry!
            Toast.makeText(MainActivity.getContext(), "Cannot contact server, please try again later",
                    Toast.LENGTH_LONG).show();
            activity.finish();

        } else if (result.getMode() == NODATA) {
            //Oh Google! Enforced statics in abstract classes make baby jesus cry!
            Toast.makeText(MainActivity.getContext(), result.getResultCode(),
                    Toast.LENGTH_LONG).show();
            activity.mViewPager.setCurrentItem(0);

        } else if (result.getMode() == REALMLIST) {
            //update the serverlist ui only
            delegate.processFinish("REALMLIST");
            MainActivity.mRealmList.clear();
            MainActivity.mRealmList.addAll(MainActivity.mDatabase.getRealms());
            MainActivity.mRealmAdapter.notifyDataSetChanged();
            MainActivity.mRealmID = MainActivity.mRealmList.get(0).getName();
            MainActivity.realmListFragment.setServerTextDisplay();
            activity.prefsSave();
            Logger.writeLog(TAG, "Got realmlist from web", localDebug);

        } else if (result.getMode() == CHARACTERIMAGE) {
            //valid image was retrieved, update UI
            MainActivity.realmListFragment.setAvatarDisplay();
            //   MainActivity.mCharacters.clear();
            //          MainActivity.mCharactersAdapter.notifyDataSetChanged();
            //      MainActivity.mGalleryAdapter.notifyDataSetChanged();

            GetFeedTask characterListAsyncTask = new GetFeedTask(GetFeedTask.SQLCHARACTERLIST);
            characterListAsyncTask.execute(GetFeedTask.SQLCHARACTERLIST);
            //TODO work out why gallery/avatar is not being updated when changing pictures

            Logger.writeLog(TAG, "Got character images from web.", localDebug);

        } else if (result.getMode() == SQLCHARACTERLIST) {
            //image data has changed, tell UI about it
            MainActivity.mCharactersAdapter.notifyDataSetChanged();
            MainActivity.mGalleryAdapter.notifyDataSetChanged();
            Logger.writeLog(TAG, "Got character list from SQLite", localDebug);

        } else if (result.getMode() == SQLREALMLIST) {
            if (MainActivity.mRealmList.size() > 0) {
                MainActivity.mRealmAdapter.notifyDataSetChanged();
                delegate.processFinish("POPULATED SQLREALMLIST");
            } else {
                delegate.processFinish("EMPTY SQLREALMLIST");
            }
            Logger.writeLog(TAG, "Got realmlist from SQLite", localDebug);

        } else if (result.getMode() == DELETERECORD) {
            GetFeedTask characterListAsyncTask = new GetFeedTask(GetFeedTask.SQLCHARACTERLIST);
            characterListAsyncTask.execute(GetFeedTask.SQLCHARACTERLIST);
            Logger.writeLog(TAG, "Deleted Record", localDebug);
        }
    }
}