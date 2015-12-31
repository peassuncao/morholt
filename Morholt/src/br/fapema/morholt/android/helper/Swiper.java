package br.fapema.morholt.android.helper;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class Swiper {

    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;
    private CanSwipe canSwipe;
    
	public void start(Context context, CanSwipe canSwipe) {
		this.canSwipe = canSwipe;
		gestureDetector = new GestureDetector(context, new SwipeGestureDetector(canSwipe));
	    gestureListener = new View.OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            return gestureDetector.onTouchEvent(event);
	        }
	    };	
	    setOnTouchListener();
	}
	
	private void setOnTouchListener() {
		canSwipe.getContainerPanel().setOnTouchListener(gestureListener);
	}
	
}
