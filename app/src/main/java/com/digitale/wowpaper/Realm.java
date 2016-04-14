package com.digitale.wowpaper;

import android.provider.BaseColumns;

import java.util.ArrayList;

/**
 * Created by Rich on 03/04/2016.
 * container for realm data
 */
public class Realm {

    int _id;
    String type;
    String population;
    boolean queue;
    boolean status;
    String name;
    String slug;
    String battlegroup;
    String locale;
    String timezone;
    String[] connected_realms;
    String regionID;
    int favourite;
    /**
     * Blank Constructor
     */
    public Realm() {
//abstract class for referencing SQLite columns/table
    }
    public static abstract class RealmRecord implements BaseColumns {
        public static final String TABLE_NAME = "realm_status";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_TYPE = "type";
        public static final String COLUMN_NAME_POPULATION = "population";
        public static final String COLUMN_NAME_QUEUE = "queue";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_SLUG = "slug";
        public static final String COLUMN_NAME_BATTLEGROUP = "battlegroup";
        public static final String COLUMN_NAME_LOCALE = "locale";
        public static final String COLUMN_NAME_TIMEZONE = "timezone";
        public static final String COLUMN_NAME_REGIONID = "region_id";
        public static final String COLUMN_NAME_FAVOURITE = "favourite";
        public static final String COLUMN_NAME_NULLABLE = "";
    }
    public Realm(String type, String population, boolean queue, boolean status, String name, String slug, String battlegroup,
                 String locale, String timezone, String[] connected_realms,String regionID) {
        this.type = type;
        this.population = population;
        this.queue = queue;
        this.status = status;
        this.name = name;
        this.slug = slug;
        this.battlegroup = battlegroup;
        this.locale = locale;
        this.timezone = timezone;
        this.connected_realms = connected_realms;
        this.regionID=regionID;
    }


    //getters and setters

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public int getFavourite() {
        return favourite;
    }

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

    public String getRegionID() {
        return regionID;
    }

    public void setRegionID(String regionID) {
        this.regionID = regionID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPopulation() {
        return population;
    }

    public void setPopulation(String population) {
        this.population = population;
    }

    public boolean isQueue() {
        return queue;
    }

    public void setQueue(boolean queue) {
        this.queue = queue;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getBattlegroup() {
        return battlegroup;
    }

    public void setBattlegroup(String battlegroup) {
        this.battlegroup = battlegroup;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String[] getConnected_realms() {
        return connected_realms;
    }

    public void setConnected_realms(String[] connected_realms) {
        this.connected_realms = connected_realms;
    }
}
