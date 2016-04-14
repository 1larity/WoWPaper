package com.digitale.wowpaper;

/**
 * delegate
 * Created by Rich on 12/04/2016.
 */

public interface AsyncResponse {
    void processFinish(int output, Object data);
}