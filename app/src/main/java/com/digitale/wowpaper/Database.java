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
    private static final String TAG ="JSONDATABASE" ;
    private static final boolean DEBUG = false;
    public ArrayList<Realm> realms = new ArrayList<>();
    public WOWCharacter character=new WOWCharacter();



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
            if(DEBUG){
                Log.i(TAG, "" + jRealms);
            }
            this.realms = new Gson().fromJson(String.valueOf(jRealms), type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void characterFromJson(String data) {
        java.lang.reflect.Type type =
                new com.google.gson.reflect.TypeToken<WOWCharacter>() {
                }.getType();
        if(DEBUG){
            Log.i(TAG, "Character" + data);
        }
        this.character = new Gson().fromJson(data, type);
    }
}
