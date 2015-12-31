package br.fapema.morholt.android.wizardpager.wizard.basic;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	public MyViewPager(Context context) {
		super(context);
	}
	
	public MyViewPager(Context context, AttributeSet attrs) {
	    super(context, attrs);
	    setOffscreenPageLimit(1);
	}
	
	private boolean swipeEnabled = true;
	
	public void enableSwipe(boolean enable) {
		swipeEnabled = enable;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    if (swipeEnabled) {
	    	return super.onTouchEvent(event);

	    } else {
	        return false;
	    }
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
	    return super.onInterceptTouchEvent(event);
	}

}
