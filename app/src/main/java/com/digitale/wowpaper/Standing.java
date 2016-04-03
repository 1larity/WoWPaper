package com.digitale.wowpaper;

/**
 * Created by Rich on 24/03/2016.
 * container for league standing (team) record
 */
class Standing {
    private int position;
    private String teamName;
    private int teamId;
    private int playedGames;
    private String crestURI;
    private int points;
    private int goals;
    private int goalsAgainst;
    private int goalDifference;
    private int wins;
    private int draws;
    private int losses;
    private TeamStats away;
    private TeamStats home;
    private Links _links;

    /**

     * Blank Constructor
     */
    public Standing() {
    }

    public Links get_links() {
        return _links;
    }

//    public void set_links(Links _links) {
//        this._links = _links;
//    }

    public TeamStats getAway() {
        return away;
    }

    public void setAway(TeamStats away) {
        this.away = away;
    }

    public TeamStats getHome() {
        return home;
    }

    public void setHome(TeamStats home) {
        this.home = home;
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
    public TeamStats getAwayStats() {
        return away;
    }

    public void setAwayStats(TeamStats awayStats) {
        this.away = awayStats;
    }

    public TeamStats getHomestats() {
        return home;
    }

    public void setHomestats(TeamStats homestats) {
        this.home = homestats;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public int getTeamId() {
        return teamId;
    }

    public void setTeamId(int teamId) {
        this.teamId = teamId;
    }

    public int getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(int playedGames) {
        this.playedGames = playedGames;
    }

    public String getCrestURI() {
        return crestURI;
    }

    public void setCrestURI(String crestURI) {
        this.crestURI = crestURI;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
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

    public int getGoalDifference() {
        return goalDifference;
    }

    public void setGoalDifference(int goalDifference) {
        this.goalDifference = goalDifference;
    }

}
