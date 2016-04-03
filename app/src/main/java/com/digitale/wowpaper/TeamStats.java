package com.digitale.wowpaper;

/**
 * Created by Rich on 24/03/2016.
 * container for team home/away statistics
 */
class TeamStats {
    private int goals;
    private int goalsAgainst;
    private int wins;
    private int draws;
    private int losses;

    /**
     * Blank Constructor
     */
    public TeamStats() {
    }

    public int getGoals() {
        return goals;
    }

    public void setGoals(int goals) {
        this.goals = goals;
    }

    public int getGoalsAgainst() {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst) {
        this.goalsAgainst = goalsAgainst;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getDraws() {
        return draws;
    }

    public void setDraws(int draws) {
        this.draws = draws;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

}
