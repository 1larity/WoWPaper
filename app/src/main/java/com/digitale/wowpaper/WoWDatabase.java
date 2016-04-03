package com.digitale.wowpaper;

/**
 * Created by Rich on 02/04/2016.
 */

    import android.content.Context;
    import android.database.Cursor;
    import android.database.sqlite.SQLiteDatabase;
    import android.database.sqlite.SQLiteQueryBuilder;
    import android.util.Log;

    import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

    public class WoWDatabase extends SQLiteAssetHelper {

        private static final String DATABASE_NAME = "wow.db";
        private static final int DATABASE_VERSION = 2;

        public WoWDatabase(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            // you can use an alternate constructor to specify a database location
            // (such as a folder on the sd card)
            // you must ensure that this folder is available and you have permission
            // to write to it
            //super(context, DATABASE_NAME, context.getExternalFilesDir(null).getAbsolutePath(), null, DATABASE_VERSION);
            setForcedUpgrade();
        }

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

    }

