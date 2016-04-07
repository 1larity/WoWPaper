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

    public class WoWDatabase extends SQLiteAssetHelper {

        private static final String DATABASE_NAME = "wow.db";
        private static final int DATABASE_VERSION = 4;
        private static final String TAG = "WOWDATABASE ";

        public WoWDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            // you can use an alternate constructor to specify a database location
            // (such as a folder on the sd card)
            // you must ensure that this folder is available and you have permission
            // to write to it
            //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
            setForcedUpgrade(4);



        }

        //get list of all API Regions
        public Cursor getRegions() {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            String [] sqlSelect = {"0 _id", "geo_zone_name", "url"};
            String sqlTables = "api_connection_details";
            qb.setTables(sqlTables);
            Cursor c = qb.query(db, sqlSelect, null, null,
                    null, null, null);
            c.moveToFirst();
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
        //get API image URL for a given region ID number
        public String getCurrentImageRegionURL(int regionID) {
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
        //get a character image URL using realm.bg,name,region key
        public String getCharacterImageURL(String charName,String realm,String battlegroup,int regionID) {
            SQLiteDatabase db = getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            //get character thumbnail URLs  column
            String [] sqlSelect = { "thumbnail"};
            //from list of characters
            String sqlTables = "wow_character";
            qb.setTables(sqlTables);
            //where character has matching realm.bg,name,region key
            Cursor c = qb.query(db, sqlSelect, WOWCharacter.CharacterRecord.COLUMN_NAME_NAME+"='"+charName+"' AND "+
                            WOWCharacter.CharacterRecord.COLUMN_NAME_REALM+"='"+realm+"' AND "+
                            WOWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP+"='"+battlegroup+"' AND "+
                            WOWCharacter.CharacterRecord.COLUMN_NAME_REGION+"='"+regionID+"'", null,
                    null, null, null);
            c.moveToFirst();
            return c.getString(0);
        }
        //adds character record to database
        public long insertCharacter(WOWCharacter character) {
            SQLiteDatabase wdb=getWritableDatabase();
            ContentValues characterValues = new ContentValues();
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_NAME, character.getName());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_LAST_MODIFIED, character.getLastModified());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_REALM, character.getRealm());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP, character.getBattlegroup());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_CLASS, character.getWowClass());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_RACE, character.getRace());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_GENDER, character.getGender());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_LEVEL, character.getLevel());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_ACHIEVEMENT_POINTS, character.getAchievementPoints());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_THUMBNAIL, character.getThumbnail());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_CALC_CLASS, character.getCalcClass());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_FACTION, character.getFaction());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_TOTAL_HONORABLE_KILLS, character.getTotalHonorableKills());
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_REGION, character.getRegion());
            // Insert the new row, returning the primary key value of the new row
            long newRowId = 0;
            try {
                //insert character into table or update pre-existing
                newRowId = wdb.insertWithOnConflict(
                        WOWCharacter.CharacterRecord.TABLE_NAME,
                        WOWCharacter.CharacterRecord.COLUMN_NAME_NULLABLE,
                        characterValues, SQLiteDatabase.CONFLICT_IGNORE);
                if (newRowId == -1) {
                    Log.d(TAG,"Character Exists, Updating");
                    //conflict exists, update row where character realm,name,battlegroup and region match
                    wdb.update( WOWCharacter.CharacterRecord.TABLE_NAME, characterValues,
                            WOWCharacter.CharacterRecord.COLUMN_NAME_REALM+"=? AND "+
                                    WOWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP+"=? AND "+
                                    WOWCharacter.CharacterRecord.COLUMN_NAME_NAME+"=? AND "+
                                    WOWCharacter.CharacterRecord.COLUMN_NAME_REGION+"=?",
                            new String[] {character.getRealm(),character.getBattlegroup(),character.getName(),String.valueOf(character.getRegion())});
                }
            } catch (SQLiteConstraintException e) {
                     newRowId = -1;
            }
            wdb.close();
            return newRowId;
        }
        //adds realm record to database
        public long insertRealm(Realm realm) {
            SQLiteDatabase wdb=getWritableDatabase();
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
                    Log.d(TAG,"Realm Exists, Updating");
                    //conflict exists, update row where realm name and realm region match
                    newRowId =   wdb.update( Realm.RealmRecord.TABLE_NAME, realmValues,
                            "name=? AND region_id=?",
                            new String[] {realm.getName(),realm.getRegionID()});
                }

            } catch (SQLiteConstraintException e) {
                           newRowId = -1;
            }
            wdb.close();
            return newRowId;
        }
        //adds character record to database
        public long updateCharacterAvatar(WOWCharacter character, byte[] avatar) {
            SQLiteDatabase wdb=getWritableDatabase();
            ContentValues characterValues = new ContentValues();
            characterValues.put(WOWCharacter.CharacterRecord.COLUMN_NAME_AVATAR, avatar);

            // Insert the new row, returning the primary key value of the new row
            long newRowId = 0;
            try  {
                    Log.d(TAG,"Character Exists, Updating Avatar");
                    //conflict exists, update row where character realm,name,battlegroup and region match
                    wdb.update( WOWCharacter.CharacterRecord.TABLE_NAME, characterValues,
                            WOWCharacter.CharacterRecord.COLUMN_NAME_REALM+"=? AND "+
                                    WOWCharacter.CharacterRecord.COLUMN_NAME_BATTLEGROUP+"=? AND "+
                                    WOWCharacter.CharacterRecord.COLUMN_NAME_NAME+"=? AND "+
                                    WOWCharacter.CharacterRecord.COLUMN_NAME_REGION+"=?",
                            new String[] {character.getRealm(),character.getBattlegroup(),character.getName(),String.valueOf(character.getRegion())});

            } catch (SQLiteConstraintException e) {
                newRowId = -1;
            }
            wdb.close();
            return newRowId;
        }
    }

