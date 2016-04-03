package com.digitale.wowpaper;

/**
 * Created by Rich on 13/03/2016.
 * Container for commentary record for use in ArrayLists
 */
class CommentaryListItem  {
    private String heading;
    private String description;
    private String time;
    private String type;
    CommentaryListItem(String heading, String description, String time,String type) {
        this.heading = heading;
        this.description = description;
        this.time = time;
        this.type = type;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public String getHeading() {
        return heading;
    }

    public String getDescription() {
        return description;
    }
    public String getTime() {
        return time;
    }
}
