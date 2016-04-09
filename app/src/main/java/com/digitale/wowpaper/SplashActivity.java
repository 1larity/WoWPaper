package com.digitale.wowpaper;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;


/**
 * basic Splash screen activity displays for a pre determined length of time.
 * Created by Rich on 01/07/2012.
 */
public class SplashActivity extends Activity {

    static Context mActivity;

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mActivity = this;
        String value = "";
        setContentView(R.layout.splash);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            value = extras.getString("SKIN");
        }

        ImageView bgImage = (ImageView) findViewById(R.id.splashBG);
        assert value != null;
        switch (value) {
            case "1":
                bgImage.setImageResource(R.drawable.horde_bg);
                break;
            case "2":
                bgImage.setImageResource(R.drawable.alliance_bg);
                break;
            default:
                bgImage.setImageResource(R.drawable.bg_load_screen);
                break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                SplashActivity.this.finish();
            }
        }, 3000);
    }

    public static Context getContext(){
        return mActivity.getApplicationContext();
    }
}
