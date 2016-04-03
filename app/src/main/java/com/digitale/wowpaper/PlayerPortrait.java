package com.digitale.wowpaper;

import android.graphics.Bitmap;

/**
 * Custom type for containing bitmaps for player portraits and identification string.
 * Created by Rich on 18/03/2016.
 */
public class PlayerPortrait {
    Bitmap bitmap;
    String name;

    public PlayerPortrait(Bitmap bitmap, String name) {
        this.bitmap=bitmap;
        this.name=name;
    }
}
