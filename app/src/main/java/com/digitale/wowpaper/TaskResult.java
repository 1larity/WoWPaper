package com.digitale.wowpaper;

/**
 * Created by Rich on 13/03/2016.
 * container for commentary and stats data
 */
public class TaskResult {
    int mode;
    String resultCode;
    /**
     * Blank Constructor
     */
    public TaskResult() {
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public int getMode() {

        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

}
