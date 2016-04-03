package com.digitale.wowpaper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Rich on 15/03/2016.
 * container for match data
 */
public class PlayStats {
    int score;
    int halfTimeScore;
    int possession;
    int shots;
    int shotsOnTarget;
    int corners;
    int fouls;

    /**
     * Blank Constructor
     */
    public PlayStats() {
    }

    /**
     * Full Constructor
     */
    public PlayStats(JSONObject jPlayStats) {
        try {
            this.score = jPlayStats.getInt("score");
            this.halfTimeScore = jPlayStats.getInt("halfTimeScore");
            this.possession = jPlayStats.getInt("possession");
            this.shots = jPlayStats.getInt("shots");
            this.shotsOnTarget = jPlayStats.getInt("shotsOnTarget");
            this.corners = jPlayStats.getInt("corners");
            this.fouls = jPlayStats.getInt("fouls");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
