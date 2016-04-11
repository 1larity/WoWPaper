package com.digitale.wowpaper;

import android.animation.ValueAnimator;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * kludge to deal with Samsung and HTC devices that do not implement onOffsetsChanged in their launchers
 * Created by Rich on 05/04/2016.
 */
class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private static final String TAG ="GESTURE DETECTOR " ;
    private static boolean localDebug=false;

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Logger.writeLog(TAG,"onSingleTapConfirmed",localDebug);
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Logger.writeLog(TAG, "onDoubleTap",localDebug);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Logger.writeLog(TAG, "onDoubleTapEvent",localDebug);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Logger.writeLog(TAG, "onDown",localDebug);
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Logger.writeLog(TAG, "onShowPress",localDebug);
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Logger.writeLog(TAG, "onSingleTapUp",localDebug);
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Logger.writeLog(TAG, "onScroll",localDebug);
        final float newXOffset = WoWWallpaperService.mXOffset + distanceX / WoWWallpaperService.screenX / WoWWallpaperService.mNumberOfPages;
        if (newXOffset > 1) {
            WoWWallpaperService.mXOffset = 1f;
        } else if (newXOffset < 0) {
            WoWWallpaperService.mXOffset = 0f;
        } else {
            WoWWallpaperService.mXOffset = newXOffset;
        }
        // translate by xOffset;
        return super.onScroll(e1, e2, distanceX, distanceY);
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Logger.writeLog(TAG, "onLongPress",localDebug);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Logger.writeLog(TAG, "onFling",localDebug);
        float endValue = velocityX > 0
                ? (WoWWallpaperService.mXOffset - (WoWWallpaperService.mXOffset % (1 / WoWWallpaperService.mNumberOfPages)))
                : (WoWWallpaperService.mXOffset - (WoWWallpaperService.mXOffset % (1 / WoWWallpaperService.mNumberOfPages)) + (1 / WoWWallpaperService.mNumberOfPages));

        if (endValue < 0f) {
            endValue = 0f;
        } else if (endValue > 1f) {
            endValue = 1f;
        }

        final ValueAnimator compatValueAnimator = ValueAnimator.ofFloat(WoWWallpaperService.mXOffset, endValue);
        compatValueAnimator.setDuration(150);

        compatValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override public void onAnimationUpdate(final ValueAnimator animation) {
                WoWWallpaperService.mXOffset = (float) animation.getAnimatedValue();
                // translate by xOffset;
            }
        });
        compatValueAnimator.start();
        return true;

    }
}
