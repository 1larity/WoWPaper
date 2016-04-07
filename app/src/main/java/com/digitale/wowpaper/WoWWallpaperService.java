package com.digitale.wowpaper;

import android.content.Context;
import android.gesture.GestureOverlayView;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Movie;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by Rich on 05/04/2016.
 */
public class WoWWallpaperService extends WallpaperService {
    private static final String TAG = "WALLPAPER";
    float mWallpaperXStep;
    float mWallpaperYStep;
    static float mXOffset;
    int pictureX;
    int pictureY;
    static int  screenX;
    static int  screenY;
    View.OnTouchListener gestureListener;
    GestureDetector mGestureDetector;
    //number of launcher pages(needed for scrolling wallpaper with launcher)
    static int mNumberOfPages =3 ;
    @Override
    public WallpaperService.Engine onCreateEngine() {
        // Create an object of our Custom Gesture Detector Class
        CustomGestureDetector customGestureDetector = new CustomGestureDetector();
        // Create a GestureDetector
        mGestureDetector = new GestureDetector(this, customGestureDetector);
        // Attach listeners that'll be called for double-tap and related gestures
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return mGestureDetector.onTouchEvent(event);
            }
        };
        DisplayMetrics metrics = new DisplayMetrics();
         screenX=metrics.widthPixels;
         screenY=metrics.heightPixels;

        try {
            Movie movie = Movie.decodeStream(
                    getResources().getAssets().open("leaf.gif"));
            pictureX=movie.width();
            pictureY=movie.height();
            return new WoWWallpaperEngine(movie);
        }catch(IOException e){
            Log.d("GIF", "Could not load asset");
            return null;
        }
    }

    private class WoWWallpaperEngine extends WallpaperService.Engine {
        private final int frameDuration = 100;

        private SurfaceHolder holder;
        private Movie movie;
        private boolean visible;
        private Handler handler;
        private boolean isOnOffsetsChangedWorking = false;
        DisplayMetrics metrics = new DisplayMetrics();
        int screenX=metrics.widthPixels;
        int screenY=metrics.heightPixels;

        @Override
        public void onOffsetsChanged (float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
          //calculate number of launcher pages
           mNumberOfPages=(int)(1 / xOffsetStep)+1;
            Log.d(TAG,"Pages= "+ mNumberOfPages);
            if (isOnOffsetsChangedWorking == false && xOffset != 0.0f && xOffset != 0.5f) {
                isOnOffsetsChangedWorking = true;
            }

            if (isOnOffsetsChangedWorking == true) {
                Log.d(TAG,"Offset method works! "+xOffset+" "+xPixelOffset+" "+xOffsetStep);
                mXOffset=xPixelOffset;
            }
        }
        //awful hack because HTC and Samsung decided to do their own thing with the launcher
        @Override public void onTouchEvent(MotionEvent event) {
            if (!isOnOffsetsChangedWorking) {
                mGestureDetector.onTouchEvent(event);
            }
        }

        public WoWWallpaperEngine(Movie movie) {
            this.movie = movie;
            handler = new Handler();
        }

        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;

        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d(TAG,"VISIBLE="+visible);
            this.visible = visible;
            if (visible) {
                handler.post(drawGIF);
            } else {
                handler.removeCallbacks(drawGIF);
            }
        }
        private Runnable drawGIF = new Runnable() {
            Canvas canvas;
            public void run() {
                draw(canvas);
            }
        };
        private void draw(Canvas canvas) {
            if (visible) {
                 canvas = holder.lockCanvas();

                canvas.save();
                //calculate best fit scale for image while retaining aspect ratio
                Matrix m = canvas.getMatrix();

                RectF drawableRect = new RectF(0, 0, pictureX, pictureY);
                RectF viewRect = new RectF(0, 0,canvas.getWidth()*mNumberOfPages, canvas.getHeight());
                m.setRectToRect(drawableRect, viewRect, Matrix.ScaleToFit.CENTER);
                //scale the canvas
                canvas.setMatrix(m);
           //    Log.d(TAG, "mxoffset " + mXOffset +" h "+canvas.getHeight()+"px"+pictureX+" w "+canvas.getWidth()+"py"+pictureY);
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                movie.draw(canvas, (int) (mXOffset), 0);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);
                movie.setTime((int) (System.currentTimeMillis() % movie.duration()));

                handler.removeCallbacks(drawGIF);
                handler.postDelayed(drawGIF, frameDuration);
            }
        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            handler.removeCallbacks(drawGIF);
        }
    }
    /**
     * For applications that use multiple virtual screens showing a wallpaper,
     * specify the step size between virtual screens. For example, if the
     * launcher has 3 virtual screens, it would specify an xStep of 0.5,
     * since the X offset for those screens are 0.0, 0.5 and 1.0
     * @param xStep The X offset delta from one screen to the next one
     * @param yStep The Y offset delta from one screen to the next one
     */

    public void setWallpaperOffsetSteps(float xStep, float yStep) {
        Log.i(TAG,"Wallpaper scroll offset changed "+xStep);
        mWallpaperXStep = xStep;
        mWallpaperYStep = yStep;
    }
}
