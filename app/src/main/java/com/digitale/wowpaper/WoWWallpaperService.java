package com.digitale.wowpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

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
    /**
     * width of screen
     */
    static int  screenX;
    /**
     * height of screen
     */
    static int  screenY;
    View.OnTouchListener gestureListener;
    GestureDetector mGestureDetector;
    //number of launcher pages(needed for scrolling wallpaper with launcher)
    static int mNumberOfPages =3;
    public static WoWDatabase db;
    static ArrayList<WoWCharacter> mImages =new ArrayList<>();
static ArrayList<Bitmap> mImageCache=new ArrayList<>();

    @Override
    public WallpaperService.Engine onCreateEngine() {
        db = new WoWDatabase(this);
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

         screenX=getScreenMetrics().getWidth();
         screenY=getScreenMetrics().getHeight();
        GetFeedTask characterListAsyncTask = new GetFeedTask(this,GetFeedTask.IMAGESFORWALLPAPER);
        characterListAsyncTask.execute(GetFeedTask.IMAGESFORWALLPAPER);

            pictureX=740;
            pictureY=550;
            return new WoWWallpaperEngine();

    }

    private class WoWWallpaperEngine extends WallpaperService.Engine {
        private final int frameDuration = 1000;
        private final int imageDuration = 5000;
        //if lancher pages are sliding
        private boolean mSliding=false;
        private SurfaceHolder holder;
        private boolean visible;
        //handler for controlling frame swaps
        private Handler frameHandler;
        //handler for controlling image swaps
        private Handler imageHandler;
        private boolean isOnOffsetsChangedWorking = false;
        private Bitmap mErrorBitmap;
        //the current image id being displayed
        private int mImageNumber;
        /**
         * flag to inform if we need to load/resize a new bitmap to lower cpu use
         */
        private boolean mImageChanged=true;
        private Bitmap mutableBitmap;

        public WoWWallpaperEngine() {
            Drawable myDrawable = getResources().getDrawable(R.drawable.firstaid);
             mErrorBitmap = ((BitmapDrawable) myDrawable).getBitmap();
            imageHandler= new Handler();
            frameHandler = new Handler();

        }
        @Override
        public void onOffsetsChanged (float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
            mSliding=true;
          //calculate number of launcher pages
           mNumberOfPages=(int)(1 / xOffsetStep)+1;
            //if wallpaper is being displayed in LWP picker xOffsetStep is undefined
            //so fix value
            if(mNumberOfPages<=0){
                mNumberOfPages=1;
            }
         //   Log.d(TAG,"Pages= "+ mNumberOfPages);
            if (isOnOffsetsChangedWorking == false && xOffset != 0.0f && xOffset != 0.5f) {
                isOnOffsetsChangedWorking = true;
            }

            if (isOnOffsetsChangedWorking == true) {
              //  Log.d(TAG,"Offset method works! "+xOffset+" "+xPixelOffset+" "+xOffsetStep);
                mXOffset=xOffset;
            }
        }
        //awful hack because HTC and Samsung decided to do their own thing with the launcher
        @Override public void onTouchEvent(MotionEvent event) {
            if (!isOnOffsetsChangedWorking) {
                mGestureDetector.onTouchEvent(event);
            }
        }



        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
            this.holder = surfaceHolder;

        }
        @Override
        public void onVisibilityChanged(boolean visible) {
            Log.d(TAG, "VISIBLE=" + visible);
            this.visible = visible;
            if (visible) {
                imageHandler.post(swapImage);
                frameHandler.post(drawImage);

            } else {
                imageHandler.removeCallbacks(swapImage);
                frameHandler.removeCallbacks(drawImage);
            }
        }
        private Runnable swapImage = new Runnable() {

            public void run() {
                swap();
            }
        };
        private void swap( ) {
            mImageChanged=false;
            int previousImageID=mImageNumber;
            if (visible) {
                //init RNG
                Random rng=new Random();

                //if image cache is not empty
                if(mImageCache.size()>0) {
                    //generate RN between 0 and number of images in the image cache
                    mImageNumber = rng.nextInt(mImageCache.size());
                }else{
                    //Image cache is empty set imagenumber to error state
                    mImageNumber=-1;
                }
                if(previousImageID!=mImageNumber){
                    mImageChanged=true;
                }
                imageHandler.removeCallbacks(swapImage);
                imageHandler.postDelayed(swapImage, imageDuration);
            }
        }

        private Runnable drawImage = new Runnable() {
            Canvas canvas;
            public void run() {
                draw(canvas);
            }
        };
        private void draw(Canvas canvas) {
            if (visible) {

                 canvas = holder.lockCanvas();

                canvas.save();

              //  canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);

                //if imagecache is not empty
                if (mImageNumber>=0) {
                     mutableBitmap = mImageCache.get(mImageNumber);
                } else{
                    mutableBitmap = mErrorBitmap;
                }


                canvas.drawBitmap(mutableBitmap, -((mutableBitmap.getWidth()- screenX) * mXOffset) , 0, null);
                canvas.restore();
                holder.unlockCanvasAndPost(canvas);

//mutableBitmap.recycle();
                frameHandler.removeCallbacks(drawImage);
                //if launcher is sliding, speed up framerate to make scroll smoother
                if(mSliding) {
                    frameHandler.postDelayed(drawImage, 10);
                    //make handler/runnable to restore framerate in 1 second
                    Handler slidingReset=new Handler();
                    frameHandler.postDelayed(drawImage, frameDuration);
                     Runnable sliderReset = new Runnable() {
                            public void run() {
                            mSliding=false;
                        }
                    };
                    //reset sliding flag in 1 second
                slidingReset.postDelayed(sliderReset,1000);
                }else{

                frameHandler.postDelayed(drawImage, frameDuration);
            }
            }

        }
        @Override
        public void onDestroy() {
            super.onDestroy();
            frameHandler.removeCallbacks(drawImage);
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
    //get correct screen metrics regardless of API
    public ScreenMetrics getScreenMetrics (){
        DisplayMetrics metrics = new DisplayMetrics();
        Display display =((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        int realWidth;
        int realHeight;

        if (Build.VERSION.SDK_INT >= 17){
            //new pleasant way to get real metrics
            DisplayMetrics realMetrics = new DisplayMetrics();
            display.getRealMetrics(realMetrics);
            realWidth = realMetrics.widthPixels;
            realHeight = realMetrics.heightPixels;

        } else if (Build.VERSION.SDK_INT >= 14) {
            //reflection for this weird in-between time
            try {
                Method mGetRawH = Display.class.getMethod("getRawHeight");
                Method mGetRawW = Display.class.getMethod("getRawWidth");
                realWidth = (Integer) mGetRawW.invoke(display);
                realHeight = (Integer) mGetRawH.invoke(display);
            } catch (Exception e) {
                //this may not be 100% accurate, but it's all we've got
                realWidth = display.getWidth();
                realHeight = display.getHeight();
                Log.e("Display Info", "Couldn't use reflection to get the real display metrics.");
            }

        } else {
            //This should be close, as lower API devices should not have window navigation bars
            realWidth = display.getWidth();
            realHeight = display.getHeight();
        }
        ScreenMetrics screenMetrics=new ScreenMetrics();
        screenMetrics.setWidth(realWidth);
        screenMetrics.setHeight(realHeight);
    return screenMetrics;
    }




    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        //constain aspect if newwidth/height is 0
        if (newWidth == 0) {
            scaleWidth = scaleHeight;
        } else if (newHeight == 0){
            scaleHeight = scaleWidth;
    }
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
     //   bm.recycle();
        return resizedBitmap;
    }
}
