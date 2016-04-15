package com.digitale.wowpaper;

/**
 * SQLite helper to assist accessing DB
 * implements methods for installing and upgrading pre-built
 * databases
 * Created by Rich on 02/04/2016.
 */

    import android.content.ContentValues;
    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteConstraintException;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteQueryBuilder;
    import android.util.Log;

    import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

    import java.util.ArrayList;

public class WoWDatabase extends SQLiteAssetHelper {

        private static final String DATABASE_NAME = "wow.db";
        private static final int DATABASE_VERSION = 5;
        private static final String TAG = "WOWDATABASE ";
    private static final long WAITDURATION =500 ;
    //used to control write locking the DB
    private boolean mWriteLock;
    private boolean localDebug=true;

    public WoWDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            // you can use an alternate constructor to specify a database location
            // (such as a folder on the sd card)
            // you must ensure that this folder is available and you have permission
            // to write to it
            //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
            setForcedUpgrade(5);



        }

        //get list of all API Regions
        public Cursor getRegions() {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String [] sqlSelect = {"_id", "geo_zone_name", "url"};
            String sqlTables = "api_connection_details";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, sqlSelect, null, null,
                    null, null, null);
            c.moveToFirst();
            return c;

        }
    //get list of all characters
    public Cursor getTableContents(String tablename, String whereClause) {
        writeLock();
        Logger.writeLog(TAG,"Get Table Contents for "+tablename,localDebug);
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(tablename);
        Cursor c = qb.query(db, null, whereClause, null,
                null, null, null);

        c.moveToFirst();
        if(localDebug){
            while (!c.isAfterLast()) {
                Logger.writeLog(TAG,"Row "+c.getPosition()+" ID "+c.getString(c.getColumnIndex("_id"))+ "field1 "+
                        c.getString(c.getColumnIndex("name")),localDebug);
                c.moveToNext();
            }
        }
        mWriteLock=false;
        return c;
    }
        //get API URL for a given region ID number
        public String getCurrentRegionURL(int position) {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String [] sqlSelect = { "url"};
            String sqlTables = "api_connection_details";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, sqlSelect, null, null,
                    null, null, null);
            c.moveToPosition(position);
            return c.getString(0);
        }
        //get index number of region when supplied the url for that API
        public int getRegionIDFromURL(String regionURL){
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String [] sqlSelect = { "_id"};
            String sqlTables = "api_connection_details";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, sqlSelect, "url='"+regionURL+"'", null,
                    null, null, null);
            c.moveToFirst();
            return c.getInt(0);
        }
    //get get region URL when supplied with regionID
    public String getRegionURLfromID(int _id){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] sqlSelect = { "url"};
        String sqlTables = "api_connection_details";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, "_id='"+_id+"'", null,
                null, null, null);
        c.moveToFirst();
        return c.getString(0);
    }
        //get friendly name of region when supplied the url for that API
        public String getRegionNameFromURL(String regionURL){
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String [] sqlSelect = { "geo_zone_name"};
            String sqlTables = "api_connection_details";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, sqlSelect, "url='"+regionURL+"'", null,
                    null, null, null);
            c.moveToFirst();
            return c.getString(0);
        }
    //get friendly name of region when supplied the ID for that API
    public String getRegionNameFromID(int regionID){
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String [] sqlSelect = { "geo_zone_name"};
        String sqlTables = "api_connection_details";
        qb.setTables(sqlTables);
        Cursor c = qb.query(db, sqlSelect, "_id='"+regionID+"'", null,
                null, null, null);
        c.moveToFirst();
        return c.getString(0);
    }
        //get API image URL for a given region ID number
        public String getCurrentImageRegionURL(int regionID) {
            Logger.writeLog(TAG,"Attempting to get region URL for ID "+regionID,localDebug);
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            //get URl COLUMN
            String [] sqlSelect = { "url"};
            //from list of API endpoints
            String sqlTables = "api_render_connection_details";
            qb.setTables(sqlTables);
            //where record id=requested id
            Cursor c = qb.query(db, sqlSelect, "_id='"+regionID+"'", null,
                    null, null, null);
            c.moveToFirst();
            return c.getString(0);
        }
    //Delete for a given ID number
    public void deleteRecord(long _id,String tableName) {
        Logger.writeLog(TAG,"Attempting delete on ID="+_id+" tablename "+tableName,localDebug);
        writeLock();
        SQLiteDatabase wdb =getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String sqlTables = tableName;
        qb.setTables(sqlTables);
        //where record id=requested id
        wdb.delete(tableName,"_id='"+_id+"'",null);
        wdb.close();
        mWriteLock=false;

    }
        //get a character image URL using realm.bg,name,region key
        public String getCharacterImageURL(long _id) {
            Logger.writeLog(TAG,"Getting Image URL for character ID"+_id,localDebug);
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            //get character thumbnail URLs  column
            String [] sqlSelect = { "thumbnail"};
            //from list of characters
            String sqlTables = "wow_character";
            qb.setTables(sqlTables);
            //where character has matching realm.bg,name,region key
            Cursor c = qb.query(db, sqlSelect, WoWCharacter.CharacterRecord.COLUMN_NAME_ID+"='"+_id+"'" , null,
                    null, null, null);

            c.moveToFirst();
            return c.getString(0);
        }
        //adds character record to database
        public long insertCharacter(WoWCharacter character) {
            writeLock();
            SQLiteDatabase wdb=getWritableDatabase();
            ContentValues characterValues = new ContentValues();
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_NAME, character.getName());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_LAST_MODIFIED, character.getLastModified());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_REALM, character.getRealm());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP, character.getBattlegroup());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_CLASS, character.getWowClass());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_RACE, character.getRace());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_GENDER, character.getGender());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_LEVEL, character.getLevel());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_ACHIEVEMENT_POINTS, character.getAchievementPoints());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_THUMBNAIL, character.getThumbnail());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_CALC_CLASS, character.getCalcClass());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_FACTION, character.getFaction());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_TOTAL_HONORABLE_KILLS, character.getTotalHonorableKills());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_REGION, character.getRegion_id());
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_FAVOURITE, character.getFavourite());
            // Insert the new row, returning the primary key value of the new row
            long newRowId = 0;
            try {
                //insert character into table or update pre-existing
                newRowId = wdb.insertWithOnConflict(
                        WoWCharacter.CharacterRecord.TABLE_NAME,
                        WoWCharacter.CharacterRecord.COLUMN_NAME_NULLABLE,
                        characterValues, SQLiteDatabase.CONFLICT_IGNORE);
                if (newRowId == -1) {
                    Log.d(TAG,"Character Exists, Updating");
                    //columns
                    String [] sqlSelect = { WoWCharacter.CharacterRecord.COLUMN_NAME_ID};
                    //where values
                    String[] whereVal=new String[] {character.getRealm(),character.getBattlegroup(),character.getName(),String.valueOf(character.getRegion_id())};
                    //where clause
                    String whereClause=WoWCharacter.CharacterRecord.COLUMN_NAME_REALM+"=? AND "+
                            WoWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP+"=? AND "+
                            WoWCharacter.CharacterRecord.COLUMN_NAME_NAME+"=? AND "+
                            WoWCharacter.CharacterRecord.COLUMN_NAME_REGION+"=?";
                    //conflict exists, update row where character realm,name,battlegroup and region match
                    wdb.update( WoWCharacter.CharacterRecord.TABLE_NAME, characterValues,whereClause,whereVal);
                //get ID of updated record
                    SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
                    qb.setTables(WoWCharacter.CharacterRecord.TABLE_NAME);
                    Cursor c=qb.query(wdb,sqlSelect,whereClause, whereVal,null,null,null);
                    c.moveToFirst();
                    newRowId=c.getLong(0);
                }
            } catch (SQLiteConstraintException e) {
                     newRowId = -1;
            }
            wdb.close();
            mWriteLock=false;
            return newRowId;
        }
        //adds realm record to database
        public long insertRealms(ArrayList<Realm> realms) {
            writeLock();

            SQLiteDatabase wdb=getWritableDatabase();
            long result=0;
            int insertedCount=0;
            int updatedCount=0;
            for(Realm realm:realms) {
                ContentValues realmValues = new ContentValues();
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_TYPE, realm.getType());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_POPULATION, realm.getPopulation());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_QUEUE, realm.isQueue());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_STATUS, realm.isStatus());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_NAME, realm.getName());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_SLUG, realm.getSlug());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_BATTLEGROUP, realm.getBattlegroup());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_LOCALE, realm.getLocale());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_TIMEZONE, realm.getTimezone());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_REGIONID, realm.getRegionID());
                realmValues.put(Realm.RealmRecord.COLUMN_NAME_FAVOURITE, realm.getFavourite());
                // Insert the new row, returning the primary key value of the new row
                long newRowId = 0;
                try {
                    //insert realm into table or update pre-existing
                    newRowId = wdb.insertWithOnConflict(
                            Realm.RealmRecord.TABLE_NAME,
                            Realm.RealmRecord.COLUMN_NAME_NULLABLE,
                            realmValues, SQLiteDatabase.CONFLICT_IGNORE);
                    //insert returns -1 if row alreday exists
                    if (newRowId == -1) {
                        Logger.writeLog(TAG, "Realm "+realmValues.get(Realm.RealmRecord.COLUMN_NAME_NAME)+" exists, updating",localDebug);
                        //conflict exists, update row where realm name and realm region match
                        newRowId = wdb.update(Realm.RealmRecord.TABLE_NAME, realmValues,
                                "name=? AND region_id=?",
                                new String[]{realm.getName(), realm.getRegionID()});
                        updatedCount=updatedCount+1;
                    }else{
                        Logger.writeLog(TAG, "Realm "+realmValues.get(Realm.RealmRecord.COLUMN_NAME_NAME)+
                                " does not exist, inserted at row"+newRowId,localDebug);
                        insertedCount=insertedCount+1;
                    }

                } catch (SQLiteConstraintException e) {
                    newRowId = -1;
                }
                result=newRowId;
            }
            wdb.close();
            Log.d(TAG, "Realms updated=" + updatedCount);
            Log.d(TAG, "Realms inserted="+ insertedCount);
            mWriteLock=false;
            return result;
        }

    private void writeLock() {
        // hold processing here until lock is freed
        if(mWriteLock){
            try {
                Log.d(TAG,"Waiting for lock");
                Thread.sleep(WAITDURATION);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mWriteLock=true;
    }

    //Update character record to new avatar
        public long updateCharacterAvatar(WoWCharacter character, byte[] avatar) {
            writeLock();
            SQLiteDatabase wdb=getWritableDatabase();
            ContentValues characterValues = new ContentValues();
            characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_AVATAR, avatar);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = 0;
            try  {
                    Log.d(TAG,"Character Exists, Updating Avatar");
                    //conflict exists, update row where character realm,name,battlegroup and region match
                newRowId =  wdb.update( WoWCharacter.CharacterRecord.TABLE_NAME, characterValues,
                            WoWCharacter.CharacterRecord.COLUMN_NAME_REALM+"=? AND "+
                                    WoWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP+"=? AND "+
                                    WoWCharacter.CharacterRecord.COLUMN_NAME_NAME+"=? AND "+
                                    WoWCharacter.CharacterRecord.COLUMN_NAME_REGION+"=?",
                            new String[] {character.getRealm(),character.getBattlegroup(),character.getName(),String.valueOf(character.getRegion_id())});

            } catch (SQLiteConstraintException e) {
                newRowId = -1;
            }
            wdb.close();
            Log.d(TAG, "Avatar update complete RowID=" + newRowId);
            mWriteLock=false;
            return newRowId;
        }

    //Update character record to new profile image
    public long updateCharacterProfilemain(WoWCharacter character, byte[] profilemain) {
        writeLock();
        SQLiteDatabase wdb=getWritableDatabase();
        ContentValues characterValues = new ContentValues();
        characterValues.put(WoWCharacter.CharacterRecord.COLUMN_NAME_PROFILE, profilemain);

        // Update the row, returning the primary key value of the new row
        long newRowId = 0;
        try  {
            Log.d(TAG,"Character Exists, Updating Profilemain");
            String sqlString= WoWCharacter.CharacterRecord.COLUMN_NAME_REALM+"=? AND "+
                    WoWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP+"=? AND "+
                    WoWCharacter.CharacterRecord.COLUMN_NAME_NAME+"=? AND "+
                    WoWCharacter.CharacterRecord.COLUMN_NAME_REGION+"=?";
            Log.d(TAG,"where "+sqlString);
            Logger.writeLog(TAG,"Realm "+character.getRealm()+" BG " +character.getBattlegroup()+
                    " Name"+ character.getName()+" region "+character.getRegion_id(),localDebug);
            //conflict exists, update row where character realm,name,battlegroup and region match
            newRowId =  wdb.update( WoWCharacter.CharacterRecord.TABLE_NAME, characterValues,
                    WoWCharacter.CharacterRecord.COLUMN_NAME_REALM+"=? AND "+
                            WoWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP+"=? AND "+
                            WoWCharacter.CharacterRecord.COLUMN_NAME_NAME+"=? AND "+
                            WoWCharacter.CharacterRecord.COLUMN_NAME_REGION+"=?",
                    new String[] {character.getRealm(),character.getBattlegroup(),character.getName(),String.valueOf(character.getRegion_id())});

        } catch (SQLiteConstraintException e) {
            newRowId = -1;
        }
        wdb.close();
        Log.d(TAG,"Profile update complete RowID="+newRowId);
        mWriteLock=false;
        return newRowId;
    }
    //Update realm record to new favourite status
    public long updateRealmFavourite(Realm realm) {
        writeLock();
        SQLiteDatabase wdb=getWritableDatabase();
        ContentValues realmValues = new ContentValues();
        realmValues.put(Realm.RealmRecord.COLUMN_NAME_FAVOURITE, realm.getFavourite());

        // Update the row, returning the primary key value of the new row
        long newRowId = 0;
        try  {
            Log.d(TAG,"Realm Exists, updating favourite");
            String sqlString= Realm.RealmRecord.COLUMN_NAME_ID+"=?";
            Log.d(TAG,"where "+sqlString);
            Logger.writeLog(TAG,"Realm ID"+realm.get_id(),localDebug);
            //conflict exists, update row where realm ID match
            newRowId =  wdb.update( Realm.RealmRecord.TABLE_NAME, realmValues,
                    Realm.RealmRecord.COLUMN_NAME_ID+"=?",
                    new String[] {String.valueOf(realm.get_id())});

        } catch (SQLiteConstraintException e) {
            newRowId = -1;
        }
        wdb.close();
        Log.d(TAG,"Realm update complete RowID="+newRowId);
        mWriteLock=false;
        return newRowId;
    }
    }

