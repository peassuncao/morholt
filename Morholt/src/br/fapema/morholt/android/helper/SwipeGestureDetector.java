package br.fapema.morholt.android.helper;

import android.util.Log;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

public class SwipeGestureDetector extends SimpleOnGestureListener {
	private static final int SWIPE_MIN_DISTANCE = 50;
    private static final int SWIPE_MAX_OFF_PATH = 200;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;

    private CanSwipe canSwipe;
    public SwipeGestureDetector(CanSwipe canSwipe) {
    	this.canSwipe = canSwipe;
    }
    
    @Override
    public boolean onDown(MotionEvent e) {
    	
        return true;
    }
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
            float velocityY) {
        try {
            //Toast t = Toast.makeText(getActivity(), "Gesture detected", Toast.LENGTH_SHORT);
            //t.show();
            float diffAbs = Math.abs(e1.getY() - e2.getY());
            float diff = e1.getX() - e2.getX();

            if (diffAbs > SWIPE_MAX_OFF_PATH)
                return false;

            // Left swipe
            if (diff > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	canSwipe.onLeftSwipe();
            } 
            // Right swipe
            else if (-diff > SWIPE_MIN_DISTANCE
                    && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
            	canSwipe.onRightSwipe();
            }
        } catch (Exception e) {
            Log.e("Home", "Error on gestures: " + e.getMessage());
            throw new RuntimeException (e);
        }
        return false;
    }
}
