package com.digitale.wowpaper;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Rich on 15/03/2016.
 * container for season record
 */
class Database {
    private static final String TAG = "JSONDATABASE";
    private static final boolean DEBUG = false;
    public ArrayList<Realm> realms = new ArrayList<>();
    public WOWCharacter character = new WOWCharacter();


    /**
     * Blank Constructor
     */
    public Database() {

    }


    public ArrayList<Realm> getRealms() {
        return realms;
    }

    public void setRealms(ArrayList<Realm> realms) {
        this.realms = realms;
    }


    public WOWCharacter getCharacter() {
        return character;
    }

    public void setCharacter(WOWCharacter character) {
        this.character = character;
    }

    public void realmFromJSON(String data) {
        JSONArray jRealms;
        try {
            JSONObject jRealmData = new JSONObject(data);
            jRealms = jRealmData.getJSONArray("realms");
            java.lang.reflect.Type type =
                    new com.google.gson.reflect.TypeToken<ArrayList<Realm>>() {
                    }.getType();
            if (DEBUG) {
                Log.i(TAG, "" + jRealms);
            }
            this.realms = new Gson().fromJson(String.valueOf(jRealms), type);
            //insert realms into database
            SQLiteDatabase wdb=MainActivity.db.getWritableDatabase();
            for(Realm currentRealm:this.realms){
                Log.d(TAG,"inserting realm "+currentRealm.getName()+" into database");
                //set the region this server is in to maintain realm>region relationship
                currentRealm.setRegionID(MainActivity.mWoWRegionID);
                long ID = MainActivity.db.insertRealm(currentRealm);
            }
            wdb.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public long characterFromJson(String data) {
        long result;

        java.lang.reflect.Type type =
                new com.google.gson.reflect.TypeToken<WOWCharacter>() {
                }.getType();

        this.character = new Gson().fromJson(data, type);
        //set the reion of this character to maintain character>region relationship
        this.character.setRegion(MainActivity.db.getRegionIDFromURL(MainActivity.mWoWRegionID));
        //insert character into database
        long ID = MainActivity.db.insertCharacter(this.character);
        result = ID;
        if (DEBUG) {
            Log.i(TAG, "Character" + ID + " " + data);

        }
        return result;
    }

    /**
     * sets the current charater avatar image and store in SQLite
     * @param characterAvatar
     */
    public void setAvatar(byte[] characterAvatar) {
        this.character.setAvatar(characterAvatar);
        //update character avatar image
        long ID = MainActivity.db.updateCharacterAvatar(this.character,characterAvatar);
    }
}
