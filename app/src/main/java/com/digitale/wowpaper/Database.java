package com.digitale.wowpaper;

import android.util.Log;

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
    public WoWCharacter character = new WoWCharacter();
    public ArrayList<WoWCharacter> characters = new ArrayList<>();
    private boolean localDebug=true;


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


    public WoWCharacter getCharacter() {
        return character;
    }

    public void setCharacter(WoWCharacter character) {
        this.character = character;
    }

    public ArrayList<Realm> realmFromJSON(String data) {
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
            for(Realm currentRealm:this.realms){
                //set region ID and favorite status of realms
                //set the region this server is in to maintain realm>region relationship
                currentRealm.setRegionID(MainActivity.mWoWRegionID);
                currentRealm.setFavourite(0);
            }
            Log.d(TAG,"inserting realms into database");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.realms;
    }

    /**
     * Convert data string to objects of WoWCharacter type with GSON library
     *
     * @param data String of JSON objects representing WoW character traits.
     * @return result Long ID of the inserted row.
     */
    public long characterFromJson(String data) {
        long result;

        java.lang.reflect.Type type =
                new com.google.gson.reflect.TypeToken<WoWCharacter>() {
                }.getType();

        this.character = new Gson().fromJson(data, type);
        //set the region of this character to maintain character>region relationship
        Logger.writeLog(TAG,"writing region ID("+MainActivity.PrefsDB.getRegionIDFromURL(MainActivity.mWoWRegionID)
                +") to character "+this.character.getName(),localDebug);
        this.character.setRegion_id(MainActivity.PrefsDB.getRegionIDFromURL(MainActivity.mWoWRegionID));
        this.character.setFavourite(0);
        //insert character into database
        long ID = MainActivity.PrefsDB.insertCharacter(this.character);
        result = ID;
        if (DEBUG) {
            Log.i(TAG, "Character" + ID + " " + data);

        }
        if (result>=0){
            this.character.set_id(result);
        }
        return result;
    }

    /**
     * sets the current character avatar image and store in SQLite PrefsDB
     * @param characterAvatar
     */
    public void setAvatar(byte[] characterAvatar,WoWCharacter currentCharacter) {
        Log.d(TAG,"Commiting avatar data (size)"+characterAvatar.length);
        this.character.setAvatar(characterAvatar);
        //update character avatar image
        long ID = MainActivity.PrefsDB.updateCharacterAvatar(currentCharacter, characterAvatar);

    }
    /**
     * sets the current character profile image and store in SQLite PrefsDB
     * @param characterProfile
     */
    public void setProfileImage(byte[] characterProfile,WoWCharacter currentCharacter) {
        Logger.writeLog(TAG,"Commiting profile data (size)"+characterProfile.length,localDebug);
        this.character.setProfilemain(characterProfile);
        //update character avatar image
        long ID = MainActivity.PrefsDB.updateCharacterProfilemain(currentCharacter, characterProfile);
    }
}
