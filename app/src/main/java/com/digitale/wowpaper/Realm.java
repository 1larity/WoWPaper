package com.digitale.wowpaper;

import java.util.ArrayList;

/**
 * Created by Rich on 03/04/2016.
 * container for realm data
 */
public class Realm {
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

    /**
     * Blank Constructor
     */
    public Realm() {

    }

    public Realm(String type, String population, boolean queue, boolean status, String name, String slug, String battlegroup,
                 String locale, String timezone, String[] connected_realms) {
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
    }


    //getters and setters
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
