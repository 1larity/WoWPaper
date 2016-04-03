package com.digitale.wowpaper;

import java.util.ArrayList;

/**
 * Created by Rich on 15/03/2016.
 * container for league data
 */
class League {

    private int matchday;
    private String name;
    public ArrayList <Standing> standings;

    /**
     * Blank Constructor
     */
    public League() {
    }
    public League(String name, int matchday) {
        this.matchday=matchday;
        this.name=name;
    }
    public ArrayList<Standing> getStandings() {
        return standings;
    }

    public void setStandings(ArrayList<Standing> standings) {
        this.standings = standings;
    }
    public int getMatchday() {
        return matchday;
    }

    public void setMatchday(int matchday) {
        this.matchday = matchday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
