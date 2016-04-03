package com.digitale.wowpaper;


import android.os.AsyncTask;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Get JSON data from incrowd Website.
 * Needs to be run as seperate thread so UI thread doesn't get blocked.
 * params[0] The mode of the data fetch (SEASONS or STATS).
 * params[1] The URL of the data.
 */
class GetFeedTask extends AsyncTask<Integer, Void, TaskResult> {
    private static final int NODATA = -2;
    private static final int NETWORKFAIL = -1;
    public static final int CHARACTER = 2;
    public static final int  REALMLISTREFRESH=3;
    public static final int PLAYERS = 4;
    public static final int PLAYER = 5;
    public static final int REALMLIST = 1;
    public MainActivity activity;

    public GetFeedTask(MainActivity activity) {

        this.activity = activity;
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
        int mode = params[0];
        String getURL = "";
        //formulate urls depending on mode
        switch (mode) {

            case CHARACTER:
                getURL = activity.mWoWRegionID+"/wow/character/"+MainActivity.mRealmID+"/"+
                        activity.mCharacterName+
                        "?locale=en_GB&apikey="+MainActivity.API;
                System.out.println("getting character URL " + getURL);
                break;
            case PLAYERS:
                getURL = activity.getString(R.string.teamURL) + String.valueOf(activity.mTeamID)+"/players";
                System.out.println("getting players URL " + getURL);
                break;
            case REALMLIST:
                getURL = MainActivity.mWoWRegionID+"/wow/realm/status?locale=en_GB&apikey="+MainActivity.API;
                System.out.println("getting REALM LIST URL " + getURL);
                break;
            case REALMLISTREFRESH:
                getURL = MainActivity.mWoWRegionID+"/wow/realm/status?locale=en_GB&apikey="+MainActivity.API;
                System.out.println("getting REALM LIST URL " + getURL);
                break;
        }
        //nasty deprecated stuff I need for my phone
        //HttpURLConnection is v buggy Eclair/Froyo
        HttpResponse response;
        HttpClient myClient = new DefaultHttpClient();
        HttpGet get = new HttpGet(getURL);
        get.setHeader("X-Auth-Token", "4fa37bbe1e4741c6a5c5ff0cc8981df6");
        String str = null;

        try {
            if (!isCancelled()) {
                response = myClient.execute(get);
                str = EntityUtils.toString(response.getEntity(), "UTF-8");
            }
            // pass data to database for saving to internal data structures
            if (str != null && !(isCancelled())) {
                if (mode == CHARACTER){
                    System.out.println("Character DATA"+str);
                   activity.mDatabase.characterFromJson(str);
                }else if (mode == PLAYER){
//                    System.out.println("PLAYER DATA");
                //    activity.mDatabase.PlayerFromJson;
                }else if (mode == REALMLIST || mode==REALMLISTREFRESH) {
                    System.out.println("REALM DATA" + str);
                    activity.mDatabase.realmFromJSON(str);
                }
                results.setMode(mode);
            }
        } catch (ClientProtocolException e) {
            results.setMode(NETWORKFAIL);
            e.printStackTrace();
        } catch (IOException e) {
            results.setMode(NETWORKFAIL);
            e.printStackTrace();
        }
        return results;
    }


    public void onPostExecute(TaskResult result) {
        System.out.println("REFRESHUI");
       if(result.getMode()== CHARACTER){
            System.out.println("DB CHARACTER CONTENTS"+activity.mDatabase.getCharacter().getName());
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
            Toast.makeText(activity.getContext(), "Sorry, we don't have any more data for this.",
                    Toast.LENGTH_LONG).show();
            activity.mViewPager.setCurrentItem(0);
        }else if (result.getMode() == REALMLIST) {
            //update the serverlist ui only
            activity.mRealmList.clear();
            activity.mRealmList.addAll(activity.mDatabase.getRealms());
            MainActivity.mRealmAdapter.notifyDataSetChanged();
                  System.out.println("GOT REALMLIST");
        }else if (result.getMode() == REALMLISTREFRESH) {
           //User changed region, update current serverlist and current server
           activity.mRealmList.clear();
           activity.mRealmList.addAll(activity.mDatabase.getRealms());
           MainActivity.mRealmAdapter.notifyDataSetChanged();
           activity.mRealmID = MainActivity.mDatabase.getRealms().get(0).getName();
           activity.realmListFragment.setServerTextDisplay();
           activity.prefsSave();
           System.out.println("GOT REALMLIST");
       }
    }

}