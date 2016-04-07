package com.digitale.wowpaper;

import android.provider.BaseColumns;

/**
 * Created by Rich on 02/04/2016.
 */
public final class WoWCharacter {
    int _id;
    long lastModified;
    String name;
    String realm;
    String battlegroup;
    int wowClass;
    int race;
    int gender;
    int level;
    int achievementPoints;
    String thumbnail;
    String calcClass;
    int faction;
    int totalHonorableKills;
    int region_id;
    byte[] avatar;
    byte[] profilemain;
    //abstract class for referencing SQLite columns/table
    //makes it easy to change underlying tables
    public static abstract class CharacterRecord implements BaseColumns {
        public static final String TABLE_NAME = "wow_character";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_LAST_MODIFIED = "lastModified";
        public static final String COLUMN_NAME_REALM = "realm";
        public static final String COLUMN_NAME_BATTLEGROUP = "battlegroup";
        public static final String COLUMN_NAME_CLASS = "class";
        public static final String COLUMN_NAME_RACE = "race";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_LEVEL = "level";
        public static final String COLUMN_NAME_ACHIEVEMENT_POINTS = "achievementPoints";
        public static final String COLUMN_NAME_THUMBNAIL = "thumbnail";
        public static final String COLUMN_NAME_CALC_CLASS = "calcClass";
        public static final String COLUMN_NAME_FACTION = "faction";
        public static final String COLUMN_NAME_TOTAL_HONORABLE_KILLS = "totalHonorableKills";
        public static final String COLUMN_NAME_REGION = "region_id";
        public static final String COLUMN_NAME_AVATAR = "avatar";
        public static final String COLUMN_NAME_PROFILE = "profilemain";
        public static final String COLUMN_NAME_NULLABLE = "";
    }
    public WoWCharacter(){}
    public int getRegion() {
        return region_id;
    }

    public void setRegion(int region) {
        this.region_id = region;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public byte[] getProfilemain() {
        return profilemain;
    }

    public void setProfilemain(byte[] profilemain) {
        this.profilemain = profilemain;
    }
    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRealm() {
        return realm;
    }

    public void setRealm(String realm) {
        this.realm = realm;
    }

    public String getBattlegroup() {
        return battlegroup;
    }

    public void setBattlegroup(String battlegroup) {
        this.battlegroup = battlegroup;
    }

    public int getWowClass() {
        return wowClass;
    }

    public void setWowClass(int wowClass) {
        this.wowClass = wowClass;
    }

    public int getRace() {
        return race;
    }

    public void setRace(int race) {
        this.race = race;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAchievementPoints() {
        return achievementPoints;
    }

    public void setAchievementPoints(int achievementPoints) {
        this.achievementPoints = achievementPoints;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getCalcClass() {
        return calcClass;
    }

    public void setCalcClass(String calcClass) {
        this.calcClass = calcClass;
    }

    public int getFaction() {
        return faction;
    }

    public void setFaction(int faction) {
        this.faction = faction;
    }

    public int getTotalHonorableKills() {
        return totalHonorableKills;
    }

    public void setTotalHonorableKills(int totalHonorableKills) {
        this.totalHonorableKills = totalHonorableKills;
    }

}
