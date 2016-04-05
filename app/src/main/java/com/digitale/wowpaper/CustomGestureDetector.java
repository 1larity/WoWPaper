package com.digitale.wowpaper;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * kludge to deal with Samsung and HTC devices that do not implement onOffsetsChanged in their launchers
 * Created by Rich on 05/04/2016.
 */
class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

    private static final String TAG ="GESTURE DETECTOR " ;


    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.d(TAG,"onSingleTapConfirmed");
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap");
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.d(TAG, "onDoubleTapEvent");
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.d(TAG, "onDown");
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.d(TAG, "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.d(TAG, "onSingleTapUp");
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.d(TAG, "onScroll");
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
        Log.d(TAG, "onLongPress");
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d(TAG, "onFling");
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
