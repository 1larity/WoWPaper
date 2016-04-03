package com.digitale.wowpaper;


import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rich on 15/03/2016.
 * container for match data
 */
public class MatchData {
    String matchDuration;
    String competition;
    String venue;
    int attendance;
    String referee;
    long matchtime;

    /**
     * Blank Constructor
     */
    public MatchData(){}

    public String getMatchDuration() {
        return matchDuration;
    }
    /**
     * Full Constructor
     */
    public MatchData(String time, String competition,
                     String venue,int attendance,
                     String referee, long kickoff){

         this.matchDuration=time;
         this.competition=competition;
         this.venue=venue;
         this.attendance=attendance;
         this.referee=referee;
         this.matchtime=kickoff;
    }

    public void setMatchDuration(String matchDuration) {
        this.matchDuration = matchDuration;
    }

    public String getCompetition() {
        return this.competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public String getReferee() {
        return referee;
    }

    public void setReferee(String referee) {
        this.referee = referee;
    }

    public long getMatchtime() {
        return matchtime;
    }

    public void setMatchtime(long matchtime) {
        this.matchtime = matchtime;
    }

    public String getMatchTimeAsString() {
        Date date = new Date(matchtime);
        SimpleDateFormat dateformat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
        return dateformat.format(date);
    }

}
