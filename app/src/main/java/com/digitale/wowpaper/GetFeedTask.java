package com.digitale.wowpaper;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
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
    public static final int CHARACTER = 2;
    public static final int REALMLISTREFRESH = 3;
    public static final int CHARACTERIMAGE = 4;
    public static final int PLAYER = 5;
    public static final int REALMLIST = 1;
    public MainActivity activity;
    private int mode;
    private ProgressDialog progressDialog;
    public GetFeedTask(MainActivity activity,int mode) {

        this.activity = activity;
        this.mode=mode;
    }

    @Override
    protected void onPreExecute() {
        switch (mode) {
            case CHARACTER:
                progressDialog = ProgressDialog.show(activity,
                        "Please wait", "Downloading character data for "+activity.mCharacterName);
                break;
            case CHARACTERIMAGE:
                progressDialog = ProgressDialog.show(activity,
                        "Please wait", "Downloading image for "+activity.mCharacterName);
                break;
            case REALMLIST:
                progressDialog = ProgressDialog.show(activity,
                        "Please wait", "Downloading realms for "+
                                activity.db.getRegionNameFromURL(activity.mWoWRegionID)+
                " region.");
                break;
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
        String getURL = "";
        //formulate urls depending on mode
        switch (mode) {

            case CHARACTER:
                getURL = MainActivity.mWoWRegionID + "/wow/character/" + MainActivity.mRealmID + "/" +
                        MainActivity.mCharacterName +
                        "?locale=en_GB&apikey=" + MainActivity.API;
                System.out.println("getting character URL " + getURL);
                break;
            case CHARACTERIMAGE:
                WOWCharacter currentCharacter = MainActivity.mDatabase.getCharacter();
                getURL = MainActivity.db.getCurrentImageRegionURL(
                        MainActivity.db.getRegionIDFromURL(MainActivity.mWoWRegionID))
                        + MainActivity.db.getCharacterImageURL(currentCharacter.getName(), currentCharacter.getRealm(),
                        currentCharacter.getBattlegroup(), currentCharacter.getRegion());
                System.out.println("getting character image URL " + getURL);
                break;
            case REALMLIST:
                getURL = MainActivity.mWoWRegionID + "wow/realm/status?locale=en_GB&apikey=" + MainActivity.API;
                System.out.println("getting REALM LIST URL " + getURL);
                break;
            case REALMLISTREFRESH:
                getURL = MainActivity.mWoWRegionID + "wow/realm/status?locale=en_GB&apikey=" + MainActivity.API;
                System.out.println("getting REALM LIST URL " + getURL);
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
        try {
            if (!isCancelled()) {
                response = myClient.execute(get);
                //if we are getting a character image
                if (mode == CHARACTERIMAGE) {
                    //retrieve a bitmap
                    ImageDownloader imageDownloader = new ImageDownloader();
                    image = imageDownloader.getLogoImage(getURL);
                } else {
                    //retrieve a string
                    str = EntityUtils.toString(response.getEntity(), "UTF-8");
                }
            }

            // pass data to database for saving to internal data structures
            if ((image.length>=0 ||str != null) && !(isCancelled())) {
                if (mode == CHARACTER)
                    decodeCharacter(str);
                else if (mode == PLAYER) {
//                    System.out.println("PLAYER DATA");
                    //    activity.mDatabase.PlayerFromJson;
                } else if (mode == REALMLIST || mode == REALMLISTREFRESH) {
                    decodeRealms(str);
                } else if (mode == CHARACTERIMAGE) {
                    decodeCharacterImage(image);
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
    private void decodeCharacterImage(byte[] image) throws DataNotFoundException {


        //check for error response in data
//        if (str.contains("nok") && str.contains("status")) {
//            //data contains server error code, tell user and stop
//            throw new DataNotFoundException("Data error:- Cannot retrieve realm list for region "
//                    + activity.db.getRegionNameFromURL(activity.mWoWRegionID));
//        } else {
        //response is ok, decode and store character JSON
        activity.mDatabase.setAvatar(image);
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
                    + activity.db.getRegionNameFromURL(activity.mWoWRegionID));
        } else {
            //response is ok, decode and store character JSON
            activity.mDatabase.realmFromJSON(str);
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
                        " Check spelling and that this character is on " + activity.mRealmID);
            }
        } else {
            //response is ok, decode and store character JSON
            activity.mDatabase.characterFromJson(str);
        }
    }


    public void onPostExecute(TaskResult result) {
        progressDialog.dismiss();

        System.out.println("REFRESHUI mode "+result.getMode());

        if (result.getMode() == CHARACTER) {
            System.out.println("DB CHARACTER CONTENTS" + activity.mDatabase.getCharacter().getName());
            //now we have character record, get it's images.
            GetFeedTask characterImageAsyncTask = new GetFeedTask(activity,GetFeedTask.CHARACTERIMAGE);
            characterImageAsyncTask.execute(GetFeedTask.CHARACTERIMAGE);

            System.out.println("GOT CHARACTER DATA");
            try {
                activity.mTeamFragment.invalidateTeamView(activity);
            } catch (NullPointerException e) {
                //fragment simply doesn't exist yet, not a problem
            }

            //   activity.mPlayerList.clear();
            //  activity.mPlayerList.addAll(activity.mDatabase.getTeam().getPlayers());
            MainActivity.mPlayerAdapter.notifyDataSetChanged();

        } else if (result.getMode() == NETWORKFAIL) {
            //Oh Google! Enforced statics in abstract classes make baby jesus cry!
            Toast.makeText(activity.getContext(), "Cannot contact server, please try again later",
                    Toast.LENGTH_LONG).show();
            activity.finish();
        } else if (result.getMode() == NODATA) {
            //Oh Google! Enforced statics in abstract classes make baby jesus cry!
            Toast.makeText(activity.getContext(), result.getResultCode(),
                    Toast.LENGTH_LONG).show();
            activity.mViewPager.setCurrentItem(0);
        } else if (result.getMode() == REALMLIST) {
            //update the serverlist ui only
            activity.mRealmList.clear();
            activity.mRealmList.addAll(activity.mDatabase.getRealms());
            MainActivity.mRealmAdapter.notifyDataSetChanged();
            System.out.println("GOT REALMLIST");
        } else if (result.getMode() == REALMLISTREFRESH) {
            //User changed region, update current serverlist and current server
            activity.mRealmList.clear();
            activity.mRealmList.addAll(activity.mDatabase.getRealms());
            MainActivity.mRealmAdapter.notifyDataSetChanged();
            activity.mRealmID = MainActivity.mDatabase.getRealms().get(0).getName();
            activity.realmListFragment.setServerTextDisplay();
            activity.prefsSave();
            System.out.println("GOT REALMLIST");
        } else if (result.getMode() == CHARACTERIMAGE) {
            //valid image was retrieved, update UI
            activity.realmListFragment.setAvatarDisplay();
            System.out.println("GOT CHARACTER IMAGE");
        }

    }
}